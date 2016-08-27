package silvaren.rstoolbox.client;

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
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicResize;
import android.support.v8.renderscript.Type;
import android.test.ApplicationTestCase;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import silvaren.rstoolbox.tools.Nv21Image;
import silvaren.rstoolbox.tools.Resize;

@RunWith(AndroidJUnit4.class)
public class Nv21ImageTest extends ApplicationTestCase<Application> {

    public static final int TARGET_WIDTH = 50;
    public static final int TARGET_HEIGHT = 50;
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
    public void shouldResizeBitmapInput() {
        // given
        Nv21Image nv21Image = Nv21Image.generateSample();
        Bitmap expectedBitmap = getExpectedBitmap(nv21Image);

        // when
        Bitmap output = Nv21Image.nv21ToBitmap(rs, nv21Image.nv21ByteArray, nv21Image.width, nv21Image.height);

        // then
        int[] expectedPixels = new int[nv21Image.width * nv21Image.height];
        expectedBitmap.getPixels(expectedPixels, 0, nv21Image.width, 0, 0, nv21Image.width, nv21Image.height);
        int[] outputPixels = new int[nv21Image.width * nv21Image.height];
        output.getPixels(outputPixels, 0, nv21Image.width, 0, 0, nv21Image.width, nv21Image.height);
        Assert.assertTrue(Math.sqrt(meanSquareError(outputPixels, expectedPixels)) < 20.0);
    }

    private double meanSquareError(int[] a, int[] b) {
        byte[] aBytes = intArrayAsByteArray(a);
        byte[] bBytes = intArrayAsByteArray(b);

        double sum_sq = 0;

        for (int i = 0; i < aBytes.length; i++)
        {
            int err = bBytes[i] & 0xff - aBytes[i] & 0xff;
            sum_sq += (err * err);
        }
        double mse = (double)sum_sq / (aBytes.length);
        return mse;
    }

    private byte[] intArrayAsByteArray(int[] a) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(a.length * 4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(a);
        return byteBuffer.array();
    }

    @NonNull
    private Bitmap getExpectedBitmap(Nv21Image nv21Image) {
        YuvImage yuvImage = new YuvImage(nv21Image.nv21ByteArray, ImageFormat.NV21, nv21Image.width,
                nv21Image.height, null);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, nv21Image.width, nv21Image.height), 100, os);
        byte[] jpegByteArray = os.toByteArray();
        return BitmapFactory.decodeByteArray(jpegByteArray, 0, jpegByteArray.length);
    }
}