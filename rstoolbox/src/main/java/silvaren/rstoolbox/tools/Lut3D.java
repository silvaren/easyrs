package silvaren.rstoolbox.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsic3DLUT;
import android.support.v8.renderscript.Type;

public class Lut3D {

    private static BaseTool.BaseToolScript lut3DToolScript = new BaseTool.BaseToolScript<Lut3DParams>() {
        @Override
        public void runScript(RSToolboxContext rsToolboxContext, Allocation aout, Lut3DParams scriptParams) {
            ScriptIntrinsic3DLUT script3dLut = ScriptIntrinsic3DLUT.create(
                    rsToolboxContext.rs, rsToolboxContext.ain.getElement());
            scriptParams.setLut(rsToolboxContext.rs, script3dLut);
            script3dLut.forEach(rsToolboxContext.ain, aout);
        }
    };

    @NonNull
    private static ConvertingTool<Lut3DParams> createConvertingTool() {
        BaseTool<Lut3DParams> baseTool = new BaseTool<>(lut3DToolScript);
        return new ConvertingTool<>(baseTool);
    }

    public static Bitmap do3dLut(Context context, Bitmap inputBitmap) {
        ConvertingTool<Lut3DParams> lutTool = createConvertingTool();
        return lutTool.baseTool.doComputation(context, inputBitmap,
                new Lut3DParams());
    }

    public static byte[] do3dLut(Context context, byte[] nv21ByteArray, int width, int height) {
        ConvertingTool<Lut3DParams> lutTool = createConvertingTool();
        return lutTool.doComputation(context, nv21ByteArray, width, height,
                new Lut3DParams());
    }

    private static class Lut3DParams {

        private static Allocation initSampleCube(RenderScript rs) {
            final int sx = 2;
            final int sy = 2;
            final int sz = 2;
            Type.Builder tb = new Type.Builder(rs, Element.U8_4(rs));
            tb.setX(sx);
            tb.setY(sy);
            tb.setZ(sz);
            Type t = tb.create();
            Allocation cube = Allocation.createTyped(rs, t);
            int dat[] = new int[sx * sy * sz];
            dat[0] = 0xffffffff;
            dat[7] = 0xff000000;
            cube.copyFromUnchecked(dat);

            return cube;
        }


        public void setLut(RenderScript rs, ScriptIntrinsic3DLUT script3dLut) {
            script3dLut.setLUT(initSampleCube(rs));
        }
    }
}
