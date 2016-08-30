package silvaren.easyrs.tools;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicLUT;

import hugo.weaving.DebugLog;
import silvaren.easyrs.tools.base.ConvertingTool;
import silvaren.easyrs.tools.base.RSToolboxContext;
import silvaren.easyrs.tools.params.LutParams;

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
    public static Bitmap applyLut(RenderScript rs, Bitmap inputBitmap, LutParams.RGBALut rgbaLut) {
        ConvertingTool<LutParams> lutTool = new ConvertingTool<>(lutToolScript);
        return lutTool.doComputation(rs, inputBitmap,
                new LutParams(rgbaLut));
    }

    public static byte[] applyLut(RenderScript rs, byte[] nv21ByteArray, int width,
                                        int height, LutParams.RGBALut rgbaLut) {
        ConvertingTool<LutParams> lutTool = new ConvertingTool<>(lutToolScript);
        return lutTool.doComputation(rs, nv21ByteArray, width, height,
                new LutParams(rgbaLut));
    }

}
