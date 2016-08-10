package silvaren.rstoolbox.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

public class Blur {

    private static BaseTool.BaseToolScript blurToolScript = new BaseTool.BaseToolScript<BlurParams>() {
        @Override
        public void runScript(RSToolboxContext rsToolboxContext, Allocation aout, BlurParams scriptParams) {
            ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(
                    rsToolboxContext.rs, rsToolboxContext.ain.getElement());
            blurScript.setInput(rsToolboxContext.ain);
            blurScript.setRadius(scriptParams.radius);
            blurScript.forEach(aout);
        }
    };

    public static void blurInPlace(Context context, Bitmap bitmap, float radius) {
        BaseTool<BlurParams> blurTool = new BaseTool<>(blurToolScript);
        blurTool.doComputationInPlace(context, bitmap, new BlurParams(radius));
    }

    public static Bitmap blur(Context context, Bitmap inputBitmap, float radius) {
        BaseTool<BlurParams> blurTool = new BaseTool<>(blurToolScript);
        return blurTool.doComputation(context, inputBitmap, new BlurParams(radius));
    }

    public static void blurInPlace(Context context, byte[] nv21ByteArray, int width, int height,
                                   float radius) {
        BaseTool<BlurParams> blurTool = new BaseTool<>(blurToolScript);
        blurTool.doComputationInPlace(context, nv21ByteArray, width, height, new BlurParams(radius));
    }

    public static byte[] blur(Context context, byte[] nv21ByteArray, int width, int height,
                              float radius) {
        BaseTool<BlurParams> blurTool = new BaseTool<>(blurToolScript);
        return blurTool.doComputation(context, nv21ByteArray, width, height, new BlurParams(radius));
    }
}
