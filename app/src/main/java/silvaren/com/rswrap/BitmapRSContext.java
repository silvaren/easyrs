package silvaren.com.rswrap;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;

class BitmapRSContext {
    public final RenderScript rs;
    public final Allocation ain;
    public final Element bitmapElement;

    private BitmapRSContext(RenderScript rs, Allocation ain, Element bitmapElement) {
        this.rs = rs;
        this.ain = ain;
        this.bitmapElement = bitmapElement;
    }

    public static BitmapRSContext createFromBitmap(Bitmap bitmap, Context context) {
        RenderScript rs = RenderScript.create(context);
        Allocation ain = Allocation.createFromBitmap(rs, bitmap);
        Element bitmapElement = ain.getElement();

        return new BitmapRSContext(rs, ain, bitmapElement);
    }
}
