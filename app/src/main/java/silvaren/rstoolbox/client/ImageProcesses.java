package silvaren.rstoolbox.client;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.HashMap;
import java.util.Map;

import silvaren.rstoolbox.tools.Blend;
import silvaren.rstoolbox.tools.Blur;
import silvaren.rstoolbox.tools.ColorMatrix;
import silvaren.rstoolbox.tools.Convolve;
import silvaren.rstoolbox.tools.ConvolveParams;
import silvaren.rstoolbox.tools.Histogram;
import silvaren.rstoolbox.tools.Lut;
import silvaren.rstoolbox.tools.Lut3D;
import silvaren.rstoolbox.tools.Lut3DParams;
import silvaren.rstoolbox.tools.Nv21Image;
import silvaren.rstoolbox.tools.Resize;
import silvaren.rstoolbox.tools.Utils;

class ImageProcesses {

    enum ImageFormat {
        BITMAP(0),
        NV21(1);

        private final int id;

        ImageFormat(int id) {
            this.id = id;
        }

        public static ImageFormat valueOf(int progress) {
            ImageFormat[] values = values();
            for (ImageFormat format : values) {
                if (format.id == progress)
                    return format;
            }
            return BITMAP;
        }
    }

    public interface ImageProcess {
        Bitmap processImage(Context context, Bitmap bitmap, ImageFormat imageFormat);
    }

    static Map<String, Integer> flavorMap(Context context) {
        HashMap<String, Integer> flavorMap = new HashMap<>();
        flavorMap.put(context.getString(R.string.colormatrix), R.array.colormatrix_array);
        flavorMap.put(context.getString(R.string.convolve), R.array.convolve_array);
        flavorMap.put(context.getString(R.string.histogram), R.array.histogram_array);
        return flavorMap;
    }

    static Map<String, ImageProcess> processMap(Context context) {
        HashMap<String, ImageProcess> processMap = new HashMap<>();
        processMap.put(context.getString(R.string.original), originalProcess);
        processMap.put(context.getString(R.string.blend), blendProcess);
        processMap.put(context.getString(R.string.blur), blurProcess);
        processMap.put(context.getString(R.string.grayscale), colorMatrixGraycaleProcess);
        processMap.put(context.getString(R.string.rgbtoyuv), colorMatrixRgbtoYuvProcess);
        processMap.put(context.getString(R.string.sobel3x3), convolveSobel3x3Process);
        processMap.put(context.getString(R.string.sobel5x5), convolveSobel5x5Process);
        processMap.put(context.getString(R.string.rgba_histogram), rgbaHistogramProcess);
        processMap.put(context.getString(R.string.lum_histogram), lumHistogramProcess);
        processMap.put(context.getString(R.string.lut), lutProcess);
        processMap.put(context.getString(R.string.lut3d), lut3dProcess);
        processMap.put(context.getString(R.string.resize), resizeProcess);
        return processMap;
    }

    private static ImageProcess originalProcess = new ImageProcess() {
        @Override
        public Bitmap processImage(Context context, Bitmap bitmap, ImageFormat imageFormat) {
            return bitmap;
        }
    };

    private static ImageProcess blendProcess = new ImageProcess() {
        @Override
        public Bitmap processImage(Context context, Bitmap bitmap, ImageFormat imageFormat) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            options.inDither = false;
            options.inPurgeable = true;
            Bitmap sampleEdgeBitmap = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.sample_edge, options);
            if (imageFormat == ImageFormat.BITMAP) {
                Blend.add(context, bitmap, sampleEdgeBitmap);
                return sampleEdgeBitmap;
            } else {
                Nv21Image nv21Image = Nv21Image.bitmapToNV21(context, bitmap);
                Nv21Image dstNv21Image = Nv21Image.bitmapToNV21(context, sampleEdgeBitmap);
                Blend.add(context, nv21Image.nv21ByteArray, nv21Image.width, nv21Image.height,
                        dstNv21Image.nv21ByteArray);
                return Nv21Image.nv21ToBitmap(context, dstNv21Image);
            }
        }
    };

    private static ImageProcess blurProcess = new ImageProcess() {
        @Override
        public Bitmap processImage(Context context, Bitmap bitmap, ImageFormat imageFormat) {
            if (imageFormat == ImageFormat.BITMAP)
                return Blur.blur(context, bitmap, 25.f);
            else {
                Nv21Image nv21Image = Nv21Image.bitmapToNV21(context, bitmap);
                byte[] output = Blur.blur(context, nv21Image.nv21ByteArray, nv21Image.width,
                        nv21Image.height, 25.f);
                return Nv21Image.nv21ToBitmap(context, output, nv21Image.width, nv21Image.height);
            }
        }
    };

    private static ImageProcess colorMatrixRgbtoYuvProcess = new ImageProcess() {
        @Override
        public Bitmap processImage(Context context, Bitmap bitmap, ImageFormat imageFormat) {
            return ColorMatrix.rgbToYuv(context, bitmap);
        }
    };

    private static ImageProcess colorMatrixGraycaleProcess = new ImageProcess() {
        @Override
        public Bitmap processImage(Context context, Bitmap bitmap, ImageFormat imageFormat) {
            if (imageFormat == ImageFormat.BITMAP)
                return ColorMatrix.convertToGrayScale(context, bitmap);
            else {
                Nv21Image nv21Image = Nv21Image.bitmapToNV21(context, bitmap);
                byte[] output = ColorMatrix.convertToGrayScale(context, nv21Image.nv21ByteArray,
                        nv21Image.width, nv21Image.height);
                return Nv21Image.nv21ToBitmap(context, output, nv21Image.width, nv21Image.height);
            }
        }
    };

    private static ImageProcess convolveSobel3x3Process = new ImageProcess() {
        @Override
        public Bitmap processImage(Context context, Bitmap bitmap, ImageFormat imageFormat) {
            if (imageFormat == ImageFormat.BITMAP)
                return Convolve.convolve3x3(context, bitmap, ConvolveParams.Kernels3x3.SOBEL_X);
            else {
                Nv21Image nv21Image = Nv21Image.bitmapToNV21(context, bitmap);
                byte[] output = Convolve.convolve3x3(context, nv21Image.nv21ByteArray,
                        nv21Image.width, nv21Image.height, ConvolveParams.Kernels3x3.SOBEL_X);
                return Nv21Image.nv21ToBitmap(context, output, nv21Image.width, nv21Image.height);
            }
        }
    };


    private static ImageProcess convolveSobel5x5Process = new ImageProcess() {
        @Override
        public Bitmap processImage(Context context, Bitmap bitmap, ImageFormat imageFormat) {
            if (imageFormat == ImageFormat.BITMAP)
                return Convolve.convolve5x5(context, bitmap, ConvolveParams.Kernels5x5.SOBEL_X);
            else {
                Nv21Image nv21Image = Nv21Image.bitmapToNV21(context, bitmap);
                byte[] output = Convolve.convolve5x5(context, nv21Image.nv21ByteArray,
                        nv21Image.width, nv21Image.height, ConvolveParams.Kernels5x5.SOBEL_X);
                return Nv21Image.nv21ToBitmap(context, output, nv21Image.width, nv21Image.height);
            }
        }
    };

    private static ImageProcess rgbaHistogramProcess = new ImageProcess() {
        @Override
        public Bitmap processImage(Context context, Bitmap bitmap, ImageFormat imageFormat) {
            int[] histograms;
            if (imageFormat == ImageFormat.BITMAP)
                histograms = Histogram.rgbaHistograms(context, bitmap);
            else {
                Nv21Image nv21Image = Nv21Image.bitmapToNV21(context, bitmap);
                histograms = Histogram.rgbaHistograms(context, nv21Image.nv21ByteArray,
                        nv21Image.width, nv21Image.height);
            }
            return Utils.drawHistograms(histograms, 4);
        }
    };

    private static ImageProcess lumHistogramProcess = new ImageProcess() {
        @Override
        public Bitmap processImage(Context context, Bitmap bitmap, ImageFormat imageFormat) {
            int[] histograms;
            if (imageFormat == ImageFormat.BITMAP)
                histograms = Histogram.luminanceHistogram(context, bitmap);
            else {
                Nv21Image nv21Image = Nv21Image.bitmapToNV21(context, bitmap);
                histograms = Histogram.luminanceHistogram(context, nv21Image.nv21ByteArray,
                        nv21Image.width, nv21Image.height);
            }
            return Utils.drawHistograms(histograms, 1);
        }
    };

    private static ImageProcess lutProcess = new ImageProcess() {
        @Override
        public Bitmap processImage(Context context, Bitmap bitmap, ImageFormat imageFormat) {
            if (imageFormat == ImageFormat.BITMAP)
                return Lut.negativeEffect(context, bitmap);
            else {
                Nv21Image nv21Image = Nv21Image.bitmapToNV21(context, bitmap);
                byte[] output = Lut.negativeEffect(context, nv21Image.nv21ByteArray,
                        nv21Image.width, nv21Image.height);
                return Nv21Image.nv21ToBitmap(context, output, nv21Image.width, nv21Image.height);
            }
        }
    };

    private static ImageProcess lut3dProcess = new ImageProcess() {
        @Override
        public Bitmap processImage(Context context, Bitmap bitmap, ImageFormat imageFormat) {
            if (imageFormat == ImageFormat.BITMAP)
                return Lut3D.do3dLut(context, bitmap, Lut3DParams.swapRedAndBlueCube());
            else {
                Nv21Image nv21Image = Nv21Image.bitmapToNV21(context, bitmap);
                byte[] output = Lut3D.do3dLut(context, nv21Image.nv21ByteArray, nv21Image.width,
                        nv21Image.height, Lut3DParams.swapRedAndBlueCube());
                return Nv21Image.nv21ToBitmap(context, output, nv21Image.width, nv21Image.height);
            }
        }
    };

    private static ImageProcess resizeProcess = new ImageProcess() {
        @Override
        public Bitmap processImage(Context context, Bitmap bitmap, ImageFormat imageFormat) {
            if (imageFormat == ImageFormat.BITMAP)
                return Resize.resize(context, bitmap, 50, 50);
            else {
                Nv21Image nv21Image = Nv21Image.bitmapToNV21(context, bitmap);
                byte[] output = Resize.resize(context, nv21Image.nv21ByteArray, nv21Image.width,
                        nv21Image.height, 50,50);
                return Nv21Image.nv21ToBitmap(context, output, 50, 50);
            }
        }
    };

}
