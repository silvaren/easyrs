package io.github.silvaren.easyrs.tools;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicResize;
import android.support.v8.renderscript.Type;
import android.test.ApplicationTestCase;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import io.github.silvaren.easyrs.tools.base.Utils;

@RunWith(AndroidJUnit4.class)
public class ResizeTest extends ApplicationTestCase<Application> {

    public static final int TARGET_WIDTH = 50;
    public static final int TARGET_HEIGHT = 50;
    private final Context context = InstrumentationRegistry.getTargetContext();
    private RenderScript rs;

    public ResizeTest() {
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
    public void shouldResizeBitmapInput() {
        // given
        Nv21Image nv21Image = Utils.generateSample();
        Bitmap bmpFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21Image);
        Bitmap expectedBitmap = getExpectedBitmap(rs, bmpFromNv21);

        // when
        Bitmap output = Resize.resize(rs, bmpFromNv21, TARGET_WIDTH, TARGET_HEIGHT);

        // then
        Assert.assertTrue(output.sameAs(expectedBitmap));
    }

    @Test
    public void shouldResizeNv21Input() {
        // given
        Nv21Image nv21Image = Utils.generateSample();
        Bitmap bmpFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21Image);
        Bitmap expectedBitmap = getExpectedBitmap(rs, bmpFromNv21);
        Nv21Image expectedNv21Image = Nv21Image.bitmapToNV21(rs, expectedBitmap);

        // when
        byte[] output = Resize.resize(rs, nv21Image.nv21ByteArray, nv21Image.width, nv21Image.height, TARGET_WIDTH, TARGET_HEIGHT);

        // then
        Assert.assertTrue(Arrays.equals(output, expectedNv21Image.nv21ByteArray));
    }

    @NonNull
    private Bitmap getExpectedBitmap(RenderScript rs, Bitmap bmpFromNv21) {
        Allocation ain = Allocation.createFromBitmap(rs, bmpFromNv21);
        Bitmap expectedBitmap = Bitmap.createBitmap(TARGET_WIDTH, TARGET_HEIGHT, bmpFromNv21.getConfig());
        Type outType = Type.createXY(rs, ain.getElement(), TARGET_WIDTH, TARGET_HEIGHT);
        Allocation aout = Allocation.createTyped(rs, outType);

        ScriptIntrinsicResize resizeScript = ScriptIntrinsicResize.create(rs);
        resizeScript.setInput(ain);
        resizeScript.forEach_bicubic(aout);

        aout.copyTo(expectedBitmap);
        return expectedBitmap;
    }
}