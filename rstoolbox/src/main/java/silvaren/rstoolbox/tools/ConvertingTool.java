package silvaren.rstoolbox.tools;

import android.content.Context;
import android.graphics.Bitmap;

public class ConvertingTool<T> {

    public final BaseTool<T> baseTool;

    public ConvertingTool(BaseTool<T> baseTool) {
        this.baseTool = baseTool;
    }

    protected byte[] doComputation(Context context, byte[] nv21ByteArray, int width, int height,
                                 T scriptParams) {
        byte[] outputNv21ByteArray = new byte[nv21ByteArray.length];
        doComputation(context, nv21ByteArray, width, height, outputNv21ByteArray, scriptParams);
        return outputNv21ByteArray;
    }

    protected byte[] doComputationInPlace(Context context, byte[] nv21ByteArray, int width, int height,
                                   T scriptParams) {
        byte[] outputNv21ByteArray = new byte[nv21ByteArray.length];
        doComputation(context, nv21ByteArray, width, height, outputNv21ByteArray, scriptParams);
        return outputNv21ByteArray;
    }

    private void doComputation(Context context, byte[] nv21ByteArray, int width, int height,
                                   byte[] outputNv21ByteArray, T scriptParams) {
        Bitmap bitmapFromNv21 = Nv21Image.nv21ToBitmap(nv21ByteArray, width, height);
        Bitmap outputBitmap = baseTool.doComputation(context, bitmapFromNv21, scriptParams);
        Nv21Image nv21Output = Nv21Image.convertToNV21(context, outputBitmap);
        System.arraycopy(nv21Output.nv21ByteArray, 0, outputNv21ByteArray, 0,
                nv21Output.nv21ByteArray.length);
    }
}
