package io.github.silvaren.easyrs.tools;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Float4;
import android.support.v8.renderscript.Matrix3f;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicColorMatrix;
import android.test.ApplicationTestCase;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import io.github.silvaren.easyrs.tools.base.Utils;

@RunWith(AndroidJUnit4.class)
public class ColorMatrixTest extends ApplicationTestCase<Application> {

    private final Context context = InstrumentationRegistry.getTargetContext();
    private RenderScript rs;

    private static final Matrix3f MATRIX3F = new Matrix3f(new float[]{0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f});
    private static final Matrix3f MATRIX4F = new Matrix3f(new float[]{0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f,
            0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.4f, 1.5f});
    private static final Float4 ADD_TERMS = new Float4(0.01f, 0.02f, 0.03f, 0.04f);

    public ColorMatrixTest() {
        super(Application.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        setContext(context);
        createApplication();
        rs = RenderScript.create(getApplication());
    }

    @Test
    public void shouldConvertBitmapInputToGrayscale() {
        // given
        Nv21Image nv21Image = Utils.generateSample();
        Bitmap bmpFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21Image);
        Bitmap expectedBitmap = getExpectedBitmap(rs, bmpFromNv21, Op.GRAYSCALE);

        // when
        Bitmap output = ColorMatrix.convertToGrayScale(rs, bmpFromNv21);

        // then
        Assert.assertTrue(output.sameAs(expectedBitmap));
    }

    @Test
    public void shouldConvertNv21InputToGrayscale() {
        // given
        Nv21Image nv21Image = Utils.generateSample();
        Bitmap bmpFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21Image);
        Bitmap expectedBitmap = getExpectedBitmap(rs, bmpFromNv21, Op.GRAYSCALE);
        Nv21Image expectedNv21Image = Nv21Image.bitmapToNV21(rs, expectedBitmap);

        // when
        byte[] output = ColorMatrix.convertToGrayScale(rs, nv21Image.nv21ByteArray, nv21Image.width, nv21Image.height);

        // then
        Assert.assertTrue(Arrays.equals(output, expectedNv21Image.nv21ByteArray));
    }

    @Test
    public void shouldConvertBitmapInputToYuv() {
        // given
        Nv21Image nv21Image = Utils.generateSample();
        Bitmap bmpFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21Image);
        Bitmap expectedBitmap = getExpectedBitmap(rs, bmpFromNv21, Op.RGB_TO_YUV);

        // when
        Bitmap output = ColorMatrix.rgbToYuv(rs, bmpFromNv21);

        // then
        Assert.assertTrue(output.sameAs(expectedBitmap));
    }

    @Test
    public void shouldApplyColorMatrix3fToBitmapInput() {
        // given
        Nv21Image nv21Image = Utils.generateSample();
        Bitmap bmpFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21Image);
        Bitmap expectedBitmap = getExpectedBitmap(rs, bmpFromNv21, Op.MATRIX3F);

        // when
        Bitmap output = ColorMatrix.applyMatrix(rs, bmpFromNv21, MATRIX3F, ADD_TERMS);

        // then
        Assert.assertTrue(output.sameAs(expectedBitmap));
    }

    @Test
    public void shouldApplyColorMatrix4fToBitmapInput() {
        // given
        Nv21Image nv21Image = Utils.generateSample();
        Bitmap bmpFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21Image);
        Bitmap expectedBitmap = getExpectedBitmap(rs, bmpFromNv21, Op.MATRIX4F);

        // when
        Bitmap output = ColorMatrix.applyMatrix(rs, bmpFromNv21, MATRIX4F, ADD_TERMS);

        // then
        Assert.assertTrue(output.sameAs(expectedBitmap));
    }

    @Test
    public void shouldApplyColorMatrix3fToNv21Input() {
        // given
        Nv21Image nv21Image = Utils.generateSample();
        Bitmap bmpFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21Image);
        Bitmap expectedBitmap = getExpectedBitmap(rs, bmpFromNv21, Op.MATRIX3F);
        Nv21Image expectedNv21Image = Nv21Image.bitmapToNV21(rs, expectedBitmap);

        // when
        byte[] output = ColorMatrix.applyMatrix(rs, nv21Image.nv21ByteArray, nv21Image.width,
                nv21Image.height, MATRIX3F, ADD_TERMS);

        // then
        Assert.assertTrue(Arrays.equals(output, expectedNv21Image.nv21ByteArray));
    }

    @Test
    public void shouldApplyColorMatrix4fToNv21Input() {
        // given
        Nv21Image nv21Image = Utils.generateSample();
        Bitmap bmpFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21Image);
        Bitmap expectedBitmap = getExpectedBitmap(rs, bmpFromNv21, Op.MATRIX4F);
        Nv21Image expectedNv21Image = Nv21Image.bitmapToNV21(rs, expectedBitmap);

        // when
        byte[] output = ColorMatrix.applyMatrix(rs, nv21Image.nv21ByteArray, nv21Image.width,
                nv21Image.height, MATRIX4F, ADD_TERMS);

        // then
        Assert.assertTrue(Arrays.equals(output, expectedNv21Image.nv21ByteArray));
    }


    enum Op {
        GRAYSCALE,
        RGB_TO_YUV,
        MATRIX3F,
        MATRIX4F
    }

    @NonNull
    private Bitmap getExpectedBitmap(RenderScript rs, Bitmap bmpFromNv21, Op op) {
        Allocation ain = Allocation.createFromBitmap(rs, bmpFromNv21);
        Allocation aout = Allocation.createTyped(rs, ain.getType());

        ScriptIntrinsicColorMatrix colorMatrixScript = ScriptIntrinsicColorMatrix.create(
                rs, ain.getElement());
        if (op == Op.GRAYSCALE)
            colorMatrixScript.setGreyscale();
        if (op == Op.RGB_TO_YUV) {
            colorMatrixScript.setRGBtoYUV();
            colorMatrixScript.setAdd(0.0f, 0.5f, 0.5f, 0.0f);
        }
        if (op == Op.MATRIX3F) {
            colorMatrixScript.setColorMatrix(MATRIX3F);
            colorMatrixScript.setAdd(ADD_TERMS);
        }
        if (op == Op.MATRIX4F) {
            colorMatrixScript.setColorMatrix(MATRIX4F);
            colorMatrixScript.setAdd(ADD_TERMS);
        }

        colorMatrixScript.forEach(ain, aout);

        Bitmap expectedBitmap = Bitmap.createBitmap(bmpFromNv21.getWidth(), bmpFromNv21.getHeight(), bmpFromNv21.getConfig());
        aout.copyTo(expectedBitmap);
        return expectedBitmap;
    }
}