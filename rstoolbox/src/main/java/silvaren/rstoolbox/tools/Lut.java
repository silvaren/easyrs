package silvaren.rstoolbox.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.ScriptIntrinsicLUT;

public class Lut {
    public static Bitmap negativeEffect(Context context, Bitmap inputBitmap) {
        RSToolboxContext bitmapRSContext = RSToolboxContext.createFromBitmap(context, inputBitmap);
        Bitmap.Config config = inputBitmap.getConfig();
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap.getWidth(), inputBitmap.getHeight(),
                config);
        Allocation aout = Allocation.createTyped(bitmapRSContext.rs, bitmapRSContext.ain.getType());

        ScriptIntrinsicLUT lutScript = ScriptIntrinsicLUT.create(bitmapRSContext.rs, bitmapRSContext.ain.getElement());
        for (int i = 0; i < 256; i++) {
            lutScript.setAlpha(i, i);
            lutScript.setRed(i, 255 - i);
            lutScript.setGreen(i, 255 - i);
            lutScript.setBlue(i, 255 - i);
        }

        lutScript.forEach(bitmapRSContext.ain, aout);

        aout.copyTo(outputBitmap);
        return outputBitmap;
    }

    public static byte[] negativeEffect(Context context, byte[] nv21ByteArray, int width, int height) {
        Bitmap srcBitmap = Nv21Image.nv21ToBitmap(nv21ByteArray, width, height);
        Bitmap outputBmp = negativeEffect(context, srcBitmap);
        return Nv21Image.convertToNV21(context, outputBmp).nv21ByteArray;
    }
}
