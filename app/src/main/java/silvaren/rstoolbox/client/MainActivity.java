package silvaren.rstoolbox.client;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.google.common.base.Optional;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import hugo.weaving.DebugLog;
import silvaren.rstoolbox.tools.Blur;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.script_spinner)
    AppCompatSpinner spinner;

    @BindView(R.id.imageView)
    ImageView imageView;

    private Optional<Bitmap> sampleBitmap = Optional.absent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tools_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    @OnItemSelected(R.id.script_spinner)
    public void submit() {
        String selectedItem = (String) spinner.getSelectedItem();
        if (!sampleBitmap.isPresent())
            sampleBitmap = Optional.of(loadBitmap());
        processImage(sampleBitmap.get(), selectedItem);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sampleBitmap = Optional.of(loadBitmap());
//        Bitmap sampleEdgeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_edge, options);

        String selectedItem = (String) spinner.getSelectedItem();
        processImage(sampleBitmap.get(), selectedItem);


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

//        Nv21Image nv21Image = Nv21Image.convertToNV21(this, sampleBitmap);
//        Blur.blurInPlace(this, nv21Image.nv21ByteArray, nv21Image.width, nv21Image.height, 25.f);
//        Nv21Image sampleEdgeNv21 = Nv21Image.convertToNV21(this, sampleEdgeBitmap);
//        byte[] result = Blend.add(this, nv21Image.nv21ByteArray, nv21Image.width, nv21Image.height,
//                sampleEdgeNv21.nv21ByteArray);
//
//        Convolve.convolve5x5InPlace(this, nv21Image.nv21ByteArray,
//                nv21Image.width, nv21Image.height, Convolve.Kernels5x5.SOBEL_X);
//        int[] histograms = Histogram.rgbaHistograms(this, colorBitmap);

//        Bitmap colorBitmap = Utils.drawColorBitmap(sampleBitmap, 0xFF000000);
//        Nv21Image colorNv21Image = Nv21Image.convertToNV21(this, sampleBitmap);
//        int[] histograms = Histogram.rgbaHistograms(this, colorNv21Image.nv21ByteArray, colorNv21Image.width, colorNv21Image.height);
//        int[] histogram = Histogram.luminanceHistogram(this, colorNv21Image.nv21ByteArray, colorNv21Image.width, colorNv21Image.height);
//        Bitmap histogramsBitmap = Utils.drawHistograms(histograms, 4);
//        Bitmap histogramBitmap = Utils.drawHistograms(histogram, 1);

//        byte[] result = ColorMatrix.doConvertToGrayScale(this, nv21Image.nv21ByteArray,
//                nv21Image.width, nv21Image.height);
//        byte[] result = Lut.negativeEffect(this, nv21Image.nv21ByteArray,
//                nv21Image.width, nv21Image.height);
//        Lut3D.do3dLut(this, nv21Image.nv21ByteArray,
//                nv21Image.width, nv21Image.height);
//        byte[] result = Resize.resize(this, nv21Image.nv21ByteArray, nv21Image.width, nv21Image.height, 50, 50);
//        Bitmap outBitmap = Nv21Image.nv21ToBitmap(this, result, 50, 50);

//        ImageView imageView = (ImageView) findViewById(R.id.imageView);
//        imageView.setImageBitmap(outBitmap);
    }

    private Bitmap loadBitmap() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        options.inDither = false;
        options.inPurgeable = true;
        return BitmapFactory.decodeResource(getResources(), R.drawable.sample, options);
    }

    private void processImage(Bitmap sampleBitmap, String selectedItem) {
        if (selectedItem.equals(getString(R.string.blur))) {
            Bitmap outputBitmap = Blur.blur(this, sampleBitmap, 25.f);
            imageView.setImageBitmap(outputBitmap);
        }
    }
}
