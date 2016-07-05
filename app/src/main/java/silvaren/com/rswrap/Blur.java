package silvaren.com.rswrap;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

class Blur {
    public static void blurInPlace(Context context, Bitmap bitmap, float radius) {
        blur(context, bitmap, bitmap, radius);
    }

    private Bitmap blur(Context context, Bitmap inputBitmap, float radius) {
        Bitmap.Config config = inputBitmap.getConfig();
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap.getWidth(), inputBitmap.getHeight(),
                config);
        blur(context, inputBitmap, outputBitmap, radius);
        return outputBitmap;
    }

    public static void blur(Context context, Bitmap inputBitmap, Bitmap outputBitmap, float radius) {
        MainActivity.BitmapRSContext bitmapRSContext = MainActivity.BitmapRSContext.createFromBitmap(inputBitmap, context);
        Allocation aout = Allocation.createTyped(bitmapRSContext.rs, bitmapRSContext.ain.getType());

        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(
                bitmapRSContext.rs,
                bitmapRSContext.bitmapElement);
        blurScript.setInput(bitmapRSContext.ain);
        blurScript.setRadius(radius);
        blurScript.forEach(aout);

        aout.copyTo(outputBitmap);
    }
}
