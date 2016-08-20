package silvaren.rstoolbox.tools;

import android.support.v8.renderscript.ScriptIntrinsicLUT;

class LutParams {

    public static final RGBALut negative() {
        int[] rLut = new int[256];
        int[] gLut = new int[256];
        int[] bLut = new int[256];
        int[] aLut = new int[256];
        for (int i = 0; i < 256; i++) {
            rLut[i] = 255 - i;
            gLut[i] = 255 - i;
            bLut[i] = 255 - i;
            aLut[i] = i;
        }
        return new RGBALut(rLut, gLut, bLut, aLut);
    }

    private final RGBALut rgbaLut;

    LutParams(RGBALut rgbaLut) {
        this.rgbaLut = rgbaLut;
    }


    public void setLutParams(ScriptIntrinsicLUT lutScript) {
        for (int i = 0; i < 256; i++) {
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
