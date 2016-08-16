package silvaren.rstoolbox.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicYuvToRGB;
import android.support.v8.renderscript.Type;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import hugo.weaving.DebugLog;

class YuvToRgb {
    public static Bitmap yuvToRgb(Context context, byte[] nv21ByteArray, int width, int height) {
        return yuvToRgb(context, new Nv21Image(nv21ByteArray, width, height));
    }

    public static Bitmap yuvToRgb(Context context, Nv21Image nv21Image) {
        RenderScript rs = RenderScript.create(context);

        Type.Builder tb = new Type.Builder(rs, Element.createPixel(rs,
                Element.DataType.UNSIGNED_8, Element.DataKind.PIXEL_YUV));
        tb.setX(nv21Image.width);
        tb.setY(nv21Image.height);
        tb.setYuvFormat(android.graphics.ImageFormat.NV21);
        Allocation yuvAllocation = Allocation.createTyped(rs, tb.create(), Allocation.USAGE_SCRIPT);
        yuvAllocation.copyFrom(nv21Image.nv21ByteArray);

        Type rgbType = Type.createXY(rs, Element.RGBA_8888(rs), nv21Image.width, nv21Image.height);
        Allocation rgbAllocation = Allocation.createTyped(rs, rgbType);

        ScriptIntrinsicYuvToRGB yuvToRgbScript = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
        yuvToRgbScript.setInput(yuvAllocation);
        yuvToRgbScript.forEach(rgbAllocation);

        Bitmap bitmap = Bitmap.createBitmap(nv21Image.width, nv21Image.height, Bitmap.Config.ARGB_8888);
        rgbAllocation.copyTo(bitmap);

        return bitmap;
    }
}
