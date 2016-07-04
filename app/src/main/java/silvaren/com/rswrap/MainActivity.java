package silvaren.com.rswrap;

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
//        Bitmap sampleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample);

        RenderScript rs = RenderScript.create(this);
        Allocation ain = Allocation.createFromBitmap(rs, sampleBitmap);
        Element bitmapElement = ain.getElement();
        Allocation aout = Allocation.createTyped(rs, ain.getType());

        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, bitmapElement);
        blurScript.setInput(ain);
        blurScript.setRadius(25.f);
        blurScript.forEach(aout);

        aout.copyTo(sampleBitmap);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(sampleBitmap);
    }
}
