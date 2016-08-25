package silvaren.rstoolbox.client;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.test.ActivityTestCase;
import android.test.AndroidTestCase;
import android.test.ApplicationTestCase;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import silvaren.rstoolbox.tools.Blur;
import silvaren.rstoolbox.tools.Nv21Image;

@RunWith(AndroidJUnit4.class)
public class BlurTest extends ApplicationTestCase<Application> {

    private final Context context = InstrumentationRegistry.getTargetContext();

    public BlurTest() {
        super(Application.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        setContext(context);
        createApplication();
    }

    @Test
    public void testShouldApplyBlurToBitmapInput() {
        // given
        RenderScript rs = RenderScript.create(getApplication());
        Nv21Image nv21Image = Nv21Image.generateSample();
        Bitmap bmpFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21Image);
        Allocation ain = Allocation.createFromBitmap(rs, bmpFromNv21);
        Allocation aout = Allocation.createTyped(rs, ain.getType());

        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(
                rs, ain.getElement());
        blurScript.setInput(ain);
        blurScript.setRadius(25.f);
        blurScript.forEach(aout);

        Bitmap expectedBitmap = Bitmap.createBitmap(bmpFromNv21.getWidth(), bmpFromNv21.getHeight(), bmpFromNv21.getConfig());
        aout.copyTo(expectedBitmap);

        // when
        Bitmap output = Blur.blur(rs, bmpFromNv21, 25.f);

        // then
        Assert.assertTrue(output.sameAs(expectedBitmap));
    }

    @Test
    public void shouldApplyBlurToNv21Input() {
        // given
        RenderScript rs = RenderScript.create(getApplication().getApplicationContext());
        Nv21Image nv21Image = Nv21Image.generateSample();
        Bitmap bmpFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21Image);
        Allocation ain = Allocation.createFromBitmap(rs, bmpFromNv21);
        Allocation aout = Allocation.createTyped(rs, ain.getType());

        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(
                rs, ain.getElement());
        blurScript.setInput(ain);
        blurScript.setRadius(25.f);
        blurScript.forEach(aout);

        Bitmap expectedBitmap = Bitmap.createBitmap(bmpFromNv21.getWidth(), bmpFromNv21.getHeight(), bmpFromNv21.getConfig());
        aout.copyTo(expectedBitmap);
        Nv21Image expectedNv21Image = Nv21Image.bitmapToNV21(rs, expectedBitmap);

        // when
        byte[] output = Blur.blur(rs, nv21Image.nv21ByteArray, nv21Image.width, nv21Image.height, 25.f);

        // then
        Assert.assertTrue(Arrays.equals(output, expectedNv21Image.nv21ByteArray));
    }
}