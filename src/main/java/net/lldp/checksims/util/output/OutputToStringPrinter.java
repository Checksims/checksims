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
 * Copyright (c) 2014-2015 Nicholas DeMarinis, Matthew Heon, and Dolan Murvihill
 */

package net.lldp.checksims.util.output;

import net.lldp.checksims.algorithm.InternalAlgorithmError;
import net.lldp.checksims.algorithm.similaritymatrix.SimilarityMatrix;
import net.lldp.checksims.algorithm.similaritymatrix.output.MatrixPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provide results of Checksims as a string.
 */
public class OutputToStringPrinter implements OutputPrinter {
    private final StringBuffer buffer;

    /**
     * Instantiate a new OutputToStringPrinter with a clean buffer.
     */
    public OutputToStringPrinter() {
        buffer = new StringBuffer();
    }

    /**
     * @return Current contents of the output buffer
     */
    public String getBuffer() {
        return buffer.toString();
    }

    /**
     * Print the given output matrix using the given strategy and append it to the output buffer.
     *
     * @param toPrint Output matrix to print
     * @param printWith Strategy to use when printing
     */
    @Override
    public void print(SimilarityMatrix toPrint, MatrixPrinter printWith) {
        checkNotNull(toPrint);
        checkNotNull(printWith);

        Logger logs = LoggerFactory.getLogger(OutputToStringPrinter.class);

        String output;
        try {
            output = printWith.printMatrix(toPrint);
        } catch (InternalAlgorithmError e) {
            logs.error("Error printing similarity matrix");
            throw new RuntimeException(e);
        }

        buffer.append(output);
        buffer.append("\n");
    }

    @Override
    public String toString() {
        return "An OutputToString printer instance";
    }
}
