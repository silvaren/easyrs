package silvaren.rstoolbox.tools;

import android.graphics.Bitmap;

public class Utils {

    public static Bitmap drawColorBitmap(Bitmap sampleBitmap, int color) {
        Bitmap outputBitmap = Bitmap.createBitmap(Constants.COLOR_DEPTH, Constants.COLOR_DEPTH,
                Bitmap.Config.ARGB_8888);
        for (int x = 0; x < Constants.COLOR_DEPTH; x++) {
            for (int y = 0; y < Constants.COLOR_DEPTH; y++) {
                int c = 0xFF000000;
                c |= x << 0;
                c |= x << 8;
                c |= x << 16;
                outputBitmap.setPixel(x, y, c);
            }
        }

        return outputBitmap;
    }

    public static Bitmap drawHistograms(int[] histograms, int channels) {
        Bitmap outputBitmap = Bitmap.createBitmap(Constants.COLOR_DEPTH * channels,
                Constants.COLOR_DEPTH, Bitmap.Config.ARGB_8888);

        float[] maxes = new float[channels];
        for (int c = 0; c < channels; c++) {
            int max = 0;
            for (int i = 0; i < Constants.COLOR_DEPTH; i++) {
                max = Math.max(histograms[c + i * channels], max);
            }
            maxes[c] = max;
        }

        for (int y = 0; y < Constants.COLOR_DEPTH; y++) {
            for (int x = 0; x < Constants.COLOR_DEPTH * channels; x++) {
                int c = x / Constants.COLOR_DEPTH;
                int i = x % Constants.COLOR_DEPTH;
                int height = (int) ((histograms[c + i * channels] / maxes[c]) * (Constants.COLOR_DEPTH - 1.f));
                int color = y < height? 0xFF000000 : 0xFFFFFFFF;
                outputBitmap.setPixel(x, Constants.COLOR_DEPTH - 1 -y, color);
            }
        }

        return outputBitmap;
    }
}
