package edu.wpi.checksims.SmithWaterman;

/**
 * Contains required inputs for our modified version of the Smith-Waterman algorithm
 */
public class SmithWatermanParameters {
    private final String a, b;
    private final int h, d, r;
    private final int threshold;

    public static final int DefaultH = 1;
    public static final int DefaultD = 1;
    public static final int DefaultR = 1;
    public static final int DefaultThreshold = 5;

    public SmithWatermanParameters(String a, String b) {
        this.a = a;
        this.b = b;
        this.h = DefaultH;
        this.d = DefaultD;
        this.r = DefaultR;
        this.threshold = DefaultThreshold;
    }

    public SmithWatermanParameters(String a, String b, int threshold) {
        this.a = a;
        this.b = b;
        this.h = DefaultH;
        this.d = DefaultD;
        this.r = DefaultR;
        this.threshold = threshold;
    }

    public SmithWatermanParameters(String a, String b, int threshold, int h, int d, int r) {
        this.a = a;
        this.b = b;
        this.threshold = threshold;
        this.h = h;
        this.d = d;
        this.r = r;
    }

    public String getA() {
        return a;
    }

    public String getB() {
        return b;
    }

    public int getH() {
        return h;
    }

    public int getD() {
        return d;
    }

    public int getR() {
        return r;
    }

    public int getThreshold() {
        return threshold;
    }

    @Override
    public String toString() {
        return "A Smith-Waterman parameters block for 2 strings of length " + a.length() + " and " + b.length() +
                " with a threshold value of " + threshold;
    }
}
