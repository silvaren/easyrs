package io.github.silvaren.easyrs.tools;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicConvolve3x3;
import android.support.v8.renderscript.ScriptIntrinsicConvolve5x5;

import io.github.silvaren.easyrs.tools.base.ConvertingTool;
import io.github.silvaren.easyrs.tools.base.RSToolboxContext;
import io.github.silvaren.easyrs.tools.params.ConvolveParams;

public class Convolve {

    interface ConvolveScript {
        void runConvolveScript(RSToolboxContext rsToolboxrs, Allocation aout, ConvolveParams scriptParams);
    }

    private static ConvertingTool.BaseToolScript convolveToolScript(final ConvolveScript convolveScript) {
        return new ConvertingTool.BaseToolScript<ConvolveParams>() {
            @Override
            public void runScript(RSToolboxContext rsToolboxrs, Allocation aout,
                                  ConvolveParams scriptParams) {
                convolveScript.runConvolveScript(rsToolboxrs, aout, scriptParams);
            }
        };
    }

    private static ConvolveScript convolve3x3Script = new ConvolveScript() {
        @Override
        public void runConvolveScript(RSToolboxContext rsToolboxContext, Allocation aout, ConvolveParams scriptParams) {
            ScriptIntrinsicConvolve3x3 convolve3x3Script = ScriptIntrinsicConvolve3x3.create(
                    rsToolboxContext.rs, rsToolboxContext.ain.getElement());
            convolve3x3Script.setInput(rsToolboxContext.ain);
            convolve3x3Script.setCoefficients(scriptParams.coefficients);
            convolve3x3Script.forEach(aout);
        }
    };

    private static ConvolveScript convolve5x5Script = new ConvolveScript() {
        @Override
        public void runConvolveScript(RSToolboxContext rsToolboxContext, Allocation aout, ConvolveParams scriptParams) {
            ScriptIntrinsicConvolve5x5 convolve5x5Script = ScriptIntrinsicConvolve5x5.create(
                    rsToolboxContext.rs, rsToolboxContext.ain.getElement());
            convolve5x5Script.setInput(rsToolboxContext.ain);
            convolve5x5Script.setCoefficients(scriptParams.coefficients);
            convolve5x5Script.forEach(aout);
        }
    };

    /**
     * Applies a 3x3 convolution to a Bitmap image.
     * @param coefficients the 3x3 convolution coefficients.
     */
    public static Bitmap convolve3x3(RenderScript rs, Bitmap bitmap, float[] coefficients) {
        ConvertingTool<ConvolveParams> convolveTool = new ConvertingTool<>(
                convolveToolScript(convolve3x3Script));
        return convolveTool.doComputation(rs, bitmap, new ConvolveParams(coefficients));
    }

    /**
     * Applies a 3x3 convolution to a NV21 image.
     * @param nv21ByteArray the original NV21 byte array.
     * @param width the original NV21 image width.
     * @param height the original NV21 image height.
     * @param coefficients the 3x3 convolution coefficients.
     */
    public static byte[] convolve3x3(RenderScript rs, byte[] nv21ByteArray, int width, int height,
                                            float[] coefficients) {
        ConvertingTool<ConvolveParams> convolveTool = new ConvertingTool<>(
                convolveToolScript(convolve3x3Script));
        return convolveTool.doComputation(rs, nv21ByteArray, width, height,
                new ConvolveParams(coefficients));
    }

    /**
     * Applies a 5x5 convolution to a Bitmap image.
     * @param coefficients the 5x5 convolution coefficients.
     */
    public static Bitmap convolve5x5(RenderScript rs, Bitmap bitmap, float[] coefficients) {
        ConvertingTool<ConvolveParams> convolveTool = new ConvertingTool<>(
                convolveToolScript(convolve5x5Script));
        return convolveTool.doComputation(rs, bitmap, new ConvolveParams(coefficients));
    }

    /**
     * Applies a 5x5 convolution to a NV21 image.
     * @param nv21ByteArray the original NV21 byte array.
     * @param width the original NV21 image width.
     * @param height the original NV21 image height.
     * @param coefficients the 5x5 convolution coefficients.
     */
    public static byte[] convolve5x5(RenderScript rs, byte[] nv21ByteArray, int width, int height,
                                            float[] coefficients) {
        ConvertingTool<ConvolveParams> convolveTool = new ConvertingTool<>(
                convolveToolScript(convolve5x5Script));
        return convolveTool.doComputation(rs, nv21ByteArray, width, height,
                new ConvolveParams(coefficients));
    }
}
