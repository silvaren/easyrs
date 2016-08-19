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

        return bitmap;
    }
}
