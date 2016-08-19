package silvaren.rstoolbox.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;

public class ConvertingTool<T> {

    private final BaseToolScript tool;

    public ConvertingTool(BaseToolScript tool) {
        this.tool = tool;
    }

    interface BaseToolScript<T> {
        void runScript(RSToolboxContext rsToolboxContext, Allocation aout, T scriptParams);
    }

    protected Bitmap doComputation(Context context, Bitmap inputBitmap, T scriptParams) {
        Bitmap.Config config = inputBitmap.getConfig();
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap.getWidth(), inputBitmap.getHeight(),
                config);
        return doComputation(context, inputBitmap, outputBitmap, scriptParams);
    }

    private Bitmap doComputation(Context context, Bitmap inputBitmap, Bitmap outputBitmap, T scriptParams) {

        RSToolboxContext rsToolboxContext = RSToolboxContext.createFromBitmap(context, inputBitmap);

        Allocation aout = Allocation.createTyped(rsToolboxContext.rs, rsToolboxContext.ain.getType());

        this.tool.runScript(rsToolboxContext, aout, scriptParams);

        aout.copyTo(outputBitmap);

        return outputBitmap;
    }

    protected byte[] doComputation(Context context, byte[] nv21ByteArray, int width, int height,
                                 T scriptParams) {
        byte[] outputNv21ByteArray = new byte[nv21ByteArray.length];
        doComputation(context, nv21ByteArray, width, height, outputNv21ByteArray, scriptParams);
        return outputNv21ByteArray;
    }

    private void doComputation(Context context, byte[] nv21ByteArray, int width, int height,
                                   byte[] outputNv21ByteArray, T scriptParams) {
        Bitmap bitmapFromNv21 = Nv21Image.nv21ToBitmap(context, nv21ByteArray, width, height);
        Bitmap outputBitmap = doComputation(context, bitmapFromNv21, scriptParams);
        Nv21Image nv21Output = Nv21Image.convertToNV21(context, outputBitmap);
        System.arraycopy(nv21Output.nv21ByteArray, 0, outputNv21ByteArray, 0,
                nv21Output.nv21ByteArray.length);
    }
}
