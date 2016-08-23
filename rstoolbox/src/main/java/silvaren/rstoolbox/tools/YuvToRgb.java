package silvaren.rstoolbox.tools;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicYuvToRGB;
import android.support.v8.renderscript.Type;
import android.util.Log;

public class YuvToRgb {
    public static Bitmap yuvToRgb(RenderScript rs, byte[] nv21ByteArray, int width, int height) {
        return yuvToRgb(rs, new Nv21Image(nv21ByteArray, width, height));
    }

    public static Bitmap yuvToRgb(RenderScript rs, Nv21Image nv21Image) {
        long startTime = System.currentTimeMillis();

        Type.Builder yuvTypeBuilder = new Type.Builder(rs, Element.U8(rs))
                .setX(nv21Image.nv21ByteArray.length);
        Type yuvType = yuvTypeBuilder.create();
        Allocation yuvAllocation = Allocation.createTyped(rs, yuvType, Allocation.USAGE_SCRIPT);
        yuvAllocation.copyFrom(nv21Image.nv21ByteArray);

        Type.Builder rgbTypeBuilder = new Type.Builder(rs, Element.RGBA_8888(rs));
        rgbTypeBuilder.setX(nv21Image.width);
        rgbTypeBuilder.setY(nv21Image.height);
        Allocation rgbAllocation = Allocation.createTyped(rs, rgbTypeBuilder.create());

        ScriptIntrinsicYuvToRGB yuvToRgbScript = ScriptIntrinsicYuvToRGB.create(rs, Element.RGBA_8888(rs));
        yuvToRgbScript.setInput(yuvAllocation);
        yuvToRgbScript.forEach(rgbAllocation);

        Bitmap bitmap = Bitmap.createBitmap(nv21Image.width, nv21Image.height, Bitmap.Config.ARGB_8888);
        rgbAllocation.copyTo(bitmap);

        Log.d("NV21", "Conversion to Bitmap: " + (System.currentTimeMillis() - startTime) + "ms");
        return bitmap;
    }
}
