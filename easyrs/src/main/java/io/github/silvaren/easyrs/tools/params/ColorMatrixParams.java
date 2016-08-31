package io.github.silvaren.easyrs.tools.params;

import android.support.v8.renderscript.Float4;
import android.support.v8.renderscript.Matrix3f;
import android.support.v8.renderscript.Matrix4f;
import android.support.v8.renderscript.ScriptIntrinsicColorMatrix;

import com.google.common.base.Optional;

public class ColorMatrixParams {

    public static final float U_RANGE_FIX = 1.14678899082569f;
    public static final float V_RANGE_FIX = 0.8130081300813f;
    public static final float[] RGB_TO_YUV_COEFS = new float[]{
            0.299f, 0.587f, 0.114f,
            -0.14713f * U_RANGE_FIX, -0.28886f * U_RANGE_FIX, 0.436f * U_RANGE_FIX,
            0.615f * V_RANGE_FIX, -0.51499f * V_RANGE_FIX, -0.10001f * V_RANGE_FIX};

    interface MatrixSetter {
        void setParams(ScriptIntrinsicColorMatrix scriptIntrinsicColorMatrix);
    }

    public static final ColorMatrixParams GRAYSCALE = new ColorMatrixParams(new MatrixSetter() {
        @Override
        public void setParams(ScriptIntrinsicColorMatrix scriptIntrinsicColorMatrix) {
            scriptIntrinsicColorMatrix.setGreyscale();
        }
    });

    public static final ColorMatrixParams RGB_TO_YUV = new ColorMatrixParams(new MatrixSetter() {
        @Override
        public void setParams(ScriptIntrinsicColorMatrix scriptIntrinsicColorMatrix) {
            scriptIntrinsicColorMatrix.setRGBtoYUV();
            scriptIntrinsicColorMatrix.setAdd(0.0f, 0.5f, 0.5f, 0.0f);
        }
    });

    public static final Matrix3f rgbToNv21Matrix() {
        Matrix3f matrix3f = new Matrix3f(RGB_TO_YUV_COEFS);
        matrix3f.transpose();
        return matrix3f;
    }

    public static final ColorMatrixParams createWithMatrix(final Matrix3f matrix3f,
                                                           final Optional<Float4> addCoefficients) {
        return new ColorMatrixParams(new MatrixSetter() {
            @Override
            public void setParams(ScriptIntrinsicColorMatrix scriptIntrinsicColorMatrix) {
                scriptIntrinsicColorMatrix.setColorMatrix(matrix3f);
                if (addCoefficients.isPresent())
                    scriptIntrinsicColorMatrix.setAdd(addCoefficients.get());
            }
        });
    }

    public static final ColorMatrixParams createWithMatrix(final Matrix4f matrix4f,
                                                           final Optional<Float4> addCoefficients) {
        return new ColorMatrixParams(new MatrixSetter() {
            @Override
            public void setParams(ScriptIntrinsicColorMatrix scriptIntrinsicColorMatrix) {
                scriptIntrinsicColorMatrix.setColorMatrix(matrix4f);
                if (addCoefficients.isPresent())
                    scriptIntrinsicColorMatrix.setAdd(addCoefficients.get());
            }
        });
    }

    private final MatrixSetter matrixSetter;

    ColorMatrixParams(MatrixSetter matrixSetter) {
        this.matrixSetter = matrixSetter;
    }

    public void setColorMatrixParams(ScriptIntrinsicColorMatrix colorMatrixScript) {
        matrixSetter.setParams(colorMatrixScript);
    }


}
