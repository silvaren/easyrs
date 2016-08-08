package silvaren.rstoolbox.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.ScriptIntrinsicConvolve3x3;
import android.support.v8.renderscript.ScriptIntrinsicConvolve5x5;

public class Convolve extends BaseTool<ConvolveParams> {

    interface ConvolveScript {
        void runConvolveScript(RSToolboxContext rsToolboxContext, Allocation aout, ConvolveParams scriptParams);
    }

    private final ConvolveScript convolveScript;

    Convolve(ConvolveScript convolveScript) {
        this.convolveScript = convolveScript;
    }

    @Override
    protected void runScript(RSToolboxContext rsToolboxContext, Allocation aout, ConvolveParams scriptParams) {
        this.convolveScript.runConvolveScript(rsToolboxContext, aout, scriptParams);
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

    public static void convolveInPlace3x3(Context context, Bitmap bitmap, float[] coefficients) {
        Convolve convolve = new Convolve(Convolve.convolve3x3Script);
        convolve.doComputationInPlace(context, bitmap, new ConvolveParams(coefficients));
    }

    public static Bitmap convolve3x3(Context context, Bitmap bitmap, float[] coefficients) {
        Convolve convolve = new Convolve(Convolve.convolve3x3Script);
        return convolve.doComputation(context, bitmap, new ConvolveParams(coefficients));
    }

    public static void convolveInPlace3x3inPlace(Context context, byte[] nv21ByteArray, int width,
                                                 int height, float[] coefficients) {
        Convolve convolve = new Convolve(Convolve.convolve3x3Script);
        convolve.doComputationInPlace(context, nv21ByteArray, width, height,
                new ConvolveParams(coefficients));
    }

    public static byte[] convolveInPlace3x3(Context context, byte[] nv21ByteArray, int width, int height,
                                            float[] coefficients) {
        Convolve convolve = new Convolve(Convolve.convolve3x3Script);
        return convolve.doComputation(context, nv21ByteArray, width, height,
                new ConvolveParams(coefficients));
    }

    public static void convolveInPlace5x5(Context context, Bitmap bitmap, float[] coefficients) {
        Convolve convolve = new Convolve(Convolve.convolve5x5Script);
        convolve.doComputationInPlace(context, bitmap, new ConvolveParams(coefficients));
    }

    public static Bitmap convolve5x5(Context context, Bitmap bitmap, float[] coefficients) {
        Convolve convolve = new Convolve(Convolve.convolve5x5Script);
        return convolve.doComputation(context, bitmap, new ConvolveParams(coefficients));
    }

    public static void convolveInPlace5x5inPlace(Context context, byte[] nv21ByteArray, int width,
                                                 int height, float[] coefficients) {
        Convolve convolve = new Convolve(Convolve.convolve5x5Script);
        convolve.doComputationInPlace(context, nv21ByteArray, width, height,
                new ConvolveParams(coefficients));
    }

    public static byte[] convolveInPlace5x5(Context context, byte[] nv21ByteArray, int width, int height,
                                            float[] coefficients) {
        Convolve convolve = new Convolve(Convolve.convolve5x5Script);
        return convolve.doComputation(context, nv21ByteArray, width, height,
                new ConvolveParams(coefficients));
    }
}
