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
 * Copyright (c) 2014 Matthew Heon and Dolan Murvihill
 */

package edu.wpi.checksims.util;

import com.google.common.primitives.Ints;

/**
 * Wraps a 2D array of 32-bit signed integers to provide a convenient interface
 */
public class TwoDimIntArray {
    private int[][] array;
    public final int width;
    public final int height;

    private int max;
    private boolean maxSet;
    private TwoDimArrayCoord maxPos;

    public TwoDimIntArray(int width, int height) {
        if(width <= 0 || height <= 0) {
            throw new RuntimeException("Width and height must be nonzero and positive for an array!"); // TODO convert to checked
        }

        this.array = new int[width][height];
        this.width = width;
        this.height = height;

        this.max = 0;
        this.maxSet = false;
        this.maxPos = null;
    }

    public int getValue(TwoDimArrayCoord coord) {
        return array[coord.x][coord.y];
    }

    public void setValue(int value, TwoDimArrayCoord coord) {
        // Ensure we always have the largest value placed into the array
        if(!maxSet || max <= value) {
            max = value;
            maxPos = coord;
            maxSet = true;
        }

        array[coord.x][coord.y] = value;
    }

    public void reset() {
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                    array[i][j] = 0;
            }
        }
    }

    public int getMax() {
        return max;
    }

    public TwoDimArrayCoord getMaxPos() {
        return maxPos;
    }

    public int getMaxFrom(TwoDimArrayCoord[] coords) {
        int[] toSort = new int[coords.length];

        for(int i = 0; i < coords.length; i++) {
            toSort[i] = getValue(coords[i]);
        }

        return Ints.max(toSort);
    }

    /**
     * Get maximum value of the predecessor coordinates (upper-left, upper, and left)
     *
     * Could be accomplished with getMaxFrom, but this optimized version significantly speeds
     * the Smith-Waterman algorithm
     *
     * @param coord Coordinate to get max from predecessors
     * @return Maximum value of predecessors of coord in this array
     */
    public int getMaxOfPredecessors(TwoDimArrayCoord coord) {
        // TODO investigate performance implications of checking cord.x and coord.y here

        int a = array[coord.x - 1][coord.y - 1];
        int b = array[coord.x - 1][coord.y];
        int c = array[coord.x][coord.y - 1];

        // Efficient getMax of 3 elements
        // 2-3 comparisons maximum
        if(a < b) {
            if(b < c) {
                return c;
            } else {
                return b;
            }
        } else {
            if(b < c) {
                if(a < c) {
                    return c;
                } else {
                    return a;
                }
            } else {
                return a;
            }
        }
    }

    public int[][] getArray() {
        return array.clone();
    }

    public String toString() {
        return "A two-dimensional integer array of width " + width + " and height " + height;
    }

    public String arrayToString() {
        StringBuilder b = new StringBuilder();

        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                b.append(String.format("%04d, ", array[i][j]));
            }
            b.append("\n");
        }

        return b.toString();
    }
}
