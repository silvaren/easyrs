package io.github.silvaren.easyrs.tools.params;

import android.support.v8.renderscript.ScriptIntrinsicLUT;

public class LutParams {

    public static final int LUT_SIZE = 256;

    private final RGBALut rgbaLut;

    public LutParams(RGBALut rgbaLut) {
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
        public final int[] rLut;
        public final int[] gLut;
        public final int[] bLut;
        public final int[] aLut;

        RGBALut(int[] rLut, int[] gLut, int[] bLut, int[] aLut) {
            this.rLut = rLut;
            this.gLut = gLut;
            this.bLut = bLut;
            this.aLut = aLut;
        }
    }
}
