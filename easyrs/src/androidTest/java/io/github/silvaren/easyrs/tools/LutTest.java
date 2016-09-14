package io.github.silvaren.easyrs.tools;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicLUT;
import android.test.ApplicationTestCase;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import io.github.silvaren.easyrs.tools.base.Utils;
import io.github.silvaren.easyrs.tools.params.LutParams;
import io.github.silvaren.easyrs.tools.params.SampleParams;

@RunWith(AndroidJUnit4.class)
public class LutTest extends ApplicationTestCase<Application> {

    private final Context context = InstrumentationRegistry.getTargetContext();
    private RenderScript rs;

    public LutTest() {
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
    public void shouldApplyLutToBitmapInput() {
        // given
        Nv21Image nv21Image = Utils.generateSample();
        Bitmap bmpFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21Image);
        Bitmap expectedBitmap = getExpectedBitmap(rs, bmpFromNv21);

        // when
        Bitmap output = Lut.applyLut(rs, bmpFromNv21, SampleParams.Lut.negative());

        // then
        Assert.assertTrue(output.sameAs(expectedBitmap));
    }

    @Test
    public void shouldApplyLutToNv21Input() {
        // given
        Nv21Image nv21Image = Utils.generateSample();
        Bitmap bmpFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21Image);
        Bitmap expectedBitmap = getExpectedBitmap(rs, bmpFromNv21);
        Nv21Image expectedNv21Image = Nv21Image.bitmapToNV21(rs, expectedBitmap);

        // when
        byte[] output = Lut.applyLut(rs, nv21Image.nv21ByteArray, nv21Image.width, nv21Image.height, SampleParams.Lut.negative());

        // then
        Assert.assertTrue(Arrays.equals(output, expectedNv21Image.nv21ByteArray));
    }

    @NonNull
    private Bitmap getExpectedBitmap(RenderScript rs, Bitmap bmpFromNv21) {
        Allocation ain = Allocation.createFromBitmap(rs, bmpFromNv21);
        Allocation aout = Allocation.createTyped(rs, ain.getType());

        ScriptIntrinsicLUT lutScript = ScriptIntrinsicLUT.create(rs, ain.getElement());
        for (int i = 0; i < LutParams.LUT_SIZE; i++) {
            LutParams.RGBALut rgbaLut = SampleParams.Lut.negative();
            lutScript.setAlpha(i, rgbaLut.aLut[i]);
            lutScript.setRed(i, rgbaLut.rLut[i]);
            lutScript.setGreen(i, rgbaLut.gLut[i]);
            lutScript.setBlue(i, rgbaLut.bLut[i]);
        }
        lutScript.forEach(ain, aout);

        Bitmap expectedBitmap = Bitmap.createBitmap(bmpFromNv21.getWidth(), bmpFromNv21.getHeight(), bmpFromNv21.getConfig());
        aout.copyTo(expectedBitmap);
        return expectedBitmap;
    }
}