package silvaren.rstoolbox.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.ScriptIntrinsicHistogram;

import hugo.weaving.DebugLog;


public class Histogram {

    @DebugLog
    public static int[] luminanceHistogram(Context context, Bitmap inputBitmap) {
        RSToolboxContext bitmapRSContext = RSToolboxContext.createFromBitmap(context, inputBitmap);
        Allocation aout = Allocation.createSized(bitmapRSContext.rs, Element.I32(bitmapRSContext.rs),
                Constants.COLOR_DEPTH);

        ScriptIntrinsicHistogram histogramScript = ScriptIntrinsicHistogram.create(
                bitmapRSContext.rs, bitmapRSContext.ain.getElement());
        histogramScript.setOutput(aout);
        histogramScript.forEach(bitmapRSContext.ain);

        int[] histogram = new int[Constants.COLOR_DEPTH];
        aout.copyTo(histogram);

        return histogram;
    }

    @DebugLog
    public static int[] rgbaHistograms(Context context, Bitmap inputBitmap) {
        RSToolboxContext bitmapRSContext = RSToolboxContext.createFromBitmap(context, inputBitmap);
        Allocation aout = Allocation.createSized(bitmapRSContext.rs, Element.I32_4(bitmapRSContext.rs),
                Constants.COLOR_DEPTH);

        ScriptIntrinsicHistogram histogramScript = ScriptIntrinsicHistogram.create(
                bitmapRSContext.rs, bitmapRSContext.ain.getElement());
        histogramScript.setOutput(aout);
        histogramScript.forEach(bitmapRSContext.ain);

        int[] histograms = new int[Constants.CHANNELS * Constants.COLOR_DEPTH];
        aout.copyTo(histograms);

        // RGBA interleaved: [R0,G0,B0,A0,R1,G1,B1,A1...
        return histograms;
    }

    @DebugLog
    public static int[] rgbaHistograms(Context context, byte[] nv21ByteArray, int width, int height) {
        Bitmap srcBitmap = Nv21Image.nv21ToBitmap(context, nv21ByteArray, width, height);
        return rgbaHistograms(context, srcBitmap);
    }

    @DebugLog
    public static int[] luminanceHistogram(Context context, byte[] nv21ByteArray, int width, int height) {
        Bitmap srcBitmap = Nv21Image.nv21ToBitmap(context, nv21ByteArray, width, height);
        return luminanceHistogram(context, srcBitmap);
    }
}
