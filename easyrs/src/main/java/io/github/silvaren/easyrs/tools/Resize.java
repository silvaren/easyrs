package io.github.silvaren.easyrs.tools;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicResize;
import android.support.v8.renderscript.Type;

import io.github.silvaren.easyrs.tools.base.RSToolboxContext;

public class Resize {

    /**
     * Resizes a Bitmap image to a target width and height.
     */
    public static Bitmap resize(RenderScript rs, Bitmap inputBitmap, int targetWidth,
                                int targetHeight) {
        RSToolboxContext bitmapRSContext = RSToolboxContext.createFromBitmap(rs, inputBitmap);
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

    /**
     * Resizes a NV21 image to a target width and height.
     * @param nv21ByteArray the original NV21 byte array.
     * @param width the original NV21 image width.
     * @param height the original NV21 image height.
     */
    public static byte[] resize(RenderScript rs, byte[] nv21ByteArray, int width, int height,
                                int targetWidth, int targetHeight) {
        Bitmap srcBitmap = Nv21Image.nv21ToBitmap(rs, nv21ByteArray, width, height);
        Bitmap resizedBitmap = resize(rs, srcBitmap, targetWidth, targetHeight);
        return Nv21Image.bitmapToNV21(rs, resizedBitmap).nv21ByteArray;
    }
}
