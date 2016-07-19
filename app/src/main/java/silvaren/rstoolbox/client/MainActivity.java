package silvaren.rstoolbox.client;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.support.v8.renderscript.Type;
import android.widget.ImageView;

import silvaren.rstoolbox.tools.Nv21Image;

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
//        Lut3D.do3dLut(this, sampleBitmap);
        Nv21Image nv21Image = Nv21Image.convertToNV21(this, sampleBitmap);

        RenderScript rs = RenderScript.create(this);
        Type.Builder tb = new Type.Builder(rs, Element.createPixel(rs,
                Element.DataType.UNSIGNED_8, Element.DataKind.PIXEL_YUV));
        tb.setX(nv21Image.width);
        tb.setY(nv21Image.height);
        tb.setYuvFormat(android.graphics.ImageFormat.NV21);
        Allocation yuvAllocation = Allocation.createTyped(rs, tb.create(), Allocation.USAGE_SCRIPT);
        yuvAllocation.copyFrom(nv21Image.nv21ByteArray);

        Allocation aout = Allocation.createTyped(rs, yuvAllocation.getType());

        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(
                rs,
                yuvAllocation.getElement());
        blurScript.setInput(yuvAllocation);
        blurScript.setRadius(25.f);
        blurScript.forEach(aout);
        aout.copyTo(nv21Image.nv21ByteArray);
        Bitmap outBitmap = Nv21Image.nv21ToBitmap(sampleBitmap, nv21Image.nv21ByteArray);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(outBitmap);
    }
}
