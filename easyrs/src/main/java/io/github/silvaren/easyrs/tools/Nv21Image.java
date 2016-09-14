package io.github.silvaren.easyrs.tools;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.Float4;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicResize;
import android.support.v8.renderscript.Type;
import android.util.Log;

import io.github.silvaren.easyrs.scripts.ScriptC_channel;
import io.github.silvaren.easyrs.scripts.ScriptC_uvencode;
import io.github.silvaren.easyrs.tools.base.RSToolboxContext;
import io.github.silvaren.easyrs.tools.params.ColorMatrixParams;

public class Nv21Image {

    public final byte[] nv21ByteArray;
    public final int width;
    public final int height;

    public Nv21Image(byte[] nv21ByteArray, int width, int height) {
        this.nv21ByteArray = nv21ByteArray;
        this.width = width;
        this.height = height;
    }

    /**
     * Converts an android Bitmap image to NV21 format. If the image has odd dimensions the
     * conversion process will round down each dimension to its closest even integer.
     * @param dstArray is an optional byte array to receive the converted NV21 data. It
     *                 must be (1.5 * number_of_pixels) bytes long. If null is passed,
     *                 a new byte array will be created and returned.
     */
    public static Nv21Image bitmapToNV21(RenderScript rs, Bitmap bitmap, byte[] dstArray) {
        long startTime = System.currentTimeMillis();

        Bitmap croppedBitmap = bitmap;

        if (bitmap.getWidth() % 2 > 0 || bitmap.getHeight() % 2 > 0) {
            croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, (bitmap.getWidth() / 2) * 2,
                    (bitmap.getHeight() / 2) * 2);
        }
        Bitmap yuvImage = ColorMatrix.applyMatrix(rs, croppedBitmap,
                ColorMatrixParams.rgbToNv21Matrix(), new Float4(0.0f, 0.5f, 0.5f, 0.0f));

        RSToolboxContext bitmapRSContext = RSToolboxContext.createFromBitmap(rs, yuvImage);
        ScriptC_channel channelScript = new ScriptC_channel(bitmapRSContext.rs);
        Type outType = Type.createXY(bitmapRSContext.rs, Element.U8(bitmapRSContext.rs),
                yuvImage.getWidth(), yuvImage.getHeight());
        Allocation aout = Allocation.createTyped(bitmapRSContext.rs, outType);
        channelScript.forEach_channelR(bitmapRSContext.ain, aout);
        int size = croppedBitmap.getWidth() * croppedBitmap.getHeight();

        byte[] yByteArray;
        if (dstArray == null)
            yByteArray = new byte[size + size / 2];
        else
            yByteArray = dstArray;
        aout.copyTo(yByteArray);

        Bitmap.Config config = yuvImage.getConfig();
        Bitmap resizedBmp = Bitmap.createBitmap(yuvImage.getWidth()/2, yuvImage.getHeight()/2, config);
        Type resizeoutType = Type.createXY(bitmapRSContext.rs, bitmapRSContext.ain.getElement(),
                yuvImage.getWidth()/2, yuvImage.getHeight()/2);
        Allocation resizeaout = Allocation.createTyped(bitmapRSContext.rs, resizeoutType);
        ScriptIntrinsicResize resizeScript = ScriptIntrinsicResize.create(bitmapRSContext.rs);
        resizeScript.setInput(bitmapRSContext.ain);
        resizeScript.forEach_bicubic(resizeaout);
        resizeaout.copyTo(resizedBmp);

        Allocation resizedIn = Allocation.createFromBitmap(bitmapRSContext.rs, resizedBmp);
        ScriptC_uvencode encodeScript = new ScriptC_uvencode(bitmapRSContext.rs);
        Type uvtype = Type.createX(bitmapRSContext.rs, Element.U8(bitmapRSContext.rs),
                size / 2);
        Allocation uvAllocation = Allocation.createTyped(bitmapRSContext.rs, uvtype);
        encodeScript.set_width(yuvImage.getWidth());
        encodeScript.set_height(yuvImage.getHeight());
        encodeScript.set_gOut(uvAllocation);
        encodeScript.forEach_root(resizedIn);

        byte[] uvByteArray = new byte[size / 2];

        uvAllocation.copyTo(uvByteArray);
        System.arraycopy(uvByteArray, 0, yByteArray, size, uvByteArray.length);

        Log.d("NV21", "Conversion to NV21: " + (System.currentTimeMillis() - startTime) + "ms");
        return new Nv21Image(yByteArray, yuvImage.getWidth(), yuvImage.getHeight());
    }

    /**
     * Converts a NV21 image to a Bitmap, by using {@link YuvToRgb}.
     * @param nv21ByteArray the original NV21 byte array.
     * @param width the original NV21 image width.
     * @param height the original NV21 image height.
     */
    public static Bitmap nv21ToBitmap(RenderScript rs, byte[] nv21ByteArray, int width, int height) {
        return YuvToRgb.yuvToRgb(rs, nv21ByteArray, width, height);
    }

    /**
     * Converts a NV21 image to a Bitmap, by using {@link YuvToRgb}.
     * @param nv21Image the NV21 image to convert.
     */
    public static Bitmap nv21ToBitmap(RenderScript rs, Nv21Image nv21Image) {
        return YuvToRgb.yuvToRgb(rs, nv21Image.nv21ByteArray, nv21Image.width, nv21Image.height);
    }

    /**
     * Converts an android Bitmap image to NV21 format. If the image has odd dimensions the
     * conversion process will round down each dimension to its closest even integer.
     */
    public static Nv21Image bitmapToNV21(RenderScript rs, Bitmap bitmap) {
        return bitmapToNV21(rs, bitmap, null);
    }
}
