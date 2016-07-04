package silvaren.com.rswrap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        options.inDither = false;
        options.inPurgeable = true;
        Bitmap sampleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample, options);

        Bitmap blurredBitmap = blur(sampleBitmap, 25.f, this);


        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(blurredBitmap);
    }

    static class BitmapRSContext {
        public final RenderScript rs;
        public final Allocation ain;
        public final Element bitmapElement;
        public final Allocation aout;

        private BitmapRSContext(RenderScript rs, Allocation ain, Element bitmapElement, Allocation aout) {
            this.rs = rs;
            this.ain = ain;
            this.bitmapElement = bitmapElement;
            this.aout = aout;
        }

        public static BitmapRSContext createFromBitmap(Bitmap bitmap, Context context) {
            RenderScript rs = RenderScript.create(context);
            Allocation ain = Allocation.createFromBitmap(rs, bitmap);
            Element bitmapElement = ain.getElement();
            Allocation aout = Allocation.createTyped(rs, ain.getType());

            return new BitmapRSContext(rs, ain, bitmapElement, aout);
        }
    }

    private void blurInPlace(Bitmap bitmap, float radius, Context context) {
        blur(bitmap, bitmap, radius, context);
    }

    private Bitmap blur(Bitmap inputBitmap, float radius, Context context) {
        Bitmap.Config config = inputBitmap.getConfig();
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap.getWidth(), inputBitmap.getHeight(),
                config);
        blur(inputBitmap, outputBitmap, radius, context);
        return outputBitmap;
    }

    private void blur(Bitmap inputBitmap, Bitmap outputBitmap, float radius, Context context) {
        BitmapRSContext bitmapRSContext = BitmapRSContext.createFromBitmap(inputBitmap, context);

        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(
                bitmapRSContext.rs,
                bitmapRSContext.bitmapElement);
        blurScript.setInput(bitmapRSContext.ain);
        blurScript.setRadius(radius);
        blurScript.forEach(bitmapRSContext.aout);

        bitmapRSContext.aout.copyTo(outputBitmap);
    }
}
