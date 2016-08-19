package silvaren.rstoolbox.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.ScriptIntrinsicColorMatrix;

import hugo.weaving.DebugLog;

public class ColorMatrix {

    private static ConvertingTool.BaseToolScript colorMatrixToolScript = new ConvertingTool.BaseToolScript<ColorMatrixParams>() {
        @Override
        public void runScript(RSToolboxContext rsToolboxContext, Allocation aout, ColorMatrixParams scriptParams) {
            ScriptIntrinsicColorMatrix colorMatrixScript = ScriptIntrinsicColorMatrix.create(
                    rsToolboxContext.rs, rsToolboxContext.ain.getElement());
            scriptParams.setColorMatrixParams(colorMatrixScript);
            colorMatrixScript.forEach(rsToolboxContext.ain, aout);
        }
    };

    private static Bitmap doColorMatrixComputation(Context context, Bitmap inputBitmap,
                                                   ColorMatrixParams.Operation op) {
        ConvertingTool<ColorMatrixParams> colorMatrixTool = new ConvertingTool<>(colorMatrixToolScript);
        return colorMatrixTool.doComputation(context, inputBitmap, new ColorMatrixParams(op));
    }

    @DebugLog
    public static Bitmap convertToGrayScale(Context context, Bitmap inputBitmap) {
        return doColorMatrixComputation(context, inputBitmap, ColorMatrixParams.Operation.GRAYSCALE);
    }

    @DebugLog
    public static Bitmap rgbToYuv(Context context, Bitmap inputBitmap) {
        return doColorMatrixComputation(context, inputBitmap, ColorMatrixParams.Operation.RGB_TO_YUV);
    }

    public static byte[] convertToGrayScale(Context context, byte[] nv21ByteArray, int width, int height) {
        ConvertingTool<ColorMatrixParams> convertingTool = new ConvertingTool<>(colorMatrixToolScript);
        return convertingTool.doComputation(context, nv21ByteArray, width, height,
                new ColorMatrixParams(ColorMatrixParams.Operation.GRAYSCALE));

    }
}
