package silvaren.rstoolbox.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.Log;

import hugo.weaving.DebugLog;

public class Blur {

    static class BlurParams {
        public final float radius;
        public BlurParams(float radius) {
            this.radius = radius;
        }
    }

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

    @NonNull
    private static ConvertingTool<BlurParams> createConvertingTool() {
        BaseTool<BlurParams> baseTool = new BaseTool<>(blurToolScript);
        return new ConvertingTool<>(baseTool);
    }

    @DebugLog
    public static Bitmap blur(Context context, Bitmap inputBitmap, float radius) {
        ConvertingTool<BlurParams> blurTool = createConvertingTool();
        return blurTool.baseTool.doComputation(context, inputBitmap, new BlurParams(radius));
    }

    public static byte[] blur(Context context, byte[] nv21ByteArray, int width, int height,
                              float radius) {
        ConvertingTool<BlurParams> blurTool = createConvertingTool();
        return blurTool.doComputation(context, nv21ByteArray, width, height, new BlurParams(radius));
    }
}
