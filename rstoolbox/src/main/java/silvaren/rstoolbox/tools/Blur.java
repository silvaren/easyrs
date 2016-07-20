package silvaren.rstoolbox.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

public class Blur {

    public static void blurInPlace(Context context, Bitmap bitmap, float radius) {
        doBlur(context, bitmap, bitmap, radius);
    }

    public static Bitmap blur(Context context, Bitmap inputBitmap, float radius) {
        Bitmap.Config config = inputBitmap.getConfig();
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap.getWidth(), inputBitmap.getHeight(),
                config);
        doBlur(context, inputBitmap, outputBitmap, radius);
        return outputBitmap;
    }

    private static void doBlur(Context context, Bitmap inputBitmap, Bitmap outputBitmap,
                               float radius) {
        BitmapRSContext bitmapRSContext = BitmapRSContext.createFromBitmap(inputBitmap, context);
        Allocation aout = Allocation.createTyped(bitmapRSContext.rs, bitmapRSContext.ain.getType());

        runBlurKernel(bitmapRSContext.rs, bitmapRSContext.ain, aout, radius);

        aout.copyTo(outputBitmap);
    }

    private static void runBlurKernel(RenderScript rs, Allocation ain, Allocation aout,
                                      float radius) {
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(
                rs, ain.getElement());
        blurScript.setInput(ain);
        blurScript.setRadius(radius);
        blurScript.forEach(aout);
    }
}
