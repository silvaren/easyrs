package silvaren.rstoolbox.tools;

import android.support.v8.renderscript.ScriptIntrinsicLUT;

public class LutParams {

    public static final int LUT_SIZE = 256;

    public static final RGBALut negative() {
        int[] rLut = new int[LUT_SIZE];
        int[] gLut = new int[LUT_SIZE];
        int[] bLut = new int[LUT_SIZE];
        int[] aLut = new int[LUT_SIZE];
        for (int i = 0; i < LUT_SIZE; i++) {
            rLut[i] = LUT_SIZE - 1 - i;
            gLut[i] = LUT_SIZE - 1 - i;
            bLut[i] = LUT_SIZE - 1 - i;
            aLut[i] = i;
        }
        return new RGBALut(rLut, gLut, bLut, aLut);
    }

    private final RGBALut rgbaLut;

    LutParams(RGBALut rgbaLut) {
        this.rgbaLut = rgbaLut;
    }


    public void setLutParams(ScriptIntrinsicLUT lutScript) {
        for (int i = 0; i < LUT_SIZE; i++) {
            lutScript.setAlpha(i, rgbaLut.aLut[i]);
            lutScript.setRed(i, rgbaLut.rLut[i]);
            lutScript.setGreen(i, rgbaLut.gLut[i]);
            lutScript.setBlue(i, rgbaLut.bLut[i]);
        }
    }

    public static class RGBALut {
        private final int[] rLut;
        private final int[] gLut;
        private final int[] bLut;
        private final int[] aLut;

        private RGBALut(int[] rLut, int[] gLut, int[] bLut, int[] aLut) {
            this.rLut = rLut;
            this.gLut = gLut;
            this.bLut = bLut;
            this.aLut = aLut;
        }
    }
}
