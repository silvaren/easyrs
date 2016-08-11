package silvaren.rstoolbox.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.ScriptIntrinsicColorMatrix;

public class ColorMatrix {

    private static BaseTool.BaseToolScript colorMatrixToolScript = new BaseTool.BaseToolScript<ColorMatrixParams>() {
        @Override
        public void runScript(RSToolboxContext rsToolboxContext, Allocation aout, ColorMatrixParams scriptParams) {
            ScriptIntrinsicColorMatrix colorMatrixScript = ScriptIntrinsicColorMatrix.create(
                    rsToolboxContext.rs, rsToolboxContext.ain.getElement());
            scriptParams.setColorMatrixParams(colorMatrixScript);
            colorMatrixScript.forEach(rsToolboxContext.ain, aout);
        }
    };

    @NonNull
    private static ConvertingTool<ColorMatrixParams> createConvertingTool() {
        BaseTool<ColorMatrixParams> baseTool = new BaseTool<>(colorMatrixToolScript);
        return new ConvertingTool<>(baseTool);
    }

    private static Bitmap doColorMatrixComputation(Context context, Bitmap inputBitmap,
                                                   ColorMatrixParams.Operation op) {
        ConvertingTool<ColorMatrixParams> colorMatrixTool = createConvertingTool();
        return colorMatrixTool.baseTool.doComputation(context, inputBitmap, new ColorMatrixParams(op));
    }

    private static void doColorMatrixComputationInPlace(Context context, Bitmap inputBitmap,
                                                   ColorMatrixParams.Operation op) {
        ConvertingTool<ColorMatrixParams> colorMatrixTool = createConvertingTool();
        colorMatrixTool.baseTool.doComputationInPlace(context, inputBitmap, new ColorMatrixParams(op));
    }

    public static void convertToGrayscaleInPlace(Context context, Bitmap bitmap) {
        doColorMatrixComputationInPlace(context, bitmap, ColorMatrixParams.Operation.GRAYSCALE);
    }

    public static Bitmap doConvertToGrayScale(Context context, Bitmap inputBitmap) {
        return doColorMatrixComputation(context, inputBitmap, ColorMatrixParams.Operation.GRAYSCALE);
    }

    public static Bitmap rgbToYuv(Context context, Bitmap inputBitmap) {
        return doColorMatrixComputation(context, inputBitmap, ColorMatrixParams.Operation.RGB_TO_YUV);
    }

    public static byte[] doConvertToGrayScale(Context context, byte[] nv21ByteArray, int width, int height) {
        ConvertingTool<ColorMatrixParams> convertingTool = createConvertingTool();
        return convertingTool.doComputation(context, nv21ByteArray, width, height,
                new ColorMatrixParams(ColorMatrixParams.Operation.GRAYSCALE));

    }

    public static void doConvertToGrayScaleInPlace(Context context, byte[] nv21ByteArray, int width, int height) {
        ConvertingTool<ColorMatrixParams> convertingTool = createConvertingTool();
        convertingTool.doComputationInPlace(context, nv21ByteArray, width, height,
                new ColorMatrixParams(ColorMatrixParams.Operation.GRAYSCALE));
    }

}
