package silvaren.rstoolbox.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.ScriptIntrinsicColorMatrix;
import android.support.v8.renderscript.ScriptIntrinsicLUT;

public class Lut {

    private static BaseTool.BaseToolScript lutToolScript = new BaseTool.BaseToolScript<LutParams>() {
        @Override
        public void runScript(RSToolboxContext rsToolboxContext, Allocation aout, LutParams scriptParams) {
            ScriptIntrinsicLUT lutScript = ScriptIntrinsicLUT.create(rsToolboxContext.rs,
                    rsToolboxContext.ain.getElement());
            scriptParams.setLutParams(lutScript);
            lutScript.forEach(rsToolboxContext.ain, aout);
        }
    };

    @NonNull
    private static ConvertingTool<LutParams> createConvertingTool() {
        BaseTool<LutParams> baseTool = new BaseTool<>(lutToolScript);
        return new ConvertingTool<>(baseTool);
    }

    public static Bitmap negativeEffect(Context context, Bitmap inputBitmap) {
        ConvertingTool<LutParams> lutTool = createConvertingTool();
        return lutTool.baseTool.doComputation(context, inputBitmap,
                new LutParams(LutParams.Operation.NEGATIVE));
    }

    public static byte[] negativeEffect(Context context, byte[] nv21ByteArray, int width, int height) {
        ConvertingTool<LutParams> lutTool = createConvertingTool();
        return lutTool.doComputation(context, nv21ByteArray, width, height,
                new LutParams(LutParams.Operation.NEGATIVE));
    }

    private static class LutParams {

        enum Operation {
            NEGATIVE
        }

        private final Operation op;

        LutParams(Operation op) {
            this.op = op;
        }

        public void setLutParams(ScriptIntrinsicLUT lutScript) {
            switch (op) {
                case NEGATIVE: setNegativeEffect(lutScript);
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
}
