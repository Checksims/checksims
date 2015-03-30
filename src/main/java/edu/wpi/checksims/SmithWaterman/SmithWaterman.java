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

package edu.wpi.checksims.SmithWaterman;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The actual Smith-Waterman algorithm, as modified by Irving in '04 for Plagiarism Detection
 */
public class SmithWaterman {
    public static final char IGNORECHAR = '\0'; // We use ASCII NULL to indicate "this character replaced"

    public static int applySmithWatermanLooping(SmithWatermanParameters params) {
        // First, apply the algorithm once, get an initial set of results
        SmithWatermanResults initial = applySmithWaterman(params);

        if(!initial.areMatchesPresent()) {
            // No matches, no point looping - just return these results
            return 0;
        }

        SmithWatermanResults currentResults = initial;
        SmithWatermanParameters currentParams = params;
        int totalOverlay = 0;

        // At least one match exists
        do {
            // Maintain a running count of the total overlay
            totalOverlay += currentResults.getMaxOverlay();

            // Remove the matched string and generate new parameters
            StringBuilder newA = new StringBuilder(currentParams.getA());
            StringBuilder newB = new StringBuilder(currentParams.getB());

            int max;
            int currentX = currentResults.getMaxOverlayX();
            int currentY = currentResults.getMaxOverlayY();

            // Move up the table
            do {
                char aChar = indexIntoString(currentX, newA.toString());
                char bChar = indexIntoString(currentY, newB.toString());

                // If the chars are identical, we replace them with the ignore character
                if(aChar == bChar) {
                    newA.setCharAt(currentX - 1, IGNORECHAR);
                    newB.setCharAt(currentY - 1, IGNORECHAR);
                }

                // Find the maximum of the upper-left diagonal, upper, and left cells in the S table
                ArrayCoord current = new ArrayCoord(currentX, currentY);
                ArrayCoord prospective = current.getDiagonal();
                max = currentResults.getTableElement(currentX - 1, currentY - 1);

                if(max < currentResults.getTableElement(currentX - 1, currentY)) {
                    max = currentResults.getTableElement(currentX - 1, currentY);
                    prospective = current.getLeft();
                }
                if(max < currentResults.getTableElement(currentX, currentY - 1)) {
                    max = currentResults.getTableElement(currentX, currentY - 1);
                    prospective = current.getAbove();
                }

                currentX = prospective.getX();
                currentY = prospective.getY();
            } while(max != 0);

            currentParams = new SmithWatermanParameters(newA.toString(), newB.toString(), currentParams.getThreshold(),
                    currentParams.getH(), currentParams.getD(), currentParams.getR());

            // Apply Smith-Waterman again with the new parameters
            currentResults = applySmithWaterman(currentParams);
        } while(currentResults.getMaxOverlay() > params.getThreshold());

        return totalOverlay;
    }

    // Apply one iteration of the core algorithm
    // (Smith-Waterman with aggressive overlay mitigation by Irving)
    public static SmithWatermanResults applySmithWaterman(SmithWatermanParameters params) {
        int width = params.getA().length() + 1, height = params.getB().length() + 1;

        // Was one or more of the strings empty? If so, there is no match
        if(width == 1 || height == 1) {
            return new SmithWatermanResults(params.getA(), params.getB(), null, 0, 0, 0, 0, 0);
        }

        int[][] table = new int[width][height];
        int[][] maxTable = new int[width][height];

        int maxOverlay = 0;
        int maxOverlayX = 0;
        int maxOverlayY = 0;

        // Initial row and column are 0
        for(int i = 0; i < width; i++) {
            table[i][0] = 0;
            maxTable[i][0] = 0;
        }
        for(int j = 0; j < height; j++) {
            table[0][j] = 0;
            maxTable[0][j] = 0;
        }

        for(int j = 1; j < height; j++) {
            for(int i = 1; i < width; i++) {
                ArrayCoord current = new ArrayCoord(i, j);
                char aChar = indexIntoString(i, params.getA());
                char bChar = indexIntoString(j, params.getB());

                int newS, newM;

                // First, compute S[i,j] - the proper table index

                // If first character matches, get the upper-left diagonal, increment by H - this is the value for the cell
                if(aChar == bChar && aChar != IGNORECHAR) {
                    newS = current.getDiagonal().asIndex(table) + params.getH();
                } else {
                    // If we don't match
                    // Cell value is Max(0, Upper-Left Diagonal - R, Upper - D, Left - D)
                    List<Integer> getMaxFrom = new ArrayList<>();

                    getMaxFrom.add(0);
                    getMaxFrom.add(current.getDiagonal().asIndex(table) - params.getR());
                    getMaxFrom.add(current.getLeft().asIndex(table) - params.getD());
                    getMaxFrom.add(current.getAbove().asIndex(table) - params.getD());

                    newS = Collections.max(getMaxFrom);
                }

                // Next, compute M[i,j] - the max table entry
                if(newS == 0) {
                    // Is S[i,j] 0? If so, M[i,j] is also 0.
                    newM = 0;
                } else if(aChar == bChar && aChar != IGNORECHAR) {
                    // If characters match, use the max of the upper-left diagonal from the S and M tables
                    List<Integer> getMaxFrom = new ArrayList<>();

                    getMaxFrom.add(current.getDiagonal().asIndex(table));
                    getMaxFrom.add(current.getDiagonal().asIndex(maxTable));

                    newM = Collections.max(getMaxFrom);
                } else {
                    // Otherwise, use the max from the upper-left, upper, and left cells in both the S and M tables
                    List<Integer> getMaxFrom = new ArrayList<>();

                    getMaxFrom.add(current.getDiagonal().asIndex(table));
                    getMaxFrom.add(current.getLeft().asIndex(table));
                    getMaxFrom.add(current.getAbove().asIndex(table));
                    getMaxFrom.add(current.getDiagonal().asIndex(maxTable));
                    getMaxFrom.add(current.getLeft().asIndex(maxTable));
                    getMaxFrom.add(current.getAbove().asIndex(maxTable));

                    newM = Collections.max(getMaxFrom);
                }

                // We now check M[i,j] - S[i,j] >= Threshold
                // If true, we've found an overlap exceeding threshold
                // Set S[i,j] and M[i,j] to 0
                // Otherwise, set them to computed values
                if(newM - newS >= params.getThreshold()) {
                    newS = 0;
                    newM = 0;
                }
                current.setIndex(newS, table);
                current.setIndex(newM, maxTable);

                // Finally, let's see if we exceeded the current maxOverlay
                if(maxOverlay <= newS && newS != 0) {
                    maxOverlay = newS;
                    maxOverlayX = i;
                    maxOverlayY = j;
                }
            }
        }

        return new SmithWatermanResults(params.getA(), params.getB(), table, width, height, maxOverlay, maxOverlayX,
                maxOverlayY);
    }

    // Gets character out of a string, assuming 1-indexing (row/col 0 are all 0s in table)
    private static char indexIntoString(int index, String str) {
        return str.charAt(index - 1);
    }

    private static int maxOfIndices(ArrayCoord[] coords, int[][] table) {
        List<Integer> getMaxFrom = new ArrayList<>();

        for(ArrayCoord c: coords) {
            getMaxFrom.add(c.asIndex(table));
        }

        return Collections.max(getMaxFrom);
    }
}

class ArrayCoord {
    private final int x;
    private final int y;

    ArrayCoord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ArrayCoord getDiagonal() {
        return new ArrayCoord(x - 1, y - 1);
    }

    public ArrayCoord getLeft() {
        return new ArrayCoord(x - 1, y);
    }

    public ArrayCoord getAbove() {
        return new ArrayCoord(x, y - 1);
    }

    public int asIndex(int[][] array) {
        return array[x][y];
    }

    public void setIndex(int value, int[][] array) {
        array[x][y] = value;
    }
}
