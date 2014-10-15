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
        if(coord.x >= width || coord.y >= height) {
            throw new RuntimeException("Coordinate is out of array bounds"); // TODO convert to checked
        }

        return array[coord.x][coord.y];
    }

    public void setValue(int value, TwoDimArrayCoord coord) {
        if(coord.x >= width || coord.y >= height) {
            throw new RuntimeException("Coordinate is out of array bounds"); // TODO convert to checked
        }

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
        int[] toSort = new int[3];

        if(coord.x == 0 || coord.y == 0) {
            throw new RuntimeException("Getting predecessors would move out of array bounds!"); // TODO should be checked
        }

        toSort[0] = array[coord.x - 1][coord.y - 1];
        toSort[1] = array[coord.x - 1][coord.y];
        toSort[2] = array[coord.x][coord.y - 1];

        return Ints.max(toSort);
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
