package silvaren.com.rswrap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.ScriptIntrinsicColorMatrix;
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
//        int[] histogram = Histogram.luminanceHistogram(this, sampleBitmap);
//        Bitmap histogramsBitmap = drawHistograms(histograms, 4);
//        Bitmap histogramBitmap = drawHistograms(histogram, 1);
//        Nv21Image nv21Image = Nv21Image.generateSample();
//        Bitmap outputBitmap = YuvToRgb.yuvToRgb(this, nv21Image);

        ColorMatrix.convertToGrayscaleInPlace(this, sampleBitmap);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(sampleBitmap);
    }

    static class ColorMatrix {

        public static void convertToGrayscaleInPlace(Context context, Bitmap bitmap) {
            doConvertToGrayscale(context, bitmap, bitmap);
        }

        public static Bitmap doConvertToGrayScale(Context context, Bitmap inputBitmap) {
            Bitmap.Config config = inputBitmap.getConfig();
            Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap.getWidth(), inputBitmap.getHeight(),
                    config);
            doConvertToGrayscale(context, inputBitmap, outputBitmap);
            return outputBitmap;
        }

        private static void doConvertToGrayscale(Context context, Bitmap inputBitmap, Bitmap outputBitmap) {
            BitmapRSContext bitmapRSContext = BitmapRSContext.createFromBitmap(inputBitmap, context);
            Allocation aout = Allocation.createTyped(bitmapRSContext.rs, bitmapRSContext.ain.getType());

            ScriptIntrinsicColorMatrix colorMatrixScript = ScriptIntrinsicColorMatrix.create(
                    bitmapRSContext.rs, bitmapRSContext.ain.getElement());
            colorMatrixScript.setGreyscale();
            colorMatrixScript.forEach(bitmapRSContext.ain, aout);

            aout.copyTo(outputBitmap);
        }
    }

    static class Resize {
        public static Bitmap resize(Context context, Bitmap inputBitmap, int width, int height) {
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

}
