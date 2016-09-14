package io.github.silvaren.easyrs.tools;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicHistogram;
import android.test.ApplicationTestCase;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import io.github.silvaren.easyrs.tools.base.Utils;

@RunWith(AndroidJUnit4.class)
public class HistogramTest extends ApplicationTestCase<Application> {

    private final Context context = InstrumentationRegistry.getTargetContext();
    private RenderScript rs;

    public HistogramTest() {
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
    public void shouldComputeLuminanceHistogramOfBitmapInput() {
        // given
        Nv21Image nv21Image = Utils.generateSample();
        Bitmap bmpFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21Image);
        int[] expectedHistogram = getExpectedHistogram(rs, bmpFromNv21, Op.LUMINANCE);

        // when
        int[] output = Histogram.luminanceHistogram(rs, bmpFromNv21);

        // then
        Assert.assertTrue(Arrays.equals(output, expectedHistogram));
    }

    @Test
    public void shouldComputeLuminanceHistogramOfNv21Input() {
        // given
        Nv21Image nv21Image = Utils.generateSample();
        Bitmap bmpFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21Image);
        int[] expectedHistogram = getExpectedHistogram(rs, bmpFromNv21, Op.LUMINANCE);

        // when
        int[] output = Histogram.luminanceHistogram(rs, nv21Image.nv21ByteArray, nv21Image.width, nv21Image.height);

        // then
        Assert.assertTrue(Arrays.equals(output, expectedHistogram));
    }

    @Test
    public void shouldComputeRgbaHistogramOfBitmapInput() {
        // given
        Nv21Image nv21Image = Utils.generateSample();
        Bitmap bmpFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21Image);
        int[] expectedHistogram = getExpectedHistogram(rs, bmpFromNv21, Op.RGBA);

        // when
        int[] output = Histogram.rgbaHistograms(rs, bmpFromNv21);

        // then
        Assert.assertTrue(Arrays.equals(output, expectedHistogram));
    }

    @Test
    public void shouldComputeRgbaHistogramOfNv21Input() {
        // given
        Nv21Image nv21Image = Utils.generateSample();
        Bitmap bmpFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21Image);
        int[] expectedHistogram = getExpectedHistogram(rs, bmpFromNv21, Op.RGBA);

        // when
        int[] output = Histogram.rgbaHistograms(rs, nv21Image.nv21ByteArray, nv21Image.width, nv21Image.height);

        // then
        Assert.assertTrue(Arrays.equals(output, expectedHistogram));
    }


    enum Op {
        LUMINANCE,
        RGBA
    }

    @NonNull
    private int[] getExpectedHistogram(RenderScript rs, Bitmap bmpFromNv21, Op op) {
        Allocation ain = Allocation.createFromBitmap(rs, bmpFromNv21);
        Allocation aout;
        int[] histogram;
        if (op == Op.LUMINANCE) {
            aout = Allocation.createSized(rs, Element.I32(rs), Histogram.COLOR_DEPTH);
            histogram = new int[Histogram.COLOR_DEPTH];
        }
        else {
            aout = Allocation.createSized(rs, Element.I32_4(rs), Histogram.COLOR_DEPTH);
            histogram = new int[Histogram.COLOR_DEPTH * Histogram.CHANNELS];
        }

        ScriptIntrinsicHistogram histogramScript = ScriptIntrinsicHistogram.create(
                rs, ain.getElement());
        histogramScript.setOutput(aout);
        histogramScript.forEach(ain);

        aout.copyTo(histogram);
        return histogram;
    }
}