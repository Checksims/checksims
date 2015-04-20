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

package edu.wpi.checksims.algorithm.output;

import java.text.DecimalFormat;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Output a similarity matrix as a matrix
 */
public class SimilarityMatrixAsMatrixPrinter implements SimilarityMatrixPrinter {
    private static SimilarityMatrixAsMatrixPrinter instance;

    private SimilarityMatrixAsMatrixPrinter() {}

    public static SimilarityMatrixAsMatrixPrinter getInstance() {
        if(instance == null) {
            instance = new SimilarityMatrixAsMatrixPrinter();
        }

        return instance;
    }

    @Override
    public String getName() {
        return "matrix";
    }

    @Override
    public String printMatrix(SimilarityMatrix matrix) {
        checkNotNull(matrix);

        StringBuilder b = new StringBuilder();

        float[][] results = matrix.getResults();
        int matrixSize = matrix.getSubmissions().size();

        // Print X axis
        // TODO should print more than just a number?
        b.append("     ");
        for(int i = 0; i < matrixSize; i++) {
            b.append(String.format("%-4d ", i));
        }
        b.append("\n");

        DecimalFormat formatter = new DecimalFormat("#.##");

        for(int i = 0; i < matrixSize; i++) {
            b.append(String.format("%-4d ", i));

            for(int j = 0; j < matrixSize; j++) {
                if(i == j) {
                    b.append(" N/A ");
                    continue;
                }

                b.append(String.format("%1$4s", formatter.format(results[i][j]))).append(" ");
            }
            b.append("\n");
        }

        return b.toString();
    }
}
