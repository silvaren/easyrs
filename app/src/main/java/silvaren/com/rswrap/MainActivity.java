package silvaren.com.rswrap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsic3DLUT;
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
//        ColorMatrix.convertToGrayscaleInPlace(this, sampleBitmap);
//        Bitmap mappedBitmap = Lut.negativeEffect(this, sampleBitmap);
        do3dLut(this, sampleBitmap);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(sampleBitmap);
    }

    private static Allocation initSampleCube(RenderScript rs) {
        final int sx = 2;
        final int sy = 2;
        final int sz = 2;
        Type.Builder tb = new Type.Builder(rs, Element.U8_4(rs));
        tb.setX(sx);
        tb.setY(sy);
        tb.setZ(sz);
        Type t = tb.create();
        Allocation cube = Allocation.createTyped(rs, t);
        int dat[] = new int[sx * sy * sz];
        dat[0] = 0xffffffff;
        dat[7] = 0xff000000;
        cube.copyFromUnchecked(dat);

        return cube;
    }

    public static void do3dLut(Context context, Bitmap inputBitmap) {
        BitmapRSContext bitmapRSContext = BitmapRSContext.createFromBitmap(inputBitmap, context);
        Allocation aout = Allocation.createTyped(bitmapRSContext.rs, bitmapRSContext.ain.getType());

        ScriptIntrinsic3DLUT script3dLut = ScriptIntrinsic3DLUT.create(
                bitmapRSContext.rs, bitmapRSContext.ain.getElement());
        Allocation lut = initSampleCube(bitmapRSContext.rs);
        script3dLut.setLUT(lut);
        script3dLut.forEach(bitmapRSContext.ain, aout);
        aout.copyTo(inputBitmap);
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
