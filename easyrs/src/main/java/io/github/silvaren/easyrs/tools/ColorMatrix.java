package io.github.silvaren.easyrs.tools;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Float4;
import android.support.v8.renderscript.Matrix3f;
import android.support.v8.renderscript.Matrix4f;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicColorMatrix;

import com.google.common.base.Optional;

import io.github.silvaren.easyrs.tools.base.ConvertingTool;
import io.github.silvaren.easyrs.tools.base.RSToolboxContext;
import io.github.silvaren.easyrs.tools.params.ColorMatrixParams;

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

    private static Bitmap doColorMatrixComputation(RenderScript rs, Bitmap inputBitmap,
                                                   ColorMatrixParams colorMatrixParams) {
        ConvertingTool<ColorMatrixParams> colorMatrixTool = new ConvertingTool<>(colorMatrixToolScript);
        return colorMatrixTool.doComputation(rs, inputBitmap, colorMatrixParams);
    }

    public static Bitmap convertToGrayScale(RenderScript rs, Bitmap inputBitmap) {
        return doColorMatrixComputation(rs, inputBitmap, ColorMatrixParams.GRAYSCALE);
    }

    public static Bitmap rgbToYuv(RenderScript rs, Bitmap inputBitmap) {
        return doColorMatrixComputation(rs, inputBitmap, ColorMatrixParams.RGB_TO_YUV);
    }

    private static Bitmap applyMatrix(RenderScript rs, Bitmap inputBitmap, Matrix3f matrix3f, Optional<Float4> addTerms) {
        ColorMatrixParams matrixParam = ColorMatrixParams.createWithMatrix(matrix3f,
                addTerms);
        return doColorMatrixComputation(rs, inputBitmap, matrixParam);
    }

    private static Bitmap applyMatrix(RenderScript rs, Bitmap inputBitmap, Matrix4f matrix4f, Optional<Float4> addTerms) {
        ColorMatrixParams matrixParam = ColorMatrixParams.createWithMatrix(matrix4f,
                addTerms);
        return doColorMatrixComputation(rs, inputBitmap, matrixParam);
    }

    private static byte[] applyMatrix(RenderScript rs, byte[] nv21ByteArray, int width, int height,
                                      Matrix3f matrix3f, Optional<Float4> addTerms) {
        ConvertingTool<ColorMatrixParams> convertingTool = new ConvertingTool<>(colorMatrixToolScript);
        ColorMatrixParams matrixParam = ColorMatrixParams.createWithMatrix(matrix3f,
                addTerms);
        return convertingTool.doComputation(rs, nv21ByteArray, width, height, matrixParam);
    }

    private static byte[] applyMatrix(RenderScript rs, byte[] nv21ByteArray, int width, int height,
                                      Matrix4f matrix4f, Optional<Float4> addTerms) {
        ConvertingTool<ColorMatrixParams> convertingTool = new ConvertingTool<>(colorMatrixToolScript);
        ColorMatrixParams matrixParam = ColorMatrixParams.createWithMatrix(matrix4f,
                addTerms);
        return convertingTool.doComputation(rs, nv21ByteArray, width, height, matrixParam);
    }

    public static Bitmap applyMatrix(RenderScript rs, Bitmap inputBitmap, Matrix3f matrix3f) {
        return applyMatrix(rs, inputBitmap, matrix3f, Optional.<Float4>absent());
    }

    public static Bitmap applyMatrix(RenderScript rs, Bitmap inputBitmap, Matrix4f matrix4f) {
        return applyMatrix(rs, inputBitmap, matrix4f, Optional.<Float4>absent());
    }

    public static Bitmap applyMatrix(RenderScript rs, Bitmap inputBitmap, Matrix3f matrix3f, Float4 addTerms) {
        return applyMatrix(rs, inputBitmap, matrix3f, Optional.of(addTerms));
    }

    public static Bitmap applyMatrix(RenderScript rs, Bitmap inputBitmap, Matrix4f matrix4f, Float4 addTerms) {
        return applyMatrix(rs, inputBitmap, matrix4f, Optional.of(addTerms));
    }

    public static byte[] applyMatrix(RenderScript rs, byte[] nv21ByteArray, int width, int height,
                                     Matrix3f matrix3f) {
        return applyMatrix(rs, nv21ByteArray, width, height, matrix3f, Optional.<Float4>absent());
    }

    public static byte[] applyMatrix(RenderScript rs, byte[] nv21ByteArray, int width, int height,
                                     Matrix4f matrix4f) {
        return applyMatrix(rs, nv21ByteArray, width, height, matrix4f, Optional.<Float4>absent());
    }

    public static byte[] applyMatrix(RenderScript rs, byte[] nv21ByteArray, int width, int height,
                                     Matrix3f matrix3f, Float4 addTerms) {
        return applyMatrix(rs, nv21ByteArray, width, height, matrix3f, Optional.of(addTerms));
    }

    public static byte[] applyMatrix(RenderScript rs, byte[] nv21ByteArray, int width, int height,
                                     Matrix4f matrix4f, Float4 addTerms) {
        return applyMatrix(rs, nv21ByteArray, width, height, matrix4f, Optional.of(addTerms));
    }

    public static byte[] convertToGrayScale(RenderScript rs, byte[] nv21ByteArray, int width,
                                            int height) {
        ConvertingTool<ColorMatrixParams> convertingTool = new ConvertingTool<>(colorMatrixToolScript);
        return convertingTool.doComputation(rs, nv21ByteArray, width, height,
                ColorMatrixParams.GRAYSCALE);

    }
}
