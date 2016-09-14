package io.github.silvaren.easyrs.tools;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

import io.github.silvaren.easyrs.tools.base.ConvertingTool;
import io.github.silvaren.easyrs.tools.base.RSToolboxContext;

public class Blur {

    static class BlurParams {
        public final float radius;
        public BlurParams(float radius) {
            this.radius = radius;
        }
    }

    private static ConvertingTool.BaseToolScript blurToolScript = new ConvertingTool.BaseToolScript<BlurParams>() {
        @Override
        public void runScript(RSToolboxContext rsToolboxContext, Allocation aout, BlurParams scriptParams) {
            ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(
                    rsToolboxContext.rs, rsToolboxContext.ain.getElement());
            blurScript.setInput(rsToolboxContext.ain);
            blurScript.setRadius(scriptParams.radius);
            blurScript.forEach(aout);
        }
    };

    /**
     * Applies a blur effect to a Bitmap image.
     * @param radius the radius of the blur.
     */
    public static Bitmap blur(RenderScript rs, Bitmap inputBitmap, float radius) {
        ConvertingTool<BlurParams> blurTool = new ConvertingTool<>(blurToolScript);
        return blurTool.doComputation(rs, inputBitmap, new BlurParams(radius));
    }

    /**
     * Applies a blur effect to a NV21 image.
     * @param nv21ByteArray the original NV21 byte array.
     * @param width the original NV21 image width.
     * @param height the original NV21 image height.
     * @param radius the radius of the blur.
     */
    public static byte[] blur(RenderScript rs, byte[] nv21ByteArray, int width, int height,
                              float radius) {
        ConvertingTool<BlurParams> blurTool = new ConvertingTool<>(blurToolScript);
        return blurTool.doComputation(rs, nv21ByteArray, width, height, new BlurParams(radius));
    }
}
