package silvaren.rstoolbox.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.ScriptIntrinsicLUT;

import hugo.weaving.DebugLog;

public class Lut {

    private static ConvertingTool.BaseToolScript lutToolScript = new ConvertingTool.BaseToolScript<LutParams>() {
        @Override
        public void runScript(RSToolboxContext rsToolboxContext, Allocation aout, LutParams scriptParams) {
            ScriptIntrinsicLUT lutScript = ScriptIntrinsicLUT.create(rsToolboxContext.rs,
                    rsToolboxContext.ain.getElement());
            scriptParams.setLutParams(lutScript);
            lutScript.forEach(rsToolboxContext.ain, aout);
        }
    };

    @DebugLog
    public static Bitmap negativeEffect(Context context, Bitmap inputBitmap) {
        ConvertingTool<LutParams> lutTool = new ConvertingTool<>(lutToolScript);
        return lutTool.doComputation(context, inputBitmap,
                new LutParams(LutParams.Operation.NEGATIVE));
    }

    public static byte[] negativeEffect(Context context, byte[] nv21ByteArray, int width, int height) {
        ConvertingTool<LutParams> lutTool = new ConvertingTool<>(lutToolScript);
        return lutTool.doComputation(context, nv21ByteArray, width, height,
                new LutParams(LutParams.Operation.NEGATIVE));
    }

}
