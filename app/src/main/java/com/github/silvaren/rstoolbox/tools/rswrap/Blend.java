package com.github.silvaren.rstoolbox.tools.rswrap;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.ScriptIntrinsicBlend;

class Blend {

    private static class BaseSetup {
        public final BitmapRSContext bitmapRSContext;
        public final Allocation aout;
        public final ScriptIntrinsicBlend blendScript;

        private BaseSetup(BitmapRSContext bitmapRSContext, Allocation aout, ScriptIntrinsicBlend scriptIntrinsicBlend) {
            this.bitmapRSContext = bitmapRSContext;
            this.aout = aout;
            this.blendScript = scriptIntrinsicBlend;
        }

        public static BaseSetup create(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
            BitmapRSContext bitmapRSContext = BitmapRSContext.createFromBitmap(srcBitmap, context);
            Allocation aout = Allocation.createFromBitmap(bitmapRSContext.rs, dstBitmap);

            ScriptIntrinsicBlend blendScript = ScriptIntrinsicBlend.create(
                    bitmapRSContext.rs, bitmapRSContext.ain.getElement());
            return new BaseSetup(bitmapRSContext, aout, blendScript);
        }
    }

    public static void add(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        BaseSetup baseSetup = BaseSetup.create(context, srcBitmap, dstBitmap);
        baseSetup.blendScript.forEachAdd(baseSetup.bitmapRSContext.ain, baseSetup.aout);
        baseSetup.aout.copyTo(dstBitmap);
    }

    public static void clear(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        BaseSetup baseSetup = BaseSetup.create(context, srcBitmap, dstBitmap);
        baseSetup.blendScript.forEachClear(baseSetup.bitmapRSContext.ain, baseSetup.aout);
        baseSetup.aout.copyTo(dstBitmap);
    }

    public static void dst(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        BaseSetup baseSetup = BaseSetup.create(context, srcBitmap, dstBitmap);
        baseSetup.blendScript.forEachDst(baseSetup.bitmapRSContext.ain, baseSetup.aout);
        baseSetup.aout.copyTo(dstBitmap);
    }

    public static void dstAtop(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        BaseSetup baseSetup = BaseSetup.create(context, srcBitmap, dstBitmap);
        baseSetup.blendScript.forEachDstAtop(baseSetup.bitmapRSContext.ain, baseSetup.aout);
        baseSetup.aout.copyTo(dstBitmap);
    }

    public static void dstIn(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        BaseSetup baseSetup = BaseSetup.create(context, srcBitmap, dstBitmap);
        baseSetup.blendScript.forEachDstIn(baseSetup.bitmapRSContext.ain, baseSetup.aout);
        baseSetup.aout.copyTo(dstBitmap);
    }

    public static void dstOut(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        BaseSetup baseSetup = BaseSetup.create(context, srcBitmap, dstBitmap);
        baseSetup.blendScript.forEachDstOut(baseSetup.bitmapRSContext.ain, baseSetup.aout);
        baseSetup.aout.copyTo(dstBitmap);
    }

    public static void dstOver(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        BaseSetup baseSetup = BaseSetup.create(context, srcBitmap, dstBitmap);
        baseSetup.blendScript.forEachDstOver(baseSetup.bitmapRSContext.ain, baseSetup.aout);
        baseSetup.aout.copyTo(dstBitmap);
    }

    public static void multiply(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        BaseSetup baseSetup = BaseSetup.create(context, srcBitmap, dstBitmap);
        baseSetup.blendScript.forEachMultiply(baseSetup.bitmapRSContext.ain, baseSetup.aout);
        baseSetup.aout.copyTo(dstBitmap);
    }

    public static void src(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        BaseSetup baseSetup = BaseSetup.create(context, srcBitmap, dstBitmap);
        baseSetup.blendScript.forEachSrc(baseSetup.bitmapRSContext.ain, baseSetup.aout);
        baseSetup.aout.copyTo(dstBitmap);
    }

    public static void srcAtop(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        BaseSetup baseSetup = BaseSetup.create(context, srcBitmap, dstBitmap);
        baseSetup.blendScript.forEachSrcAtop(baseSetup.bitmapRSContext.ain, baseSetup.aout);
        baseSetup.aout.copyTo(dstBitmap);
    }

    public static void srcIn(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        BaseSetup baseSetup = BaseSetup.create(context, srcBitmap, dstBitmap);
        baseSetup.blendScript.forEachSrcIn(baseSetup.bitmapRSContext.ain, baseSetup.aout);
        baseSetup.aout.copyTo(dstBitmap);
    }

    public static void srcOut(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        BaseSetup baseSetup = BaseSetup.create(context, srcBitmap, dstBitmap);
        baseSetup.blendScript.forEachSrcOut(baseSetup.bitmapRSContext.ain, baseSetup.aout);
        baseSetup.aout.copyTo(dstBitmap);
    }

    public static void srcOver(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        BaseSetup baseSetup = BaseSetup.create(context, srcBitmap, dstBitmap);
        baseSetup.blendScript.forEachSrcOver(baseSetup.bitmapRSContext.ain, baseSetup.aout);
        baseSetup.aout.copyTo(dstBitmap);
    }

    public static void subtract(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        BaseSetup baseSetup = BaseSetup.create(context, srcBitmap, dstBitmap);
        baseSetup.blendScript.forEachSubtract(baseSetup.bitmapRSContext.ain, baseSetup.aout);
        baseSetup.aout.copyTo(dstBitmap);
    }

    public static void xor(Context context, Bitmap srcBitmap, Bitmap dstBitmap) {
        BaseSetup baseSetup = BaseSetup.create(context, srcBitmap, dstBitmap);
        baseSetup.blendScript.forEachXor(baseSetup.bitmapRSContext.ain, baseSetup.aout);
        baseSetup.aout.copyTo(dstBitmap);
    }
}
