package silvaren.rstoolbox.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.ScriptIntrinsic3DLUT;

import hugo.weaving.DebugLog;

public class Lut3D {

    private static ConvertingTool.BaseToolScript lut3DToolScript = new ConvertingTool.BaseToolScript<Lut3DParams>() {
        @Override
        public void runScript(RSToolboxContext rsToolboxContext, Allocation aout, Lut3DParams scriptParams) {
            ScriptIntrinsic3DLUT script3dLut = ScriptIntrinsic3DLUT.create(
                    rsToolboxContext.rs, rsToolboxContext.ain.getElement());
            scriptParams.setLut(rsToolboxContext.rs, script3dLut);
            script3dLut.forEach(rsToolboxContext.ain, aout);
        }
    };

    @DebugLog
    public static Bitmap do3dLut(Context context, Bitmap inputBitmap, Lut3DParams.Cube cube) {
        ConvertingTool<Lut3DParams> lutTool = new ConvertingTool<>(lut3DToolScript);
        return lutTool.doComputation(context, inputBitmap,
                new Lut3DParams(cube));
    }

    public static byte[] do3dLut(Context context, byte[] nv21ByteArray, int width, int height,
                                 Lut3DParams.Cube cube) {
        ConvertingTool<Lut3DParams> lutTool = new ConvertingTool<>(lut3DToolScript);
        return lutTool.doComputation(context, nv21ByteArray, width, height,
                new Lut3DParams(cube));
    }

}
