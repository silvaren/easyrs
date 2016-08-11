package silvaren.rstoolbox.tools;

import android.support.v8.renderscript.ScriptIntrinsicLUT;

class LutParams {

    enum Operation {
        NEGATIVE
    }

    private final Operation op;

    LutParams(Operation op) {
        this.op = op;
    }

    public void setLutParams(ScriptIntrinsicLUT lutScript) {
        switch (op) {
            case NEGATIVE:
                setNegativeEffect(lutScript);
                break;
        }
    }

    private void setNegativeEffect(ScriptIntrinsicLUT lutScript) {
        for (int i = 0; i < 256; i++) {
            lutScript.setAlpha(i, i);
            lutScript.setRed(i, 255 - i);
            lutScript.setGreen(i, 255 - i);
            lutScript.setBlue(i, 255 - i);
        }
    }
}
