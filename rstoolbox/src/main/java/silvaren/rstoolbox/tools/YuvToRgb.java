package silvaren.rstoolbox.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicYuvToRGB;
import android.support.v8.renderscript.Type;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import hugo.weaving.DebugLog;

public class YuvToRgb {
    public static Bitmap yuvToRgb(Context context, byte[] nv21ByteArray, int width, int height) {
        return yuvToRgb(context, new Nv21Image(nv21ByteArray, width, height));
    }

    public static Bitmap yuvToRgb(Context context, Nv21Image nv21Image) {
        YuvImage yuvImage = new YuvImage(nv21Image.nv21ByteArray, android.graphics.ImageFormat.NV21,
                nv21Image.width, nv21Image.height, null);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, nv21Image.width, nv21Image.height), 100, os);
        byte[] jpegByteArray = os.toByteArray();
        return BitmapFactory.decodeByteArray(jpegByteArray, 0, jpegByteArray.length);
    }
}
