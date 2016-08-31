package io.github.silvaren.easyrs.tools.base;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;

import io.github.silvaren.easyrs.tools.Nv21Image;

public class ConvertingTool<T> {

    private final BaseToolScript tool;

    public ConvertingTool(BaseToolScript tool) {
        this.tool = tool;
    }

    public interface BaseToolScript<T> {
        void runScript(RSToolboxContext rsToolboxrs, Allocation aout, T scriptParams);
    }

    public Bitmap doComputation(RenderScript rs, Bitmap inputBitmap, T scriptParams) {
        Bitmap.Config config = inputBitmap.getConfig();
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap.getWidth(), inputBitmap.getHeight(),
                config);
        return doComputation(rs, inputBitmap, outputBitmap, scriptParams);
    }

    private Bitmap doComputation(RenderScript rs, Bitmap inputBitmap, Bitmap outputBitmap, T scriptParams) {

        RSToolboxContext rsToolboxContext = RSToolboxContext.createFromBitmap(rs, inputBitmap);

        Allocation aout = Allocation.createTyped(rsToolboxContext.rs, rsToolboxContext.ain.getType());

        this.tool.runScript(rsToolboxContext, aout, scriptParams);

        aout.copyTo(outputBitmap);

        return outputBitmap;
    }

    public byte[] doComputation(RenderScript rs, byte[] nv21ByteArray, int width, int height,
                                T scriptParams) {
        byte[] outputNv21ByteArray = new byte[nv21ByteArray.length];
        doComputation(rs, nv21ByteArray, width, height, outputNv21ByteArray, scriptParams);
        return outputNv21ByteArray;
    }

    private void doComputation(RenderScript rs, byte[] nv21ByteArray, int width, int height,
                                   byte[] outputNv21ByteArray, T scriptParams) {
        Bitmap bitmapFromNv21 = Nv21Image.nv21ToBitmap(rs, nv21ByteArray, width, height);
        Bitmap outputBitmap = doComputation(rs, bitmapFromNv21, scriptParams);
        Nv21Image nv21Output = Nv21Image.bitmapToNV21(rs, outputBitmap);
        System.arraycopy(nv21Output.nv21ByteArray, 0, outputNv21ByteArray, 0,
                nv21Output.nv21ByteArray.length);
    }
}
