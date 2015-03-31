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
import edu.wpi.checksims.util.file.FileStringWriter;

import java.io.File;
import java.io.IOException;

/**
 * Output results to a file
 */
public class OutputAsFilePrinter implements OutputPrinter {
    private final File baseName;

    /**
     * @param baseName Base name of the file to output to. The name of the similarity matrix printer used will be appended to form final filename.
     */
    public OutputAsFilePrinter(File baseName) {
        this.baseName = baseName;
    }

    /**
     * @return Absolute path of the base output file
     */
    public String getBaseName() {
        return baseName.getAbsolutePath();
    }

    /**
     * Print given similarity matrix to file
     *
     * @param toPrint Matrix to print
     * @param printWith Output strategy to use
     */
    @Override
    public void print(SimilarityMatrix toPrint, SimilarityMatrixPrinter printWith) {
        File outputTo = new File(baseName.getAbsolutePath() + "." + printWith.getName());
        String output = printWith.printMatrix(toPrint);

        try {
            FileStringWriter.writeStringToFile(outputTo, output);
        } catch(IOException e) {
            throw new RuntimeException("Could not write output to file", e);
        }
    }

    @Override
    public String toString() {
        return "OutputAsFilePrinter with base filename " + baseName.getName();
    }
}
