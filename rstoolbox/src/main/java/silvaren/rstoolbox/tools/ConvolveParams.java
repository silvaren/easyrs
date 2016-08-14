package silvaren.rstoolbox.tools;

public class ConvolveParams {

    public static class Kernels5x5 {
        public static float[] SOBEL_X = {
                1.f, 2.f, 0.f, -2.f, -1.f,
                4.f, 8.f, 0.f, -8.f, -4.f,
                6.f, 12.f, 1.f, -12.f, -6.f,
                4.f, 8.f, 0.f, -8.f, -4.f,
                1.f, 2.f, 0.f, -2.f, -1.f};
    }

    static class Kernels3x3 {
        public static float[] SOBEL_X = {
                1.f, 0.f, -1.f,
                2.f, 1.f, -2.f,
                1.f, 0.f, -1.f};
    }

    public final float[] coefficients;

    public ConvolveParams(float[] coefficients) {
        this.coefficients = coefficients;
    }
}
