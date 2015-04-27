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

import edu.wpi.checksims.algorithm.InternalAlgorithmError;
import edu.wpi.checksims.algorithm.similaritymatrix.SimilarityMatrix;
import edu.wpi.checksims.algorithm.similaritymatrix.output.MatrixPrinter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Output results to a file.
 */
public class OutputAsFilePrinter implements OutputPrinter {
    private final File baseName;

    private static Logger logs = LoggerFactory.getLogger(OutputAsFilePrinter.class);

    /**
     * The name of the matrix printer used will be appended to form the final filename where output is written.
     *
     * @param baseName Base name of the file to output to.
     */
    public OutputAsFilePrinter(File baseName) {
        this.baseName = baseName;
    }

    /**
     * @return File we output to
     */
    public File getFile() {
        return baseName;
    }

    /**
     * Print given similarity matrix to file.
     *
     * @param toPrint Matrix to print
     * @param printWith Output strategy to use
     */
    @Override
    public void print(SimilarityMatrix toPrint, MatrixPrinter printWith) {
        checkNotNull(toPrint);
        checkNotNull(printWith);

        File outputTo = new File(baseName.getAbsolutePath() + "." + printWith.getName());

        logs.info("Writing " + printWith.getName() + " output to file " + outputTo.getName());

        try {
            FileUtils.writeStringToFile(outputTo, printWith.printMatrix(toPrint), StandardCharsets.UTF_8);
        } catch(IOException | InternalAlgorithmError e) {
            throw new RuntimeException("Could not write output to file", e);
        }
    }

    @Override
    public String toString() {
        return "OutputAsFilePrinter with base filename " + baseName.getName();
    }
}
