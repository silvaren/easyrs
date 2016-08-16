package silvaren.rstoolbox.client;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.google.common.base.Optional;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.script_spinner)
    AppCompatSpinner spinner;

    @BindView(R.id.imageView)
    ImageView imageView;

    @BindView(R.id.script_flavor_spinner)
    AppCompatSpinner flavorSpinner;

    @BindView(R.id.image_format_seek)
    SeekBar imageFormatSeekBar;

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

    @Override
    protected void onResume() {
        super.onResume();
        sampleBitmap = Optional.of(loadBitmap());

        String selectedItem = (String) spinner.getSelectedItem();
        processImage(selectedItem, ImageProcesses.ImageFormat.BITMAP);
    }

    @OnItemSelected(R.id.script_spinner)
    public void scriptSpinnerListen() {
        String selectedItem = (String) spinner.getSelectedItem();
        updateFlavor(selectedItem);
        processImage(selectedItem, ImageProcesses.ImageFormat.valueOf(imageFormatSeekBar.getProgress()));
    }

    @OnItemSelected(R.id.script_flavor_spinner)
    public void flavorSpinnerListen() {
        String selectedItem = (String) flavorSpinner.getSelectedItem();
        processImage(selectedItem, ImageProcesses.ImageFormat.valueOf(imageFormatSeekBar.getProgress()));
    }

    private void updateFlavor(String selectedItem) {
        Map<String, Integer> flavorMap = ImageProcesses.flavorMap(this);
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

    private Bitmap loadBitmap() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        options.inDither = false;
        options.inPurgeable = true;
        return BitmapFactory.decodeResource(getResources(), R.drawable.sample, options);
    }

    private void processImage(String selectedItem, ImageProcesses.ImageFormat imageFormat) {
        if (!sampleBitmap.isPresent())
            sampleBitmap = Optional.of(loadBitmap());

        Optional<ImageProcesses.ImageProcess> imageProcess = Optional.fromNullable(
                ImageProcesses.processMap(this).get(selectedItem));
        if (imageProcess.isPresent()) {
            Bitmap processedBitmap = imageProcess.get().processImage(this, sampleBitmap.get(), imageFormat);
            imageView.setImageBitmap(processedBitmap);
        }
    }
}
