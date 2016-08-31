package io.github.silvaren.easyrs.tools.base;

import android.graphics.Bitmap;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import io.github.silvaren.easyrs.tools.Histogram;

public class Utils {

    public static Bitmap drawColorBitmap(Bitmap sampleBitmap, int color) {
        Bitmap outputBitmap = Bitmap.createBitmap(Histogram.COLOR_DEPTH, Histogram.COLOR_DEPTH,
                Bitmap.Config.ARGB_8888);
        for (int x = 0; x < Histogram.COLOR_DEPTH; x++) {
            for (int y = 0; y < Histogram.COLOR_DEPTH; y++) {
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
        Bitmap outputBitmap = Bitmap.createBitmap(Histogram.COLOR_DEPTH * channels,
                Histogram.COLOR_DEPTH, Bitmap.Config.ARGB_8888);

        float[] maxes = new float[channels];
        for (int c = 0; c < channels; c++) {
            int max = 0;
            for (int i = 0; i < Histogram.COLOR_DEPTH; i++) {
                max = Math.max(histograms[c + i * channels], max);
            }
            maxes[c] = max;
        }

        for (int y = 0; y < Histogram.COLOR_DEPTH; y++) {
            for (int x = 0; x < Histogram.COLOR_DEPTH * channels; x++) {
                int c = x / Histogram.COLOR_DEPTH;
                int i = x % Histogram.COLOR_DEPTH;
                int height = (int) ((histograms[c + i * channels] / maxes[c]) * (Histogram.COLOR_DEPTH - 1.f));
                int color = y < height? 0xFF000000 : 0xFFFFFFFF;
                outputBitmap.setPixel(x, Histogram.COLOR_DEPTH - 1 -y, color);
            }
        }

        return outputBitmap;
    }

    public static double meanSquareErrorRgb8888(int[] a, int[] b) {
        byte[] aBytes = intArrayAsByteArray(a);
        byte[] bBytes = intArrayAsByteArray(b);

        return meanSquareErrorFromBytes(aBytes, bBytes);
    }

    private static byte[] intArrayAsByteArray(int[] a) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(a.length * 4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(a);
        return byteBuffer.array();
    }

    public static double meanSquareErrorFromBytes(byte[] a, byte[] b) {
        double sum_sq = 0;

        for (int i = 0; i < a.length; i++)
        {
            int err = b[i] & 0xff - a[i] & 0xff;
            sum_sq += (err * err);
        }
        double mse = sum_sq / (a.length);
        return mse;
    }
}
