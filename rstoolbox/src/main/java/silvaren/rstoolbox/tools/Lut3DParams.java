package silvaren.rstoolbox.tools;

import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsic3DLUT;
import android.support.v8.renderscript.Type;

public class Lut3DParams {

    public static final Cube nopCube() {
        final int sx = 32;
        final int sy = 32;
        final int sz = 16;
        int dat[] = new int[sx * sy * sz];
        for (int z = 0; z < sz; z++) {
            for (int y = 0; y < sy; y++) {
                for (int x = 0; x < sx; x++ ) {
                    int v = 0xff000000;
                    v |= (0xff * x / (sx - 1));
                    v |= (0xff * y / (sy - 1)) << 8;
                    v |= (0xff * z / (sz - 1)) << 16;
                    dat[z*sy*sx + y*sx + x] = v;
                }
            }
        }
        return new Cube(sx, sy, sz, dat);
    }

    private final Cube cube;

    Lut3DParams(Cube cube) {
        this.cube = cube;
    }

    static class Cube {
        private final int[] cube;
        private final int xSize;
        private final int ySize;
        private final int zSize;

        Cube(int xSize, int ySize, int zSize) {
            this.cube = new int[xSize * ySize * zSize];
            this.xSize = xSize;
            this.ySize = ySize;
            this.zSize = zSize;
        }

        Cube(int xSize, int ySize, int zSize, int[] cube) {
            this.cube = cube;
            this.xSize = xSize;
            this.ySize = ySize;
            this.zSize = zSize;
        }

        public int getRGBA(int x, int y, int z) {
            return cube[z*ySize*xSize + y*xSize + x];
        }

        public void setRGB(int x, int y, int z, int rgba) {
            cube[z*ySize*xSize + y*xSize + x] = rgba;
        }

        public int[] getCube() {
            return cube;
        }
    }

    private static Allocation initCubeAllocation(RenderScript rs, Cube cube) {
        final int sx = cube.xSize;
        final int sy = cube.ySize;
        final int sz = cube.zSize;
        Type.Builder tb = new Type.Builder(rs, Element.U8_4(rs));
        tb.setX(sx);
        tb.setY(sy);
        tb.setZ(sz);
        Type t = tb.create();
        Allocation mCube = Allocation.createTyped(rs, t);
        mCube.copyFromUnchecked(cube.getCube());

        return mCube;
    }


    public void setLut(RenderScript rs, ScriptIntrinsic3DLUT script3dLut) {
        script3dLut.setLUT(initCubeAllocation(rs, cube));
    }
}
