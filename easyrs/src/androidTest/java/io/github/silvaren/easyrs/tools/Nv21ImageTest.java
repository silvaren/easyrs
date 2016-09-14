package io.github.silvaren.easyrs.tools;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v8.renderscript.RenderScript;
import android.test.ApplicationTestCase;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;

import io.github.silvaren.easyrs.tools.base.Utils;

@RunWith(AndroidJUnit4.class)
public class Nv21ImageTest extends ApplicationTestCase<Application> {

    private final Context context = InstrumentationRegistry.getTargetContext();
    private RenderScript rs;

    public Nv21ImageTest() {
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
    public void shouldConvertNv21InputToBitmap() {
        // given
        Nv21Image nv21Image = Utils.generateSample();
        Bitmap expectedBitmap = getConvertedBitmap(nv21Image);

        // when
        Bitmap output = Nv21Image.nv21ToBitmap(rs, nv21Image.nv21ByteArray, nv21Image.width, nv21Image.height);

        // then
        int[] expectedPixels = new int[nv21Image.width * nv21Image.height];
        expectedBitmap.getPixels(expectedPixels, 0, nv21Image.width, 0, 0, nv21Image.width, nv21Image.height);
        int[] outputPixels = new int[nv21Image.width * nv21Image.height];
        output.getPixels(outputPixels, 0, nv21Image.width, 0, 0, nv21Image.width, nv21Image.height);
        Assert.assertTrue(Math.sqrt(Utils.meanSquareErrorRgb8888(outputPixels, expectedPixels)) < 20.0);
    }

    @Test
    public void shouldConvertOddSizedNv21InputToBitmap() {
        // given
        int oddWidth = 51;
        int oddHeight = 49;
        Bitmap bitmap = Bitmap.createBitmap(oddWidth, oddHeight, Bitmap.Config.ARGB_8888);

        // when
        Nv21Image nv21FromBitmap = Nv21Image.bitmapToNV21(rs, bitmap);

        // then
        Assert.assertEquals(nv21FromBitmap.width, 50);
        Assert.assertEquals(nv21FromBitmap.height, 48);
    }

    @NonNull
    private Bitmap getConvertedBitmap(Nv21Image nv21Image) {
        YuvImage yuvImage = new YuvImage(nv21Image.nv21ByteArray, ImageFormat.NV21, nv21Image.width,
                nv21Image.height, null);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, nv21Image.width, nv21Image.height), 100, os);
        byte[] jpegByteArray = os.toByteArray();
        return BitmapFactory.decodeByteArray(jpegByteArray, 0, jpegByteArray.length);
    }
}