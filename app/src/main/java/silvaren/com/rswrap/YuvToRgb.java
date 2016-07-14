package silvaren.com.rswrap;

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

class YuvToRgb {
    public static Bitmap yuvToRgb(Context context, Nv21Image nv21Image) {
        RenderScript rs = RenderScript.create(context);
        Element element = Element.U8_4(rs);
        ScriptIntrinsicYuvToRGB yuvToRgbScript = ScriptIntrinsicYuvToRGB.create(rs, element);
        Type.Builder tb = new Type.Builder(rs, Element.createPixel(rs,
                Element.DataType.UNSIGNED_8, Element.DataKind.PIXEL_YUV));
        tb.setX(nv21Image.width);
        tb.setY(nv21Image.height);
        tb.setYuvFormat(android.graphics.ImageFormat.NV21);
        Allocation yuvAllocation = Allocation.createTyped(rs, tb.create(), Allocation.USAGE_SCRIPT);
        Type rgbType = Type.createXY(rs, Element.U8_4(rs), nv21Image.width, nv21Image.height);
        Allocation rgbAllocation = Allocation.createTyped(rs, rgbType);
        yuvAllocation.copyFrom(nv21Image.nv21ByteArray);
        yuvToRgbScript.setInput(yuvAllocation);
        yuvToRgbScript.forEach(rgbAllocation);
        byte[] rgbArray = new byte[nv21Image.width * nv21Image.height * 4];
        rgbAllocation.copyTo(rgbArray);
        IntBuffer intBuf =
                ByteBuffer.wrap(rgbArray)
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .asIntBuffer();
        int[] array = new int[intBuf.remaining()];
        intBuf.get(array);
        return Bitmap.createBitmap(array, nv21Image.width, nv21Image.height, Bitmap.Config.ARGB_8888);
    }
}
