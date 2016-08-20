package silvaren.rstoolbox.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Float4;
import android.support.v8.renderscript.Matrix3f;
import android.support.v8.renderscript.Matrix4f;
import android.support.v8.renderscript.ScriptIntrinsicColorMatrix;

import com.google.common.base.Optional;

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
                                                   ColorMatrixParams colorMatrixParams) {
        ConvertingTool<ColorMatrixParams> colorMatrixTool = new ConvertingTool<>(colorMatrixToolScript);
        return colorMatrixTool.doComputation(context, inputBitmap, colorMatrixParams);
    }

    @DebugLog
    public static Bitmap convertToGrayScale(Context context, Bitmap inputBitmap) {
        return doColorMatrixComputation(context, inputBitmap, ColorMatrixParams.GRAYSCALE);
    }

    @DebugLog
    public static Bitmap rgbToYuv(Context context, Bitmap inputBitmap) {
        return doColorMatrixComputation(context, inputBitmap, ColorMatrixParams.RGB_TO_YUV);
    }

    private static Bitmap applyMatrix(Context context, Bitmap inputBitmap, Matrix3f matrix3f, Optional<Float4> addTerms) {
        ColorMatrixParams matrixParam = ColorMatrixParams.createWithMatrix(matrix3f,
                addTerms);
        return doColorMatrixComputation(context, inputBitmap, matrixParam);
    }

    private static Bitmap applyMatrix(Context context, Bitmap inputBitmap, Matrix4f matrix4f, Optional<Float4> addTerms) {
        ColorMatrixParams matrixParam = ColorMatrixParams.createWithMatrix(matrix4f,
                addTerms);
        return doColorMatrixComputation(context, inputBitmap, matrixParam);
    }

    private static byte[] applyMatrix(Context context, byte[] nv21ByteArray, int width, int height,
                                      Matrix3f matrix3f, Optional<Float4> addTerms) {
        ConvertingTool<ColorMatrixParams> convertingTool = new ConvertingTool<>(colorMatrixToolScript);
        ColorMatrixParams matrixParam = ColorMatrixParams.createWithMatrix(matrix3f,
                Optional.<Float4>absent());
        return convertingTool.doComputation(context, nv21ByteArray, width, height, matrixParam);
    }

    private static byte[] applyMatrix(Context context, byte[] nv21ByteArray, int width, int height,
                                      Matrix4f matrix4f, Optional<Float4> addTerms) {
        ConvertingTool<ColorMatrixParams> convertingTool = new ConvertingTool<>(colorMatrixToolScript);
        ColorMatrixParams matrixParam = ColorMatrixParams.createWithMatrix(matrix4f,
                Optional.<Float4>absent());
        return convertingTool.doComputation(context, nv21ByteArray, width, height, matrixParam);
    }

    @DebugLog
    public static Bitmap applyMatrix(Context context, Bitmap inputBitmap, Matrix3f matrix3f) {
        return applyMatrix(context, inputBitmap, matrix3f, Optional.<Float4>absent());
    }

    @DebugLog
    public static Bitmap applyMatrix(Context context, Bitmap inputBitmap, Matrix4f matrix4f) {
        return applyMatrix(context, inputBitmap, matrix4f, Optional.<Float4>absent());
    }

    @DebugLog
    public static Bitmap applyMatrix(Context context, Bitmap inputBitmap, Matrix3f matrix3f, Float4 addTerms) {
        return applyMatrix(context, inputBitmap, matrix3f, Optional.of(addTerms));
    }

    @DebugLog
    public static Bitmap applyMatrix(Context context, Bitmap inputBitmap, Matrix4f matrix4f, Float4 addTerms) {
        return applyMatrix(context, inputBitmap, matrix4f, Optional.of(addTerms));
    }

    public static byte[] applyMatrix(Context context, byte[] nv21ByteArray, int width, int height,
                                     Matrix3f matrix3f) {
        return applyMatrix(context, nv21ByteArray, width, height, matrix3f, Optional.<Float4>absent());
    }

    public static byte[] applyMatrix(Context context, byte[] nv21ByteArray, int width, int height,
                                     Matrix4f matrix4f) {
        return applyMatrix(context, nv21ByteArray, width, height, matrix4f, Optional.<Float4>absent());
    }

    public static byte[] applyMatrix(Context context, byte[] nv21ByteArray, int width, int height,
                                     Matrix3f matrix3f, Float4 addTerms) {
        return applyMatrix(context, nv21ByteArray, width, height, matrix3f, Optional.of(addTerms));
    }

    public static byte[] applyMatrix(Context context, byte[] nv21ByteArray, int width, int height,
                                     Matrix4f matrix4f, Float4 addTerms) {
        return applyMatrix(context, nv21ByteArray, width, height, matrix4f, Optional.of(addTerms));
    }

    public static byte[] convertToGrayScale(Context context, byte[] nv21ByteArray, int width,
                                            int height) {
        ConvertingTool<ColorMatrixParams> convertingTool = new ConvertingTool<>(colorMatrixToolScript);
        return convertingTool.doComputation(context, nv21ByteArray, width, height,
                ColorMatrixParams.GRAYSCALE);

    }
}
