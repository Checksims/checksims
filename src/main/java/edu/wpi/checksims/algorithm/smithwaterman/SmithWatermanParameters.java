/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright (c) 2014-2015 Matthew Heon and Dolan Murvihill
 */

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
