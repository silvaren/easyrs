package silvaren.com.rswrap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.ScriptIntrinsicConvolve5x5;
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
        Bitmap sampleEdgeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_edge, options);

//        Bitmap blurredBitmap = blur(sampleBitmap, 25.f, this);
//        Bitmap resizedBitmap = resize(this, sampleBitmap, 50, 50);
//        Blend.add(this, sampleBitmap, sampleEdgeBitmap);
//        float[] coefficients5x5 = {
//                1.f, 2.f, 0.f,  -2.f, -1.f,
//                4.f, 8.f, 0.f,  -8.f, -4.f,
//                6.f, 12.f, 0.f, -12.f, -6.f,
//                4.f, 8.f, 0.f,  -8.f, -4.f,
//                1.f, 2.f, 0.f,  -2.f, -1.f};
        float[] f = new float[25];
//        coefficients5x5[12] = 1.5f;
        f[0] = -0.5f;
        f[12] = 1.f;
        f[24] = -0.5f;
//        f[0] = -1.f; f[1] = -3.f; f[2] = -4.f; f[3] = -3.f; f[4] = -1.f;
//        f[5] = -3.f; f[6] =  0.f; f[7] =  6.f; f[8] =  0.f; f[9] = -3.f;
//        f[10]= -4.f; f[11]=  6.f; f[12]= 20.f; f[13]=  6.f; f[14]= -4.f;
//        f[15]= -3.f; f[16]=  0.f; f[17]=  6.f; f[18]=  0.f; f[19]= -3.f;
//        f[20]= -1.f; f[21]= -3.f; f[22]= -4.f; f[23]= -3.f; f[24]= -1.f;


        Bitmap convolved5x5Bitmap = Convolve.convolve5x5(this, sampleBitmap, f);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(convolved5x5Bitmap);
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

}
