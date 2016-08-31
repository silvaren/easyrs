package io.github.silvaren.easyrs.tools;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlend;

import io.github.silvaren.easyrs.tools.base.RSToolboxContext;

public class Blend {

    private static void doOp(RenderScript rs, Bitmap srcBitmap, Bitmap dstBitmap, BlendOp blendOp) {
        BaseSetup baseSetup = BaseSetup.createFromBitmap(rs, srcBitmap, dstBitmap);
        blendOp.runOp(baseSetup);
        baseSetup.aout.copyTo(dstBitmap);
    }

    private static void doOp(RenderScript rs, byte[] nv21ByteArraySrc, int width, int height,
                               byte[] nv21ByteArrayDst, BlendOp blendOp) {
        Bitmap srcBitmap = Nv21Image.nv21ToBitmap(rs, nv21ByteArraySrc, width, height);
        Bitmap dstBitmap = Nv21Image.nv21ToBitmap(rs, nv21ByteArrayDst, width, height);
        doOp(rs, srcBitmap, dstBitmap, blendOp);
        Nv21Image.bitmapToNV21(rs, dstBitmap, nv21ByteArrayDst);
    }

    private static class BaseSetup {
        public final RSToolboxContext rsToolboxContext;
        public final Allocation aout;
        public final ScriptIntrinsicBlend blendScript;

        private BaseSetup(RSToolboxContext bitmapRSContext, Allocation aout, ScriptIntrinsicBlend scriptIntrinsicBlend) {
            this.rsToolboxContext = bitmapRSContext;
            this.aout = aout;
            this.blendScript = scriptIntrinsicBlend;
        }

        public static BaseSetup createFromBitmap(RenderScript rs, Bitmap srcBitmap, Bitmap dstBitmap) {
            RSToolboxContext bitmapRSContext = RSToolboxContext.createFromBitmap(rs, srcBitmap);
            Allocation aout = Allocation.createFromBitmap(bitmapRSContext.rs, dstBitmap);

            ScriptIntrinsicBlend blendScript = ScriptIntrinsicBlend.create(
                    bitmapRSContext.rs, bitmapRSContext.ain.getElement());
            return new BaseSetup(bitmapRSContext, aout, blendScript);
        }
    }

    interface BlendOp {
        void runOp(BaseSetup baseSetup);
    }

    private static BlendOp add = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachAdd(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static BlendOp clear = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachClear(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static BlendOp dst = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachDst(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static BlendOp dstAtop = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachDstAtop(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static BlendOp dstIn = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachDstIn(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static BlendOp dstOut = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachDstOut(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static BlendOp dstOver = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachDstOver(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static BlendOp multiply = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachMultiply(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static BlendOp src = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachSrc(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static BlendOp srcAtop = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachSrcAtop(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static BlendOp srcIn = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachSrcIn(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static BlendOp srcOut = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachSrcOut(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static BlendOp srcOver = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachSrcOver(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static BlendOp subtract = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachSubtract(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    private static BlendOp xor = new BlendOp() {
        @Override
        public void runOp(BaseSetup baseSetup) {
            baseSetup.blendScript.forEachXor(baseSetup.rsToolboxContext.ain, baseSetup.aout);
        }
    };

    public static void add(RenderScript rs, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(rs, srcBitmap, dstBitmap, add);
    }

    public static void clear(RenderScript rs, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(rs, srcBitmap, dstBitmap, clear);
    }

    public static void dst(RenderScript rs, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(rs, srcBitmap, dstBitmap, dst);
    }

    public static void dstAtop(RenderScript rs, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(rs, srcBitmap, dstBitmap, dstAtop);
    }

    public static void dstIn(RenderScript rs, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(rs, srcBitmap, dstBitmap, dstIn);
    }

    public static void dstOut(RenderScript rs, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(rs, srcBitmap, dstBitmap, dstOut);
    }

    public static void dstOver(RenderScript rs, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(rs, srcBitmap, dstBitmap, dstOver);
    }

    public static void multiply(RenderScript rs, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(rs, srcBitmap, dstBitmap, multiply);
    }

    public static void src(RenderScript rs, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(rs, srcBitmap, dstBitmap, src);
    }

    public static void srcAtop(RenderScript rs, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(rs, srcBitmap, dstBitmap, srcAtop);
    }

    public static void srcIn(RenderScript rs, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(rs, srcBitmap, dstBitmap, srcIn);
    }

    public static void srcOut(RenderScript rs, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(rs, srcBitmap, dstBitmap, srcOut);
    }

    public static void srcOver(RenderScript rs, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(rs, srcBitmap, dstBitmap, srcOver);
    }

    public static void subtract(RenderScript rs, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(rs, srcBitmap, dstBitmap, subtract);
    }

    public static void xor(RenderScript rs, Bitmap srcBitmap, Bitmap dstBitmap) {
        doOp(rs, srcBitmap, dstBitmap, xor);
    }

    // NV21 methods
    public static void add(RenderScript rs, byte[] nv21ByteArraySrc, int width, int height,
                           byte[] nv21ByteArrayDst) {
        doOp(rs, nv21ByteArraySrc, width, height, nv21ByteArrayDst, add);
    }

    public static void clear(RenderScript rs, byte[] nv21ByteArraySrc, int width, int height,
                            byte[] nv21ByteArrayDst) {
        doOp(rs, nv21ByteArraySrc, width, height, nv21ByteArrayDst, clear);
    }

    public static void dst(RenderScript rs, byte[] nv21ByteArraySrc, int width, int height,
                            byte[] nv21ByteArrayDst) {
        doOp(rs, nv21ByteArraySrc, width, height, nv21ByteArrayDst, dst);
    }

    public static void dstAtop(RenderScript rs, byte[] nv21ByteArraySrc, int width, int height,
                            byte[] nv21ByteArrayDst) {
        doOp(rs, nv21ByteArraySrc, width, height, nv21ByteArrayDst, dstAtop);
    }

    public static void dstIn(RenderScript rs, byte[] nv21ByteArraySrc, int width, int height,
                            byte[] nv21ByteArrayDst) {
        doOp(rs, nv21ByteArraySrc, width, height, nv21ByteArrayDst, dstIn);
    }

    public static void dstOut(RenderScript rs, byte[] nv21ByteArraySrc, int width, int height,
                            byte[] nv21ByteArrayDst) {
        doOp(rs, nv21ByteArraySrc, width, height, nv21ByteArrayDst, dstOut);
    }

    public static void dstOver(RenderScript rs, byte[] nv21ByteArraySrc, int width, int height,
                            byte[] nv21ByteArrayDst) {
        doOp(rs, nv21ByteArraySrc, width, height, nv21ByteArrayDst, dstOver);
    }

    public static void multiply(RenderScript rs, byte[] nv21ByteArraySrc, int width, int height,
                            byte[] nv21ByteArrayDst) {
        doOp(rs, nv21ByteArraySrc, width, height, nv21ByteArrayDst, multiply);
    }

    public static void src(RenderScript rs, byte[] nv21ByteArraySrc, int width, int height,
                            byte[] nv21ByteArrayDst) {
        doOp(rs, nv21ByteArraySrc, width, height, nv21ByteArrayDst, src);
    }

    public static void srcAtop(RenderScript rs, byte[] nv21ByteArraySrc, int width, int height,
                            byte[] nv21ByteArrayDst) {
        doOp(rs, nv21ByteArraySrc, width, height, nv21ByteArrayDst, srcAtop);
    }

    public static void srcIn(RenderScript rs, byte[] nv21ByteArraySrc, int width, int height,
                            byte[] nv21ByteArrayDst) {
        doOp(rs, nv21ByteArraySrc, width, height, nv21ByteArrayDst, srcIn);
    }

    public static void srcOut(RenderScript rs, byte[] nv21ByteArraySrc, int width, int height,
                            byte[] nv21ByteArrayDst) {
        doOp(rs, nv21ByteArraySrc, width, height, nv21ByteArrayDst, srcOut);
    }

    public static void srcOver(RenderScript rs, byte[] nv21ByteArraySrc, int width, int height,
                            byte[] nv21ByteArrayDst) {
        doOp(rs, nv21ByteArraySrc, width, height, nv21ByteArrayDst, srcOver);
    }

    public static void subtract(RenderScript rs, byte[] nv21ByteArraySrc, int width, int height,
                            byte[] nv21ByteArrayDst) {
        doOp(rs, nv21ByteArraySrc, width, height, nv21ByteArrayDst, subtract);
    }

    public static void xor(RenderScript rs, byte[] nv21ByteArraySrc, int width, int height,
                            byte[] nv21ByteArrayDst) {
        doOp(rs, nv21ByteArraySrc, width, height, nv21ByteArrayDst, xor);
    }
}
