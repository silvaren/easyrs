package silvaren.rstoolbox.client;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

        Nv21Image nv21Image = Nv21Image.convertToNV21(this, sampleBitmap);

        Bitmap outBitmap = Nv21Image.nv21ToBitmap(sampleBitmap, nv21Image.nv21ByteArray);

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
