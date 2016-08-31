package io.github.silvaren.easyrs.tools.params;

public class SampleParams {

    public static class Lut {
        public static final LutParams.RGBALut negative() {
            int[] rLut = new int[LutParams.LUT_SIZE];
            int[] gLut = new int[LutParams.LUT_SIZE];
            int[] bLut = new int[LutParams.LUT_SIZE];
            int[] aLut = new int[LutParams.LUT_SIZE];
            for (int i = 0; i < LutParams.LUT_SIZE; i++) {
                rLut[i] = LutParams.LUT_SIZE - 1 - i;
                gLut[i] = LutParams.LUT_SIZE - 1 - i;
                bLut[i] = LutParams.LUT_SIZE - 1 - i;
                aLut[i] = i;
            }
            return new LutParams.RGBALut(rLut, gLut, bLut, aLut);
        }
    }

    public static class Lut3D {
        public static final Lut3DParams.Cube swapRedAndBlueCube() {
            final int sx = 32;
            final int sy = 32;
            final int sz = 32;
            int dat[] = new int[sx * sy * sz];
            for (int z = 0; z < sz; z++) {
                for (int y = 0; y < sy; y++) {
                    for (int x = 0; x < sx; x++) {
                        int v = 0xff000000;
                        v |= (0xff * z / (sz - 1));
                        v |= (0xff * y / (sy - 1)) << 8;
                        v |= (0xff * x / (sx - 1)) << 16;
                        dat[z * sy * sx + y * sx + x] = v;
                    }
                }
            }
            return new Lut3DParams.Cube(sx, sy, sz, dat);
        }
    }

    public static class Convolve {
        public static class Kernels5x5 {
            public static float[] SOBEL_X = {
                    1.f, 2.f, 0.f, -2.f, -1.f,
                    4.f, 8.f, 0.f, -8.f, -4.f,
                    6.f, 12.f, 1.f, -12.f, -6.f,
                    4.f, 8.f, 0.f, -8.f, -4.f,
                    1.f, 2.f, 0.f, -2.f, -1.f};
        }

        public static class Kernels3x3 {
            public static float[] SOBEL_X = {
                    1.f, 0.f, -1.f,
                    2.f, 1.f, -2.f,
                    1.f, 0.f, -1.f};
        }
    }

}
