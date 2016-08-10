package silvaren.rstoolbox.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

public class Blur extends BaseTool<BlurParams> {

    public Blur() {
        super(tool);
    }

    public static void blurInPlace(Context context, Bitmap bitmap, float radius) {
        Blur blurTool = new Blur();
        blurTool.doComputationInPlace(context, bitmap, new BlurParams(radius));
    }

    public static Bitmap blur(Context context, Bitmap inputBitmap, float radius) {
        Blur blurTool = new Blur();
        return blurTool.doComputation(context, inputBitmap, new BlurParams(radius));
    }

    public static void blurInPlace(Context context, byte[] nv21ByteArray, int width, int height,
                                   float radius) {
        Blur blurTool = new Blur();
        blurTool.doComputationInPlace(context, nv21ByteArray, width, height, new BlurParams(radius));
    }

    public static byte[] blur(Context context, byte[] nv21ByteArray, int width, int height,
                              float radius) {
        Blur blurTool = new Blur();
        return blurTool.doComputation(context, nv21ByteArray, width, height, new BlurParams(radius));
    }

    @Override
    protected void runScript(RSToolboxContext rsToolboxContext, Allocation aout, BlurParams scriptParams) {
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(
                rsToolboxContext.rs, rsToolboxContext.ain.getElement());
        blurScript.setInput(rsToolboxContext.ain);
        blurScript.setRadius(scriptParams.radius);
        blurScript.forEach(aout);
    }
}
