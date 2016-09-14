package io.github.silvaren.easyrs.tools;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsic3DLUT;
import android.test.ApplicationTestCase;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import io.github.silvaren.easyrs.tools.base.Utils;
import io.github.silvaren.easyrs.tools.params.SampleParams;

@RunWith(AndroidJUnit4.class)
public class Lut3DTest extends ApplicationTestCase<Application> {

    private final Context context = InstrumentationRegistry.getTargetContext();
    private RenderScript rs;

    public Lut3DTest() {
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
    public void shouldApplyLut3DToBitmapInput() {
        // given
        Nv21Image nv21Image = Utils.generateSample();
        Bitmap bmpFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21Image);
        Bitmap expectedBitmap = getExpectedBitmap(rs, bmpFromNv21);

        // when
        Bitmap output = Lut3D.apply3dLut(rs, bmpFromNv21, SampleParams.Lut3D.swapRedAndBlueCube());

        // then
        Assert.assertTrue(output.sameAs(expectedBitmap));
    }

    @Test
    public void shouldApplyLut3DToNv21Input() {
        // given
        Nv21Image nv21Image = Utils.generateSample();
        Bitmap bmpFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21Image);
        Bitmap expectedBitmap = getExpectedBitmap(rs, bmpFromNv21);
        Nv21Image expectedNv21Image = Nv21Image.bitmapToNV21(rs, expectedBitmap);

        // when
        byte[] output = Lut3D.apply3dLut(rs, nv21Image.nv21ByteArray, nv21Image.width, nv21Image.height,
                SampleParams.Lut3D.swapRedAndBlueCube());

        // then
        Assert.assertTrue(Arrays.equals(output, expectedNv21Image.nv21ByteArray));
    }

    @NonNull
    private Bitmap getExpectedBitmap(RenderScript rs, Bitmap bmpFromNv21) {
        Allocation ain = Allocation.createFromBitmap(rs, bmpFromNv21);
        Allocation aout = Allocation.createTyped(rs, ain.getType());

        ScriptIntrinsic3DLUT script3dLut = ScriptIntrinsic3DLUT.create(rs, ain.getElement());
        script3dLut.setLUT(SampleParams.Lut3D.swapRedAndBlueCube().createAllocation(rs));
        script3dLut.forEach(ain, aout);

        Bitmap expectedBitmap = Bitmap.createBitmap(bmpFromNv21.getWidth(), bmpFromNv21.getHeight(), bmpFromNv21.getConfig());
        aout.copyTo(expectedBitmap);
        return expectedBitmap;
    }
}