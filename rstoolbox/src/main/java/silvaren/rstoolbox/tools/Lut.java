package silvaren.rstoolbox.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.ScriptIntrinsicLUT;

import hugo.weaving.DebugLog;

public class Lut {

    private static BaseTool.BaseToolScript lutToolScript = new BaseTool.BaseToolScript<LutParams>() {
        @Override
        public void runScript(RSToolboxContext rsToolboxContext, Allocation aout, LutParams scriptParams) {
            ScriptIntrinsicLUT lutScript = ScriptIntrinsicLUT.create(rsToolboxContext.rs,
                    rsToolboxContext.ain.getElement());
            scriptParams.setLutParams(lutScript);
            lutScript.forEach(rsToolboxContext.ain, aout);
        }
    };

    @NonNull
    private static ConvertingTool<LutParams> createConvertingTool() {
        BaseTool<LutParams> baseTool = new BaseTool<>(lutToolScript);
        return new ConvertingTool<>(baseTool);
    }

    @DebugLog
    public static Bitmap negativeEffect(Context context, Bitmap inputBitmap) {
        ConvertingTool<LutParams> lutTool = createConvertingTool();
        return lutTool.baseTool.doComputation(context, inputBitmap,
                new LutParams(LutParams.Operation.NEGATIVE));
    }

    @DebugLog
    public static byte[] negativeEffect(Context context, byte[] nv21ByteArray, int width, int height) {
        ConvertingTool<LutParams> lutTool = createConvertingTool();
        return lutTool.doComputation(context, nv21ByteArray, width, height,
                new LutParams(LutParams.Operation.NEGATIVE));
    }

}
