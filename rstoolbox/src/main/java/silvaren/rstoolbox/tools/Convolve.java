package silvaren.rstoolbox.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.ScriptIntrinsicConvolve3x3;
import android.support.v8.renderscript.ScriptIntrinsicConvolve5x5;

public class Convolve {

    interface ConvolveScript {
        void runConvolveScript(RSToolboxContext rsToolboxContext, Allocation aout, ConvolveParams scriptParams);
    }

    private static BaseTool.BaseToolScript convolveToolScript(final ConvolveScript convolveScript) {
        return new BaseTool.BaseToolScript<ConvolveParams>() {
            @Override
            public void runScript(RSToolboxContext rsToolboxContext, Allocation aout,
                                  ConvolveParams scriptParams) {
                convolveScript.runConvolveScript(rsToolboxContext, aout, scriptParams);
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

    public static Bitmap convolve3x3(Context context, Bitmap bitmap, float[] coefficients) {
        BaseTool<ConvolveParams> convolveTool = new BaseTool<>(convolveToolScript(convolve3x3Script));
        return convolveTool.doComputation(context, bitmap, new ConvolveParams(coefficients));
    }

    public static byte[] convolveInPlace3x3(Context context, byte[] nv21ByteArray, int width, int height,
                                            float[] coefficients) {
        BaseTool<ConvolveParams> convolveTool = new BaseTool<>(convolveToolScript(convolve3x3Script));
        return convolveTool.doComputation(context, nv21ByteArray, width, height,
                new ConvolveParams(coefficients));
    }

    public static Bitmap convolve5x5(Context context, Bitmap bitmap, float[] coefficients) {
        BaseTool<ConvolveParams> convolveTool = new BaseTool<>(convolveToolScript(convolve5x5Script));
        return convolveTool.doComputation(context, bitmap, new ConvolveParams(coefficients));
    }

    public static byte[] convolveInPlace5x5(Context context, byte[] nv21ByteArray, int width, int height,
                                            float[] coefficients) {
        BaseTool<ConvolveParams> convolveTool = new BaseTool<>(convolveToolScript(convolve5x5Script));
        return convolveTool.doComputation(context, nv21ByteArray, width, height,
                new ConvolveParams(coefficients));
    }
}
