package silvaren.rstoolbox.tools;

import android.support.v8.renderscript.ScriptIntrinsicColorMatrix;

class ColorMatrixParams {

    enum Operation {
        GRAYSCALE,
        RGB_TO_YUV
    }

    private final Operation op;

    ColorMatrixParams(Operation op) {
        this.op = op;
    }

    public void setColorMatrixParams(ScriptIntrinsicColorMatrix colorMatrixScript) {
        switch (op) {
            case GRAYSCALE:
                colorMatrixScript.setGreyscale();
                break;
            case RGB_TO_YUV:
                setRgbToYuv(colorMatrixScript);
                break;
        }
    }

    private void setRgbToYuv(ScriptIntrinsicColorMatrix colorMatrixScript) {
        colorMatrixScript.setRGBtoYUV();
        colorMatrixScript.setAdd(0.0f, 0.5f, 0.5f, 0.0f);
    }
}
