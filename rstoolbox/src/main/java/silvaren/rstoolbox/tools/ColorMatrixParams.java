package silvaren.rstoolbox.tools;

import android.support.v8.renderscript.Float4;
import android.support.v8.renderscript.Matrix3f;
import android.support.v8.renderscript.Matrix4f;
import android.support.v8.renderscript.ScriptIntrinsicColorMatrix;

import com.google.common.base.Optional;

public class ColorMatrixParams {

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
