package silvaren.rstoolbox.client;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.Type;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import silvaren.rstoolbox.scripts.ScriptC_channel;
import silvaren.rstoolbox.tools.BitmapRSContext;
import silvaren.rstoolbox.tools.ColorMatrix;

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

        Bitmap yuvImage = ColorMatrix.rgbToYuv(this, sampleBitmap);
        BitmapRSContext bitmapRSContext = BitmapRSContext.createFromBitmap(yuvImage, this);
        ScriptC_channel channelScript = new ScriptC_channel(bitmapRSContext.rs);
        Type outType = Type.createXY(bitmapRSContext.rs, Element.U8(bitmapRSContext.rs),
                yuvImage.getWidth(), yuvImage.getHeight());
        Allocation aout = Allocation.createTyped(bitmapRSContext.rs, outType);
        channelScript.forEach_channelR(bitmapRSContext.ain, aout);
        int size = sampleBitmap.getWidth() * sampleBitmap.getHeight();
        byte[] yByteArray = new byte[size + size / 2];
        aout.copyTo(yByteArray);

//        channelScript.forEach_channelG(bitmapRSContext.ain, gout);

        YuvImage yuvImage2 = new YuvImage(yByteArray, ImageFormat.NV21, yuvImage.getWidth(),
                yuvImage.getHeight(), null);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        yuvImage2.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 100,
                os);
        byte[] jpegBytes = os.toByteArray();
        Bitmap outBitmap = BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.length);

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
//        Lut3D.do3dLut(this, sampleBitmap);


        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(outBitmap);
    }
}
