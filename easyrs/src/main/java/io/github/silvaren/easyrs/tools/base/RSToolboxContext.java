package io.github.silvaren.easyrs.tools.base;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;

public class RSToolboxContext {
    public final RenderScript rs;
    public final Allocation ain;
    public final Element element;

    private RSToolboxContext(RenderScript rs, Allocation ain, Element bitmapElement) {
        this.rs = rs;
        this.ain = ain;
        this.element = bitmapElement;
    }

    public static RSToolboxContext createFromBitmap(RenderScript rs, Bitmap bitmap) {
        Allocation ain = Allocation.createFromBitmap(rs, bitmap);
        Element bitmapElement = ain.getElement();

        return new RSToolboxContext(rs, ain, bitmapElement);
    }
}
