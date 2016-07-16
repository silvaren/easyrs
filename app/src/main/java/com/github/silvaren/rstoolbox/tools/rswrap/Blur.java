package com.github.silvaren.rstoolbox.tools.rswrap;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

class Blur {

    public static void blurInPlace(Context context, Bitmap bitmap, float radius) {
        doBlur(context, bitmap, bitmap, radius);
    }

    public static Bitmap doBlur(Context context, Bitmap inputBitmap, float radius) {
        Bitmap.Config config = inputBitmap.getConfig();
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap.getWidth(), inputBitmap.getHeight(),
                config);
        doBlur(context, inputBitmap, outputBitmap, radius);
        return outputBitmap;
    }

    private static void doBlur(Context context, Bitmap inputBitmap, Bitmap outputBitmap, float radius) {
        BitmapRSContext bitmapRSContext = BitmapRSContext.createFromBitmap(inputBitmap, context);
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
