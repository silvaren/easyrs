package io.github.silvaren.easyrs.tools.base;

import android.graphics.Bitmap;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import io.github.silvaren.easyrs.tools.Histogram;
import io.github.silvaren.easyrs.tools.Nv21Image;

public class Utils {

    public static Nv21Image generateSample() {
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

        return new Nv21Image(nv21ByteArray, width, height);
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
