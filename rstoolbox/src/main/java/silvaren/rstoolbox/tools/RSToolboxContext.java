package silvaren.rstoolbox.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.Type;

public class RSToolboxContext {
    public final RenderScript rs;
    public final Allocation ain;
    public final Element element;

    private RSToolboxContext(RenderScript rs, Allocation ain, Element bitmapElement) {
        this.rs = rs;
        this.ain = ain;
        this.element = bitmapElement;
    }

    public static RSToolboxContext createFromBitmap(Context context, Bitmap bitmap) {
        RenderScript rs = RenderScript.create(context);
        Allocation ain = Allocation.createFromBitmap(rs, bitmap);
        Element bitmapElement = ain.getElement();

        return new RSToolboxContext(rs, ain, bitmapElement);
    }

    public static RSToolboxContext createFromNv21Image(Context context, byte[] nv21ByteArray,
                                                       int width, int height) {
        RenderScript rs = RenderScript.create(context);
        Type.Builder tb = new Type.Builder(rs, Element.createPixel(rs,
                Element.DataType.UNSIGNED_8, Element.DataKind.PIXEL_YUV));
        tb.setX(width);
        tb.setY(height);
        tb.setYuvFormat(android.graphics.ImageFormat.NV21);
        Allocation yuvAllocation = Allocation.createTyped(rs, tb.create(), Allocation.USAGE_SCRIPT);
        yuvAllocation.copyFrom(nv21ByteArray);
        Element nv21Element = yuvAllocation.getElement();

        return new RSToolboxContext(rs, yuvAllocation, nv21Element);
    }
}
