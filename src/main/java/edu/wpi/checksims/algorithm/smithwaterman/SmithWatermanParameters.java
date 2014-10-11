package edu.wpi.checksims.algorithm.smithwaterman;

/**
 * Parameters block for Smith-Waterman algorithm
 */
public class SmithWatermanParameters {
    public final int h;
    public final int d;
    public final int r;
    public final int overlapThreshold;
    public final int matchSizeThreshold;

    private static final SmithWatermanParameters defaultParams = new SmithWatermanParameters(1, 1, 1, 5, 5);

    public SmithWatermanParameters(int h, int d, int r, int overlapThreshold, int matchSizeThreshold) {
        this.h = h;
        this.d = d;
        this.r = r;
        this.overlapThreshold = overlapThreshold;
        this.matchSizeThreshold = matchSizeThreshold;
    }

    public static SmithWatermanParameters getDefaultParams() {
        return defaultParams;
    }

    @Override
    public String toString() {
        return "A Smith-Waterman parameters block set as follows: H=" + h + ", D = " + d + ", R = " + r +
                ", Voverlap = " + overlapThreshold + ", Vmatchsize = " + matchSizeThreshold;
    }
}
