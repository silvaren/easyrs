package io.github.silvaren.easyrs.tools;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsic3DLUT;

import io.github.silvaren.easyrs.tools.base.ConvertingTool;
import io.github.silvaren.easyrs.tools.base.RSToolboxContext;
import io.github.silvaren.easyrs.tools.params.Lut3DParams;

public class Lut3D {

    private static ConvertingTool.BaseToolScript lut3DToolScript = new ConvertingTool.BaseToolScript<Lut3DParams>() {
        @Override
        public void runScript(RSToolboxContext rsToolboxContext, Allocation aout, Lut3DParams scriptParams) {
            ScriptIntrinsic3DLUT script3dLut = ScriptIntrinsic3DLUT.create(
                    rsToolboxContext.rs, rsToolboxContext.ain.getElement());
            script3dLut.setLUT(scriptParams.cube.createAllocation(rsToolboxContext.rs));
            script3dLut.forEach(rsToolboxContext.ain, aout);
        }
    };

    public static Bitmap apply3dLut(RenderScript rs, Bitmap inputBitmap, Lut3DParams.Cube cube) {
        ConvertingTool<Lut3DParams> lutTool = new ConvertingTool<>(lut3DToolScript);
        return lutTool.doComputation(rs, inputBitmap,
                new Lut3DParams(cube));
    }

    public static byte[] apply3dLut(RenderScript rs, byte[] nv21ByteArray, int width, int height,
                                    Lut3DParams.Cube cube) {
        ConvertingTool<Lut3DParams> lutTool = new ConvertingTool<>(lut3DToolScript);
        return lutTool.doComputation(rs, nv21ByteArray, width, height,
                new Lut3DParams(cube));
    }

}
