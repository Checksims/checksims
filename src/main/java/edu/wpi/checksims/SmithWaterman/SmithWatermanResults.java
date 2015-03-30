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

/**
 * Represents the output of the Smith-Waterman algorithm
 */
public class SmithWatermanResults {
    private final String a, b;
    private final int[][] table;
    private final int tableWidth, tableHeight;
    private final int maxOverlay;
    private final int maxOverlayX, maxOverlayY;

    public SmithWatermanResults(String a, String b, int[][] table, int tableWidth, int tableHeight, int maxOverlay, int maxOverlayX, int maxOverlayY) {
        this.a = a;
        this.b = b;
        this.table = table;
        this.tableWidth = tableWidth;
        this.tableHeight = tableHeight;
        this.maxOverlay = maxOverlay;
        this.maxOverlayX = maxOverlayX;
        this.maxOverlayY = maxOverlayY;
    }

    public String getMatch() {
        StringBuilder matchA = new StringBuilder();
        StringBuilder matchB = new StringBuilder();

        if(!areMatchesPresent()) {
            return "No matches"; // TODO localize this string
        }

        int max;
        int currentX = maxOverlayX;
        int currentY = maxOverlayY;

        do {
            int prospectiveX = currentX - 1, prospectiveY = currentY - 1;
            max = table[prospectiveX][prospectiveY];

            if(max < table[currentX - 1][currentY]) {
                prospectiveX = currentX - 1;
                prospectiveY = currentY;
                max = table[prospectiveX][prospectiveY];
            }
            if(max < table[currentX][currentY - 1]) {
                prospectiveX = currentX;
                prospectiveY = currentY - 1;
                max = table[prospectiveX][prospectiveY];
            }

            char aChar = a.charAt(currentX - 1);
            char bChar = b.charAt(currentY - 1);

            if(prospectiveX == currentX - 1 && prospectiveY == currentY - 1) {
                if(aChar == bChar && aChar != SmithWaterman.IGNORECHAR) {
                    matchA.insert(0, aChar);
                    matchB.insert(0, bChar);
                } else {
                    matchA.insert(0, "-");
                    matchB.insert(0, "-");
                }
            } else if(prospectiveX == currentX - 1 && prospectiveY == currentY) {
                matchA.insert(0, aChar);
                matchB.insert(0, "-");
            } else {
                matchA.insert(0, "-");
                matchB.insert(0, bChar);
            }

            currentX = prospectiveX;
            currentY = prospectiveY;
        } while(max > 0);

        matchA.append("\n");
        matchA.append(matchB.toString());

        return matchA.toString();
    }

    public int[][] getTable() {
        return table.clone();
    }

    public int getTableElement(int x, int y) {
        return table[x][y];
    }

    public int getMaxOverlay() {
        return maxOverlay;
    }

    public int getMaxOverlayX() {
        return maxOverlayX;
    }

    public int getMaxOverlayY() {
        return maxOverlayY;
    }

    public int getTableWidth() {
        return tableWidth;
    }

    public int getTableHeight() {
        return tableHeight;
    }

    public boolean isEmpty() {
        return (null == table);
    }

    public boolean areMatchesPresent() {
        return !(isEmpty() || (maxOverlay == 0));
    }

    public String getTableAsString() {
        if(isEmpty()) {
            return "Empty results\n";
        }

        StringBuilder tableString = new StringBuilder();

        // Print X axis
        tableString.append(" ");
        for(int i = 0; i < tableWidth - 1; i++) {
            tableString.append(" ");
            tableString.append(a.charAt(i));
        }
        tableString.append("\n");

        // Print each row
        for(int j = 1; j < tableHeight; j++) {
            // Print Y axis
            tableString.append(b.charAt(j - 1));

            // Print row elements
            for(int i = 1; i < tableWidth; i++) {
                tableString.append(" ");
                int t = table[i][j];

                if(t == 0) {
                    tableString.append(" ");
                } else {
                    tableString.append(t);
                }
            }

            tableString.append("\n");
        }

        return tableString.toString();
    }

    @Override
    public String toString() {
        return "Smith-Waterman results block for two strings of length " + a.length() + " and " + b.length() +
                " with max overlay " + maxOverlay;
    }
}
