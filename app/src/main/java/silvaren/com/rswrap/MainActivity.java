package silvaren.com.rswrap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v8.renderscript.Allocation;
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
//        Bitmap convolved5x5Bitmap = Convolve.convolve5x5(this, sampleBitmap, Convolve.Kernels5x5.SOBEL_X);
//        Bitmap colorBitmap = drawColorBitmap(sampleBitmap, 0xFFFF0000);
//        int[] histograms = Histogram.rgbaHistograms(this, colorBitmap);
        int[] histogram = Histogram.luminanceHistogram(this, sampleBitmap);
//        Bitmap histogramsBitmap = drawHistograms(histograms, 4);
        Bitmap histogramBitmap = drawHistograms(histogram, 1);


        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(histogramBitmap);
    }

    private Bitmap drawColorBitmap(Bitmap sampleBitmap, int color) {
        Bitmap outputBitmap = Bitmap.createBitmap(Constants.COLOR_DEPTH, Constants.COLOR_DEPTH,
                Bitmap.Config.ARGB_8888);
        for (int x = 0; x < Constants.COLOR_DEPTH; x++) {
            for (int y = 0; y < Constants.COLOR_DEPTH; y++) {
                outputBitmap.setPixel(x, y, color);
            }
        }

        return outputBitmap;
    }

    private Bitmap drawHistograms(int[] histograms, int channels) {
        Bitmap outputBitmap = Bitmap.createBitmap(Constants.COLOR_DEPTH * channels,
                Constants.COLOR_DEPTH, Bitmap.Config.ARGB_8888);

        float[] maxes = new float[channels];
        for (int c = 0; c < channels; c++) {
            int max = 0;
            for (int i = 0; i < Constants.COLOR_DEPTH; i++) {
                max = Math.max(histograms[c + i * channels], max);
            }
            maxes[c] = max;
        }


        for (int x = 0; x < Constants.COLOR_DEPTH * channels; x++) {
            for (int y = 0; y < Constants.COLOR_DEPTH; y++) {
                int c = x / Constants.COLOR_DEPTH;
                int i = x % Constants.COLOR_DEPTH;
                int height = Constants.COLOR_DEPTH - 1 - (int) (histograms[c + i * channels] *
                        (Constants.COLOR_DEPTH - 1.f) / maxes[c]);
                int color = y > height? 0xFF000000 : 0xFFFFFFFF;
                outputBitmap.setPixel(x, y, color);
            }
        }

        return outputBitmap;
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
