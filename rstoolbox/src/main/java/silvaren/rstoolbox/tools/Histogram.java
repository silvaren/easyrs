package silvaren.rstoolbox.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.ScriptIntrinsicHistogram;


class Histogram {

    public static int[] luminanceHistogram(Context context, Bitmap inputBitmap) {
        BitmapRSContext bitmapRSContext = BitmapRSContext.createFromBitmap(inputBitmap, context);
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

    public static int[] rgbaHistograms(Context context, Bitmap inputBitmap) {
        BitmapRSContext bitmapRSContext = BitmapRSContext.createFromBitmap(inputBitmap, context);
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
}
