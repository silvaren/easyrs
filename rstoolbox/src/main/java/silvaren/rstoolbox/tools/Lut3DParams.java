package silvaren.rstoolbox.tools;

import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsic3DLUT;
import android.support.v8.renderscript.Type;

class Lut3DParams {

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
