package silvaren.com.rswrap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.ScriptIntrinsicHistogram;
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
        Bitmap outputBitmap = Bitmap.createBitmap(256 * 4, 256, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < 256 * 4; x++) {
            for (int y = 0; y < 256; y++) {
                outputBitmap.setPixel(x, y, color);
            }
        }

        return outputBitmap;
    }

    private Bitmap drawHistograms(int[] histograms, int channels) {
        Bitmap outputBitmap = Bitmap.createBitmap(256 * channels, 256, Bitmap.Config.ARGB_8888);

        float[] maxes = new float[channels];
        for (int c = 0; c < channels; c++) {
            int max = 0;
            for (int i = 0; i < 256; i++) {
                max = Math.max(histograms[c + i * channels], max);
            }
            maxes[c] = max;
        }


        for (int x = 0; x < 256 * channels; x++) {
            for (int y = 0; y < 256; y++) {
                int c = x / 256;
                int i = x % 256;
                int height = 255 - (int) (histograms[c + i * channels] * 255.f / maxes[c]);
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
