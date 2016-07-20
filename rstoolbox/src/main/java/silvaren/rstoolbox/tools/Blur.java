package silvaren.rstoolbox.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.support.v8.renderscript.Type;

public class Blur {

    public static void blurInPlace(Context context, Bitmap bitmap, float radius) {
        doBlur(context, bitmap, bitmap, radius);
    }

    public static void blurInPlace(Context context, byte[] nv21ByteArray, int width, int height,
                                   float radius) {
        doBlur(context, nv21ByteArray, width, height, nv21ByteArray, radius);
    }

    public static Bitmap blur(Context context, Bitmap inputBitmap, float radius) {
        Bitmap.Config config = inputBitmap.getConfig();
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap.getWidth(), inputBitmap.getHeight(),
                config);
        doBlur(context, inputBitmap, outputBitmap, radius);
        return outputBitmap;
    }

    public static byte[] blur(Context context, byte[] nv21ByteArray, int width, int height,
                              float radius) {
        byte[] outputNv21ByteArray = new byte[nv21ByteArray.length];
        doBlur(context, nv21ByteArray, width, height, outputNv21ByteArray, radius);
        return outputNv21ByteArray;
    }

    private static void doBlur(Context context, Bitmap inputBitmap, Bitmap outputBitmap,
                               float radius) {
        RSToolboxContext rsToolboxContext = RSToolboxContext.createFromBitmap(context, inputBitmap);
        Allocation aout = Allocation.createTyped(rsToolboxContext.rs, rsToolboxContext.ain.getType());

        runBlurKernel(rsToolboxContext.rs, rsToolboxContext.ain, aout, radius);

        aout.copyTo(outputBitmap);
    }

    private static void doBlur(Context context, byte[] nv21ByteArray, int width, int height,
                               byte[] outputNv21ByteArray, float radius) {
        RSToolboxContext rsToolboxContext = RSToolboxContext.createFromNv21Image(context,
                nv21ByteArray, width, height);
        Allocation aout = Allocation.createTyped(rsToolboxContext.rs, rsToolboxContext.ain.getType());

        runBlurKernel(rsToolboxContext.rs, rsToolboxContext.ain, aout, radius);

        aout.copyTo(outputNv21ByteArray);
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
