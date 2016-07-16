package com.github.silvaren.rstoolbox.tools.rswrap;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.ScriptIntrinsicColorMatrix;

class ColorMatrix {

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

    private static void doConvertToGrayscale(Context context, Bitmap inputBitmap, Bitmap outputBitmap) {
        BitmapRSContext bitmapRSContext = BitmapRSContext.createFromBitmap(inputBitmap, context);
        Allocation aout = Allocation.createTyped(bitmapRSContext.rs, bitmapRSContext.ain.getType());

        ScriptIntrinsicColorMatrix colorMatrixScript = ScriptIntrinsicColorMatrix.create(
                bitmapRSContext.rs, bitmapRSContext.ain.getElement());
        colorMatrixScript.setGreyscale();
        colorMatrixScript.forEach(bitmapRSContext.ain, aout);

        aout.copyTo(outputBitmap);
    }
}
