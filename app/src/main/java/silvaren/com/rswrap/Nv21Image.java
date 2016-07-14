package silvaren.com.rswrap;

import java.util.Arrays;

class Nv21Image {

    public final byte[] nv21ByteArray;
    public final int width;
    public final int height;

    public Nv21Image(byte[] nv21ByteArray, int width, int height) {
        this.nv21ByteArray = nv21ByteArray;
        this.width = width;
        this.height = height;
    }

    public static Nv21Image generateSample() {
        int width = 256 * 2;
        int height = 256 * 2;
        int size = width * height;
        byte[] nv21ByteArray = new byte[size + size / 2];
        Arrays.fill(nv21ByteArray, (byte) 127);

        for (int x = 0; x < 256; x++) {
            for (int y = 0; y < 256; y++) {
                nv21ByteArray[size + y * 256 * 2 + x * 2] = (byte) x;
                nv21ByteArray[size + y * 256 * 2 + x * 2 + 1] = (byte) y;
            }
        }

        return new Nv21Image(nv21ByteArray, width, height);
    }
}
