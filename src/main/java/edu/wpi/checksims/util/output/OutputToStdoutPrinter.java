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

package edu.wpi.checksims.util.output;

import edu.wpi.checksims.algorithm.output.SimilarityMatrix;
import edu.wpi.checksims.algorithm.output.SimilarityMatrixPrinter;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Output printer that prints all results to STDOUT
 */
public final class OutputToStdoutPrinter implements OutputPrinter {
    private static OutputToStdoutPrinter instance;

    private OutputToStdoutPrinter() {}

    /**
     * @return Singleton instance of OutputToStdoutPrinter
     */
    public static OutputToStdoutPrinter getInstance() {
        if(instance == null) {
            instance = new OutputToStdoutPrinter();
        }

        return instance;
    }

    /**
     * Print matrix to STDOUT using given strategy
     *
     * @param toPrint Matrix to print
     * @param printWith Strategy to print with
     */
    @Override
    public void print(SimilarityMatrix toPrint, SimilarityMatrixPrinter printWith) {
        checkNotNull(toPrint);
        checkNotNull(printWith);

        String output = printWith.printMatrix(toPrint);

        System.out.println(output);
    }

    @Override
    public String toString() {
        return "Singleton instance of OutputToStdoutPrinter";
    }
}
