package silvaren.com.rswrap;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Script;
import android.support.v8.renderscript.ScriptIntrinsicConvolve5x5;

class Convolve {

    public static void convolve5x5InPlace(Context context, Bitmap bitmap, float[] coefficients) {
        doConvolve5x5(context, bitmap, bitmap, coefficients);
    }

    public static Bitmap convolve5x5(Context context, Bitmap inputBitmap, float[] coefficients) {
        Bitmap.Config config = inputBitmap.getConfig();
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap.getWidth(), inputBitmap.getHeight(),
                config);
        doConvolve5x5(context, inputBitmap, outputBitmap, coefficients);
        return outputBitmap;
    }

    private static void doConvolve5x5(Context context, Bitmap inputBitmap, Bitmap outputBitmap,
                                      float[] coefficients) {
        BitmapRSContext bitmapRSContext = BitmapRSContext.createFromBitmap(inputBitmap, context);
        Allocation aout = Allocation.createTyped(bitmapRSContext.rs, bitmapRSContext.ain.getType());

        ScriptIntrinsicConvolve5x5 convolve5x5Script = ScriptIntrinsicConvolve5x5.create(
                bitmapRSContext.rs, bitmapRSContext.ain.getElement());
        convolve5x5Script.setInput(bitmapRSContext.ain);
        convolve5x5Script.setCoefficients(coefficients);
        convolve5x5Script.forEach(aout);

        aout.copyTo(outputBitmap);
    }
}
