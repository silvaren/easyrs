package silvaren.easyrs.tools;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicConvolve3x3;
import android.support.v8.renderscript.ScriptIntrinsicConvolve5x5;

import hugo.weaving.DebugLog;
import silvaren.easyrs.tools.base.ConvertingTool;
import silvaren.easyrs.tools.base.RSToolboxContext;
import silvaren.easyrs.tools.params.ConvolveParams;

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

    @DebugLog
    public static Bitmap convolve3x3(RenderScript rs, Bitmap bitmap, float[] coefficients) {
        ConvertingTool<ConvolveParams> convolveTool = new ConvertingTool<>(
                convolveToolScript(convolve3x3Script));
        return convolveTool.doComputation(rs, bitmap, new ConvolveParams(coefficients));
    }

    public static byte[] convolve3x3(RenderScript rs, byte[] nv21ByteArray, int width, int height,
                                            float[] coefficients) {
        ConvertingTool<ConvolveParams> convolveTool = new ConvertingTool<>(
                convolveToolScript(convolve3x3Script));
        return convolveTool.doComputation(rs, nv21ByteArray, width, height,
                new ConvolveParams(coefficients));
    }

    @DebugLog
    public static Bitmap convolve5x5(RenderScript rs, Bitmap bitmap, float[] coefficients) {
        ConvertingTool<ConvolveParams> convolveTool = new ConvertingTool<>(
                convolveToolScript(convolve5x5Script));
        return convolveTool.doComputation(rs, bitmap, new ConvolveParams(coefficients));
    }

    public static byte[] convolve5x5(RenderScript rs, byte[] nv21ByteArray, int width, int height,
                                            float[] coefficients) {
        ConvertingTool<ConvolveParams> convolveTool = new ConvertingTool<>(
                convolveToolScript(convolve5x5Script));
        return convolveTool.doComputation(rs, nv21ByteArray, width, height,
                new ConvolveParams(coefficients));
    }
}
