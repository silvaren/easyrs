package io.github.silvaren.easyrs.tools;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicHistogram;

import io.github.silvaren.easyrs.tools.base.RSToolboxContext;


public class Histogram {

    public static final int COLOR_DEPTH = 256;
    public static final int CHANNELS = 4;

    /**
     * Computes a luminance histogram of a Bitmap.
     * @return a 256-length int array of integer frequencies at luminance level.
     */
    public static int[] luminanceHistogram(RenderScript rs, Bitmap inputBitmap) {
        RSToolboxContext bitmapRSContext = RSToolboxContext.createFromBitmap(rs, inputBitmap);
        Allocation aout = Allocation.createSized(bitmapRSContext.rs, Element.I32(bitmapRSContext.rs),
                COLOR_DEPTH);

        ScriptIntrinsicHistogram histogramScript = ScriptIntrinsicHistogram.create(
                bitmapRSContext.rs, bitmapRSContext.ain.getElement());
        histogramScript.setOutput(aout);
        histogramScript.forEach(bitmapRSContext.ain);

        int[] histogram = new int[COLOR_DEPTH];
        aout.copyTo(histogram);

        return histogram;
    }

    /**
     * Computes a RGBA histogram of a Bitmap.
     * @return a 1024-length int array of integer frequencies at each channel intensity in the
     * following interleaved format [R0,G0,B0,A0,R1,G1,B1,A1...]
     */
    public static int[] rgbaHistograms(RenderScript rs, Bitmap inputBitmap) {
        RSToolboxContext bitmapRSContext = RSToolboxContext.createFromBitmap(rs, inputBitmap);
        Allocation aout = Allocation.createSized(bitmapRSContext.rs, Element.I32_4(bitmapRSContext.rs),
                COLOR_DEPTH);

        ScriptIntrinsicHistogram histogramScript = ScriptIntrinsicHistogram.create(
                bitmapRSContext.rs, bitmapRSContext.ain.getElement());
        histogramScript.setOutput(aout);
        histogramScript.forEach(bitmapRSContext.ain);

        int[] histograms = new int[CHANNELS * COLOR_DEPTH];
        aout.copyTo(histograms);

        // RGBA interleaved: [R0,G0,B0,A0,R1,G1,B1,A1...
        return histograms;
    }

    /**
     * Applies a blur effect to a NV21 image.
     * @param nv21ByteArray the original NV21 byte array.
     * @param width the original NV21 image width.
     * @param height the original NV21 image height.
     * @return a 256-length int array of integer frequencies at luminance level.
     */
    public static int[] luminanceHistogram(RenderScript rs, byte[] nv21ByteArray, int width, int height) {
        Bitmap srcBitmap = Nv21Image.nv21ToBitmap(rs, nv21ByteArray, width, height);
        return luminanceHistogram(rs, srcBitmap);
    }

    /**
     * Applies a blur effect to a NV21 image.
     * @param nv21ByteArray the original NV21 byte array.
     * @param width the original NV21 image width.
     * @param height the original NV21 image height.
     * @return a 1024-length int array of integer frequencies at each channel intensity in the
     * following interleaved format [R0,G0,B0,A0,R1,G1,B1,A1...]
     */
    public static int[] rgbaHistograms(RenderScript rs, byte[] nv21ByteArray, int width, int height) {
        Bitmap srcBitmap = Nv21Image.nv21ToBitmap(rs, nv21ByteArray, width, height);
        return rgbaHistograms(rs, srcBitmap);
    }
}
