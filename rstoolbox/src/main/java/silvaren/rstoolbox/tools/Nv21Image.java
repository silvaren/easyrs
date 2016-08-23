package silvaren.rstoolbox.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.Float4;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicResize;
import android.support.v8.renderscript.Type;
import android.util.Log;

import java.util.Arrays;

import silvaren.rstoolbox.scripts.ScriptC_channel;
import silvaren.rstoolbox.scripts.ScriptC_uvencode;

public class Nv21Image {

    public final byte[] nv21ByteArray;
    public final int width;
    public final int height;

    public Nv21Image(byte[] nv21ByteArray, int width, int height) {
        this.nv21ByteArray = nv21ByteArray;
        this.width = width;
        this.height = height;
    }

    public static Nv21Image generateSample() {
        int width = 256 * 2;
        int height = 256 * 2;
        int size = width * height;
        byte[] nv21ByteArray = new byte[size + size / 2];
        Arrays.fill(nv21ByteArray, (byte) 127);

        for (int x = 0; x < 256; x++) {
            for (int y = 0; y < 256; y++) {
                nv21ByteArray[size + y * 256 * 2 + x * 2] = (byte) x;
                nv21ByteArray[size + y * 256 * 2 + x * 2 + 1] = (byte) y;
            }
        }

        return new Nv21Image(nv21ByteArray, width, height);
    }

    public static Nv21Image bitmapToNV21(RenderScript rs, Bitmap sampleBitmap, byte[] dstArray) {
        long startTime = System.currentTimeMillis();
        Bitmap yuvImage = ColorMatrix.applyMatrix(rs, sampleBitmap,
                ColorMatrixParams.rgbToNv21Matrix(), new Float4(0.0f, 0.5f, 0.5f, 0.0f));

        RSToolboxContext bitmapRSContext = RSToolboxContext.createFromBitmap(rs, yuvImage);
        ScriptC_channel channelScript = new ScriptC_channel(bitmapRSContext.rs);
        Type outType = Type.createXY(bitmapRSContext.rs, Element.U8(bitmapRSContext.rs),
                yuvImage.getWidth(), yuvImage.getHeight());
        Allocation aout = Allocation.createTyped(bitmapRSContext.rs, outType);
        channelScript.forEach_channelR(bitmapRSContext.ain, aout);
        int size = sampleBitmap.getWidth() * sampleBitmap.getHeight();

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
//        Arrays.fill(yByteArray, size, yByteArray.length, (byte) 127);

        Log.d("NV21", "Conversion to NV21: " + (System.currentTimeMillis() - startTime) + "ms");
        return new Nv21Image(yByteArray, yuvImage.getWidth(), yuvImage.getHeight());
    }

    public static Bitmap nv21ToBitmap(RenderScript rs, byte[] yByteArray, int width, int height) {
        return YuvToRgb.yuvToRgb(rs, yByteArray, width, height);
    }

    public static Nv21Image bitmapToNV21(RenderScript rs, Bitmap bitmap) {
        return bitmapToNV21(rs, bitmap, null);
    }
}
