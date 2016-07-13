package silvaren.com.rswrap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.ScriptIntrinsicResize;
import android.support.v8.renderscript.ScriptIntrinsicYuvToRGB;
import android.support.v8.renderscript.Type;
import android.widget.ImageView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;

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

        int width = 256 * 2;
        int height = 256 * 2;
        int size = width * height;
        byte[] nv21ByteArray = new byte[size + size / 2];
        Arrays.fill(nv21ByteArray, (byte) 127);

        for (int x = 0; x < 256; x++) {
            for (int y = 0; y < 256; y++) {
                nv21ByteArray[size + y * 256 * 2 + x * 2] = (byte) x;
                nv21ByteArray[size + y * 256 * 2 + x * 2 + 1] = (byte) y;
            }
        }

        BitmapRSContext bitmapRSContext = BitmapRSContext.createFromBitmap(sampleBitmap, this);
        Element element = Element.U8_4(bitmapRSContext.rs);
        ScriptIntrinsicYuvToRGB yuvToRgbScript = ScriptIntrinsicYuvToRGB.create(bitmapRSContext.rs, element);
        Type.Builder tb = new Type.Builder(bitmapRSContext.rs, Element.createPixel(bitmapRSContext.rs,
                Element.DataType.UNSIGNED_8, Element.DataKind.PIXEL_YUV));
        tb.setX(width);
        tb.setY(height);
        tb.setYuvFormat(android.graphics.ImageFormat.NV21);
        Allocation yuvAllocation = Allocation.createTyped(bitmapRSContext.rs, tb.create(), Allocation.USAGE_SCRIPT);
        Type rgbType = Type.createXY(bitmapRSContext.rs, Element.U8_4(bitmapRSContext.rs), width, height);
        Allocation rgbAllocation = Allocation.createTyped(bitmapRSContext.rs, rgbType);
        yuvAllocation.copyFrom(nv21ByteArray);
        yuvToRgbScript.setInput(yuvAllocation);
        yuvToRgbScript.forEach(rgbAllocation);
        byte[] rgbArray = new byte[width * height * 4];
        rgbAllocation.copyTo(rgbArray);
        IntBuffer intBuf =
                ByteBuffer.wrap(rgbArray)
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .asIntBuffer();
        int[] array = new int[intBuf.remaining()];
        intBuf.get(array);

        Bitmap outputBitmap = Bitmap.createBitmap(array, width, height, Bitmap.Config.ARGB_8888);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(outputBitmap);
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
