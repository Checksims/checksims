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

package edu.wpi.checksims.algorithm.similaritymatrix.output;

import edu.wpi.checksims.algorithm.InternalAlgorithmError;
import edu.wpi.checksims.algorithm.similaritymatrix.SimilarityMatrix;
import org.apache.commons.lang3.tuple.Pair;

import java.text.DecimalFormat;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Print a Similarity Matrix as machine-readable CSV.
 */
public class MatrixToCSVPrinter implements MatrixPrinter {
    private static MatrixToCSVPrinter instance;

    private MatrixToCSVPrinter() {}

    /**
     * @return Singleton instance of MatrixToCSVPrinter
     */
    public static MatrixToCSVPrinter getInstance() {
        if(instance == null) {
            instance = new MatrixToCSVPrinter();
        }

        return instance;
    }

    /**
     * Print a Similarity Matrix in CSV format.
     *
     * @param matrix Matrix to print
     * @return CSV representation of matrix
     * @throws InternalAlgorithmError Thrown on internal error processing matrix
     */
    @Override
    public String printMatrix(SimilarityMatrix matrix) throws InternalAlgorithmError {
        checkNotNull(matrix);

        StringBuilder builder = new StringBuilder();
        DecimalFormat formatter = new DecimalFormat("0.00");

        Pair<Integer, Integer> arrayBounds = matrix.getArrayBounds();

        // First row: NULL, then all the Y submissions, comma-separated
        builder.append("NULL,");
        for(int y = 0; y < arrayBounds.getRight(); y++) {
            builder.append("\"");
            builder.append(matrix.getYSubmission(y).getName());
            builder.append("\"");
            if(y != (arrayBounds.getRight() - 1)) {
                builder.append(",");
            } else {
                builder.append("\n");
            }
        }

        // Remaining rows: X label, then all Y results in order
        for(int x = 0; x < arrayBounds.getLeft(); x++) {
            // First, append name of the X submission
            builder.append("\"");
            builder.append(matrix.getXSubmission(x).getName());
            builder.append("\",");

            // Next, append all the matrix values, formatted as given
            for(int y = 0; y < arrayBounds.getRight(); y++) {
                builder.append(formatter.format(matrix.getEntryFor(x, y).getSimilarityPercent()));
                if(y != (arrayBounds.getRight() - 1)) {
                    builder.append(",");
                } else {
                    builder.append("\n");
                }
            }
        }

        return builder.toString();
    }

    @Override
    public String getName() {
        return "csv";
    }

    @Override
    public String toString() {
        return "Singleton matrix of MatrixToCSVPrinter";
    }
}
