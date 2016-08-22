package silvaren.rstoolbox.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.ScriptIntrinsicResize;
import android.support.v8.renderscript.Type;

import hugo.weaving.DebugLog;

public class Resize {

    @DebugLog
    public static Bitmap resize(Context context, Bitmap inputBitmap, int targetWidth,
                                int targetHeight) {
        RSToolboxContext bitmapRSContext = RSToolboxContext.createFromBitmap(context, inputBitmap);
        Bitmap.Config config = inputBitmap.getConfig();
        Bitmap outputBitmap = Bitmap.createBitmap(targetWidth, targetHeight, config);
        Type outType = Type.createXY(bitmapRSContext.rs, bitmapRSContext.ain.getElement(), targetWidth,
                targetHeight);
        Allocation aout = Allocation.createTyped(bitmapRSContext.rs, outType);

        ScriptIntrinsicResize resizeScript = ScriptIntrinsicResize.create(bitmapRSContext.rs);
        resizeScript.setInput(bitmapRSContext.ain);
        resizeScript.forEach_bicubic(aout);

        aout.copyTo(outputBitmap);
        return outputBitmap;
    }

    public static byte[] resize(Context context, byte[] nv21ByteArray, int width, int height,
                                int targetWidth, int targetHeight) {
        Bitmap srcBitmap = Nv21Image.nv21ToBitmap(context, nv21ByteArray, width, height);
        Bitmap resizedBitmap = resize(context, srcBitmap, targetWidth, targetHeight);
        return Nv21Image.bitmapToNV21(context, resizedBitmap).nv21ByteArray;
    }
}
