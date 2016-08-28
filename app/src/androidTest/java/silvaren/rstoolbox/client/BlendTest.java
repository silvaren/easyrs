package silvaren.rstoolbox.client;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlend;
import android.support.v8.renderscript.ScriptIntrinsicResize;
import android.support.v8.renderscript.Type;
import android.test.ApplicationTestCase;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import silvaren.rstoolbox.tools.Blend;
import silvaren.rstoolbox.tools.Blur;
import silvaren.rstoolbox.tools.Nv21Image;
import silvaren.rstoolbox.tools.Resize;

@RunWith(AndroidJUnit4.class)
public class BlendTest extends ApplicationTestCase<Application> {

    private final Context context = InstrumentationRegistry.getTargetContext();
    private RenderScript rs;

    public BlendTest() {
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
    public void shouldAddBitmapInputs() {
        // given
        Nv21Image nv21Image = Nv21Image.generateSample();
        Bitmap bmpFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21Image);
        Bitmap bmpToAdd = Nv21Image.nv21ToBitmap(rs, nv21Image);
        Bitmap bmpToAddExpected = Nv21Image.nv21ToBitmap(rs, nv21Image);
        Bitmap expectedBitmap = getExpectedBitmap(rs, bmpFromNv21, bmpToAddExpected);

        // when
        Blend.add(rs, bmpFromNv21, bmpToAdd);

        // then
        Assert.assertTrue(bmpToAdd.sameAs(expectedBitmap));
    }

    @Test
    public void shouldApplyBlurToNv21Input() {
        // given
        Nv21Image nv21Image = Nv21Image.generateSample();
        Nv21Image nv21ImageToAdd = Nv21Image.generateSample();
        Bitmap bmpFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21Image);
        Bitmap bmpToAddFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21ImageToAdd);
        Bitmap expectedBitmap = getExpectedBitmap(rs, bmpFromNv21, bmpToAddFromNv21);
        Nv21Image expectedNv21Image = Nv21Image.bitmapToNV21(rs, expectedBitmap);

        // when
        Blend.add(rs, nv21Image.nv21ByteArray, nv21Image.width, nv21Image.height, nv21ImageToAdd.nv21ByteArray);

        // then
        Assert.assertTrue(Arrays.equals(nv21ImageToAdd.nv21ByteArray, expectedNv21Image.nv21ByteArray));
    }

    @NonNull
    private Bitmap getExpectedBitmap(RenderScript rs, Bitmap bmpFromNv21, Bitmap bmpToAdd) {
        Allocation ain = Allocation.createFromBitmap(rs, bmpFromNv21);
        Allocation aout = Allocation.createFromBitmap(rs, bmpToAdd);

        ScriptIntrinsicBlend blendScript = ScriptIntrinsicBlend.create(rs, ain.getElement());
        blendScript.forEachAdd(ain, aout);

        Bitmap expectedBitmap = Bitmap.createBitmap(bmpFromNv21.getWidth(), bmpFromNv21.getHeight(), bmpFromNv21.getConfig());
        aout.copyTo(expectedBitmap);
        return expectedBitmap;
    }
}