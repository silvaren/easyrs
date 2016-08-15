package silvaren.rstoolbox.client;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.google.common.base.Optional;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import silvaren.rstoolbox.tools.Blend;
import silvaren.rstoolbox.tools.Blur;
import silvaren.rstoolbox.tools.ColorMatrix;
import silvaren.rstoolbox.tools.Convolve;
import silvaren.rstoolbox.tools.ConvolveParams;
import silvaren.rstoolbox.tools.Histogram;
import silvaren.rstoolbox.tools.Lut;
import silvaren.rstoolbox.tools.Lut3D;
import silvaren.rstoolbox.tools.Resize;
import silvaren.rstoolbox.tools.Utils;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.script_spinner)
    AppCompatSpinner spinner;

    @BindView(R.id.imageView)
    ImageView imageView;

    @BindView(R.id.script_flavor_spinner)
    AppCompatSpinner flavorSpinner;

    private Optional<Bitmap> sampleBitmap = Optional.absent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tools_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @OnItemSelected(R.id.script_spinner)
    public void scriptSpinnerListen() {
        String selectedItem = (String) spinner.getSelectedItem();
        updateFlavor(selectedItem);
        processImage(selectedItem);
    }

    @OnItemSelected(R.id.script_flavor_spinner)
    public void flavorSpinnerListen() {
        String selectedItem = (String) flavorSpinner.getSelectedItem();
        processImage(selectedItem);
    }

    private void updateFlavor(String selectedItem) {
        Map<String, Integer> flavorMap = flavorMap();
        Optional<Integer> stringArrayId = Optional.fromNullable(flavorMap.get(selectedItem));
        if (stringArrayId.isPresent()) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    stringArrayId.get(), android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            flavorSpinner.setAdapter(adapter);
            flavorSpinner.setVisibility(View.VISIBLE);
        } else {
            flavorSpinner.setVisibility(View.GONE);
        }
    }

    private Map<String, Integer> flavorMap() {
        HashMap<String, Integer> flavorMap = new HashMap<>();
        flavorMap.put(getString(R.string.colormatrix), R.array.colormatrix_array);
        flavorMap.put(getString(R.string.convolve), R.array.convolve_array);
        return flavorMap;
    }

    interface ImageProcess {
        Bitmap processImage(Context context, Bitmap bitmap);
    }

    private ImageProcess blendProcess = new ImageProcess() {
        @Override
        public Bitmap processImage(Context context, Bitmap bitmap) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            options.inDither = false;
            options.inPurgeable = true;
            Bitmap sampleEdgeBitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.sample_edge, options);
            Blend.add(context, bitmap, sampleEdgeBitmap);
            return sampleEdgeBitmap;
        }
    };

    private ImageProcess blurProcess = new ImageProcess() {
        @Override
        public Bitmap processImage(Context context, Bitmap bitmap) {
            return Blur.blur(context, bitmap, 25.f);
        }
    };

    private ImageProcess colorMatrixRgbtoYuvProcess = new ImageProcess() {
        @Override
        public Bitmap processImage(Context context, Bitmap bitmap) {
            return ColorMatrix.rgbToYuv(context, bitmap);
        }
    };

    private ImageProcess colorMatrixGraycaleProcess = new ImageProcess() {
        @Override
        public Bitmap processImage(Context context, Bitmap bitmap) {
            return ColorMatrix.convertToGrayScale(context, bitmap);
        }
    };

    private ImageProcess convolveSobel3x3Process = new ImageProcess() {
        @Override
        public Bitmap processImage(Context context, Bitmap bitmap) {
            return Convolve.convolve3x3(context, bitmap, ConvolveParams.Kernels5x5.SOBEL_X);
        }
    };

    private ImageProcess convolveSobel5x5Process = new ImageProcess() {
        @Override
        public Bitmap processImage(Context context, Bitmap bitmap) {
            return Convolve.convolve5x5(context, bitmap, ConvolveParams.Kernels5x5.SOBEL_X);
        }
    };

    private ImageProcess histogramProcess = new ImageProcess() {
        @Override
        public Bitmap processImage(Context context, Bitmap bitmap) {
            int[] histograms = Histogram.rgbaHistograms(context, bitmap);
            return Utils.drawHistograms(histograms, 4);
        }
    };

    private ImageProcess lutProcess = new ImageProcess() {
        @Override
        public Bitmap processImage(Context context, Bitmap bitmap) {
            return Lut.negativeEffect(context, bitmap);
        }
    };

    private ImageProcess lut3dProcess = new ImageProcess() {
        @Override
        public Bitmap processImage(Context context, Bitmap bitmap) {
            return Lut3D.do3dLut(context, bitmap);
        }
    };

    private ImageProcess resizeProcess = new ImageProcess() {
        @Override
        public Bitmap processImage(Context context, Bitmap bitmap) {
            return Resize.resize(context, bitmap, 50, 50);
        }
    };

    private Map<String, ImageProcess> processMap() {
        HashMap<String, ImageProcess> processMap = new HashMap<>();
        processMap.put(getString(R.string.blend), blendProcess);
        processMap.put(getString(R.string.blur), blurProcess);
        processMap.put(getString(R.string.grayscale), colorMatrixGraycaleProcess);
        processMap.put(getString(R.string.rgbtoyuv), colorMatrixRgbtoYuvProcess);
        processMap.put(getString(R.string.sobel3x3), convolveSobel3x3Process);
        processMap.put(getString(R.string.sobel5x5), convolveSobel5x5Process);
        processMap.put(getString(R.string.histogram), histogramProcess);
        processMap.put(getString(R.string.lut), lutProcess);
        processMap.put(getString(R.string.lut3d), lut3dProcess);
        processMap.put(getString(R.string.resize), resizeProcess);
        return processMap;
    }

    private Bitmap loadBitmap() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        options.inDither = false;
        options.inPurgeable = true;
        return BitmapFactory.decodeResource(getResources(), R.drawable.sample, options);
    }

    private void processImage(String selectedItem) {
        if (!sampleBitmap.isPresent())
            sampleBitmap = Optional.of(loadBitmap());

        Optional<ImageProcess> imageProcess = Optional.fromNullable(processMap().get(selectedItem));
        if (imageProcess.isPresent())
            imageView.setImageBitmap(imageProcess.get().processImage(this, sampleBitmap.get()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        sampleBitmap = Optional.of(loadBitmap());
//        Bitmap sampleEdgeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_edge, options);

        String selectedItem = (String) spinner.getSelectedItem();
        processImage(selectedItem);

//        int[] histogram = Histogram.luminanceHistogram(this, sampleBitmap);
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
}
