package io.github.silvaren.easyrs.tools;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicConvolve3x3;
import android.support.v8.renderscript.ScriptIntrinsicConvolve5x5;
import android.test.ApplicationTestCase;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import io.github.silvaren.easyrs.tools.base.Utils;
import io.github.silvaren.easyrs.tools.params.SampleParams;

@RunWith(AndroidJUnit4.class)
public class ConvolveTest extends ApplicationTestCase<Application> {

    private final Context context = InstrumentationRegistry.getTargetContext();
    private RenderScript rs;

    public ConvolveTest() {
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
    public void shouldApplyConvolve3x3ToBitmapInput() {
        // given
        Nv21Image nv21Image = Utils.generateSample();
        Bitmap bmpFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21Image);
        Bitmap expectedBitmap = getExpectedBitmap3x3(rs, bmpFromNv21);

        // when
        Bitmap output = Convolve.convolve3x3(rs, bmpFromNv21, SampleParams.Convolve.Kernels3x3.SOBEL_X);

        // then
        Assert.assertTrue(output.sameAs(expectedBitmap));
    }

    @Test
    public void shouldApplyConvolve3x3ToNv21Input() {
        // given
        Nv21Image nv21Image = Utils.generateSample();
        Bitmap bmpFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21Image);
        Bitmap expectedBitmap = getExpectedBitmap3x3(rs, bmpFromNv21);
        Nv21Image expectedNv21Image = Nv21Image.bitmapToNV21(rs, expectedBitmap);

        // when
        byte[] output = Convolve.convolve3x3(rs, nv21Image.nv21ByteArray, nv21Image.width, nv21Image.height,
                SampleParams.Convolve.Kernels3x3.SOBEL_X);

        // then
        Assert.assertTrue(Arrays.equals(output, expectedNv21Image.nv21ByteArray));
    }

    @Test
    public void shouldApplyConvolve5x5ToBitmapInput() {
        // given
        Nv21Image nv21Image = Utils.generateSample();
        Bitmap bmpFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21Image);
        Bitmap expectedBitmap = getExpectedBitmap5x5(rs, bmpFromNv21);

        // when
        Bitmap output = Convolve.convolve5x5(rs, bmpFromNv21, SampleParams.Convolve.Kernels5x5.SOBEL_X);

        // then
        Assert.assertTrue(output.sameAs(expectedBitmap));
    }

    @Test
    public void shouldApplyConvolve5x5ToNv21Input() {
        // given
        Nv21Image nv21Image = Utils.generateSample();
        Bitmap bmpFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21Image);
        Bitmap expectedBitmap = getExpectedBitmap5x5(rs, bmpFromNv21);
        Nv21Image expectedNv21Image = Nv21Image.bitmapToNV21(rs, expectedBitmap);

        // when
        byte[] output = Convolve.convolve5x5(rs, nv21Image.nv21ByteArray, nv21Image.width, nv21Image.height,
                SampleParams.Convolve.Kernels5x5.SOBEL_X);

        // then
        Assert.assertTrue(Arrays.equals(output, expectedNv21Image.nv21ByteArray));
    }

    @NonNull
    private Bitmap getExpectedBitmap3x3(RenderScript rs, Bitmap bmpFromNv21) {
        Allocation ain = Allocation.createFromBitmap(rs, bmpFromNv21);
        Allocation aout = Allocation.createTyped(rs, ain.getType());

        ScriptIntrinsicConvolve3x3 convolve3x3Script = ScriptIntrinsicConvolve3x3.create(rs, ain.getElement());
        convolve3x3Script.setInput(ain);
        convolve3x3Script.setCoefficients(SampleParams.Convolve.Kernels3x3.SOBEL_X);
        convolve3x3Script.forEach(aout);

        Bitmap expectedBitmap = Bitmap.createBitmap(bmpFromNv21.getWidth(), bmpFromNv21.getHeight(), bmpFromNv21.getConfig());
        aout.copyTo(expectedBitmap);
        return expectedBitmap;
    }

    @NonNull
    private Bitmap getExpectedBitmap5x5(RenderScript rs, Bitmap bmpFromNv21) {
        Allocation ain = Allocation.createFromBitmap(rs, bmpFromNv21);
        Allocation aout = Allocation.createTyped(rs, ain.getType());

        ScriptIntrinsicConvolve5x5 convolve5x5Script = ScriptIntrinsicConvolve5x5.create(rs, ain.getElement());
        convolve5x5Script.setInput(ain);
        convolve5x5Script.setCoefficients(SampleParams.Convolve.Kernels5x5.SOBEL_X);
        convolve5x5Script.forEach(aout);

        Bitmap expectedBitmap = Bitmap.createBitmap(bmpFromNv21.getWidth(), bmpFromNv21.getHeight(), bmpFromNv21.getConfig());
        aout.copyTo(expectedBitmap);
        return expectedBitmap;
    }
}