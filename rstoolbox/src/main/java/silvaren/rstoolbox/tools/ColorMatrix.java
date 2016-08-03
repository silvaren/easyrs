package silvaren.rstoolbox.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.ScriptIntrinsicColorMatrix;

public class ColorMatrix {

    public static void convertToGrayscaleInPlace(Context context, Bitmap bitmap) {
        doConvertToGrayscale(context, bitmap, bitmap);
    }

    public static Bitmap doConvertToGrayScale(Context context, Bitmap inputBitmap) {
        Bitmap.Config config = inputBitmap.getConfig();
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap.getWidth(), inputBitmap.getHeight(),
                config);
        doConvertToGrayscale(context, inputBitmap, outputBitmap);
        return outputBitmap;
    }

    public static byte[] doConvertToGrayScale(Context context, byte[] nv21ByteArray, int width, int height) {
        Bitmap srcBitmap = Nv21Image.nv21ToBitmap(nv21ByteArray, width, height);
        convertToGrayscaleInPlace(context, srcBitmap);
        return Nv21Image.convertToNV21(context, srcBitmap).nv21ByteArray;
    }

    public static Bitmap rgbToYuv(Context context, Bitmap inputBitmap) {
        Bitmap.Config config = inputBitmap.getConfig();
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap.getWidth(), inputBitmap.getHeight(),
                config);
        doRgbToYuv(context, inputBitmap, outputBitmap);
        return outputBitmap;
    }

    private static void doConvertToGrayscale(Context context, Bitmap inputBitmap, Bitmap outputBitmap) {
        RSToolboxContext bitmapRSContext = RSToolboxContext.createFromBitmap(context, inputBitmap);
        Allocation aout = Allocation.createTyped(bitmapRSContext.rs, bitmapRSContext.ain.getType());

        ScriptIntrinsicColorMatrix colorMatrixScript = ScriptIntrinsicColorMatrix.create(
                bitmapRSContext.rs, bitmapRSContext.ain.getElement());
        colorMatrixScript.setGreyscale();
        colorMatrixScript.forEach(bitmapRSContext.ain, aout);

        aout.copyTo(outputBitmap);
    }

    private static void doRgbToYuv(Context context, Bitmap inputBitmap, Bitmap outputBitmap) {
        RSToolboxContext bitmapRSContext = RSToolboxContext.createFromBitmap(context, inputBitmap);
        Allocation aout = Allocation.createTyped(bitmapRSContext.rs, bitmapRSContext.ain.getType());

        ScriptIntrinsicColorMatrix colorMatrixScript = ScriptIntrinsicColorMatrix.create(
                bitmapRSContext.rs, bitmapRSContext.ain.getElement());
        colorMatrixScript.setRGBtoYUV();
        colorMatrixScript.setAdd(0.0f, 0.5f, 0.5f, 0.0f);
        colorMatrixScript.forEach(bitmapRSContext.ain, aout);

        aout.copyTo(outputBitmap);
    }
}
