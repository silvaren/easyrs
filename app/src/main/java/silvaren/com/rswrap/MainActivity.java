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
import android.support.v8.renderscript.ScriptIntrinsicResize;
import android.support.v8.renderscript.Type;
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

//        Bitmap blurredBitmap = blur(sampleBitmap, 25.f, this);
        Bitmap resizedBitmap = resize(this, sampleBitmap, 50, 50);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(resizedBitmap);
    }

    private Bitmap resize(Context context, Bitmap inputBitmap, int width, int height) {
        BitmapRSContext bitmapRSContext = BitmapRSContext.createFromBitmap(inputBitmap, context);
        Bitmap.Config config = inputBitmap.getConfig();
        Bitmap outputBitmap = Bitmap.createBitmap(width, height, config);
        Type outType = Type.createXY(bitmapRSContext.rs, bitmapRSContext.ain.getElement(), width,
                height);
        Allocation aout = Allocation.createTyped(bitmapRSContext.rs, outType);

        ScriptIntrinsicResize resizeScript = ScriptIntrinsicResize.create(bitmapRSContext.rs);
        resizeScript.setInput(bitmapRSContext.ain);
        resizeScript.forEach_bicubic(aout);

        aout.copyTo(outputBitmap);
        return outputBitmap;
    }

    static class BitmapRSContext {
        public final RenderScript rs;
        public final Allocation ain;
        public final Element bitmapElement;

        private BitmapRSContext(RenderScript rs, Allocation ain, Element bitmapElement) {
            this.rs = rs;
            this.ain = ain;
            this.bitmapElement = bitmapElement;
        }

        public static BitmapRSContext createFromBitmap(Bitmap bitmap, Context context) {
            RenderScript rs = RenderScript.create(context);
            Allocation ain = Allocation.createFromBitmap(rs, bitmap);
            Element bitmapElement = ain.getElement();

            return new BitmapRSContext(rs, ain, bitmapElement);
        }
    }

    private void blurInPlace(Context context, Bitmap bitmap, float radius) {
        blur(context, bitmap, bitmap, radius);
    }

    private Bitmap blur(Context context, Bitmap inputBitmap, float radius) {
        Bitmap.Config config = inputBitmap.getConfig();
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap.getWidth(), inputBitmap.getHeight(),
                config);
        blur(context, inputBitmap, outputBitmap, radius);
        return outputBitmap;
    }

    private void blur(Context context, Bitmap inputBitmap, Bitmap outputBitmap, float radius) {
        BitmapRSContext bitmapRSContext = BitmapRSContext.createFromBitmap(inputBitmap, context);
        Allocation aout = Allocation.createTyped(bitmapRSContext.rs, bitmapRSContext.ain.getType());

        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(
                bitmapRSContext.rs,
                bitmapRSContext.bitmapElement);
        blurScript.setInput(bitmapRSContext.ain);
        blurScript.setRadius(radius);
        blurScript.forEach(aout);

        aout.copyTo(outputBitmap);
    }
}
