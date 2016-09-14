package io.github.silvaren.easyrs.tools;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.test.ApplicationTestCase;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import io.github.silvaren.easyrs.tools.base.Utils;

@RunWith(AndroidJUnit4.class)
public class BlurTest extends ApplicationTestCase<Application> {

    public static final float RADIUS = 25.f;
    private final Context context = InstrumentationRegistry.getTargetContext();
    private RenderScript rs;

    public BlurTest() {
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
    public void shouldApplyBlurToBitmapInput() {
        // given
        Nv21Image nv21Image = Utils.generateSample();
        Bitmap bmpFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21Image);
        Bitmap expectedBitmap = getExpectedBitmap(rs, bmpFromNv21);

        // when
        Bitmap output = Blur.blur(rs, bmpFromNv21, RADIUS);

        // then
        Assert.assertTrue(output.sameAs(expectedBitmap));
    }

    @Test
    public void shouldApplyBlurToNv21Input() {
        // given
        Nv21Image nv21Image = Utils.generateSample();
        Bitmap bmpFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21Image);
        Bitmap expectedBitmap = getExpectedBitmap(rs, bmpFromNv21);
        Nv21Image expectedNv21Image = Nv21Image.bitmapToNV21(rs, expectedBitmap);

        // when
        byte[] output = Blur.blur(rs, nv21Image.nv21ByteArray, nv21Image.width, nv21Image.height, RADIUS);

        // then
        Assert.assertTrue(Arrays.equals(output, expectedNv21Image.nv21ByteArray));
    }

    @NonNull
    private Bitmap getExpectedBitmap(RenderScript rs, Bitmap bmpFromNv21) {
        Allocation ain = Allocation.createFromBitmap(rs, bmpFromNv21);
        Allocation aout = Allocation.createTyped(rs, ain.getType());

        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(
                rs, ain.getElement());
        blurScript.setInput(ain);
        blurScript.setRadius(RADIUS);
        blurScript.forEach(aout);

        Bitmap expectedBitmap = Bitmap.createBitmap(bmpFromNv21.getWidth(), bmpFromNv21.getHeight(), bmpFromNv21.getConfig());
        aout.copyTo(expectedBitmap);
        return expectedBitmap;
    }
}