package com.github.silvaren.rstoolbox.tools.rswrap;

import android.graphics.Bitmap;

public class Utils {

    private Bitmap drawColorBitmap(Bitmap sampleBitmap, int color) {
        Bitmap outputBitmap = Bitmap.createBitmap(Constants.COLOR_DEPTH, Constants.COLOR_DEPTH,
                Bitmap.Config.ARGB_8888);
        for (int x = 0; x < Constants.COLOR_DEPTH; x++) {
            for (int y = 0; y < Constants.COLOR_DEPTH; y++) {
                outputBitmap.setPixel(x, y, color);
            }
        }

        return outputBitmap;
    }

    private Bitmap drawHistograms(int[] histograms, int channels) {
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


        for (int x = 0; x < Constants.COLOR_DEPTH * channels; x++) {
            for (int y = 0; y < Constants.COLOR_DEPTH; y++) {
                int c = x / Constants.COLOR_DEPTH;
                int i = x % Constants.COLOR_DEPTH;
                int height = Constants.COLOR_DEPTH - 1 - (int) (histograms[c + i * channels] *
                        (Constants.COLOR_DEPTH - 1.f) / maxes[c]);
                int color = y > height? 0xFF000000 : 0xFFFFFFFF;
                outputBitmap.setPixel(x, y, color);
            }
        }

        return outputBitmap;
    }
}
