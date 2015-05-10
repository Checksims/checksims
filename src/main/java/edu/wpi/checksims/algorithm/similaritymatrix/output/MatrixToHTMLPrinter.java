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
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.DecimalFormat;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Print a Similarity Matrix to HTML.
 */
public final class MatrixToHTMLPrinter implements MatrixPrinter {
    private static MatrixToHTMLPrinter instance;

    private static final String templateLocation = "/edu/wpi/checksims/algorithm/similaritymatrix/output/htmlOutput.vm";

    private MatrixToHTMLPrinter() {}

    /**
     * @return Singleton instance of MatrixToHTMLPrinter
     */
    public static MatrixToHTMLPrinter getInstance() {
        if(instance == null) {
            instance = new MatrixToHTMLPrinter();
        }

        return instance;
    }

    /**
     * Print a Similarity Matrix as a color-coded HTML page.
     *
     * Uses Velocity templating
     *
     * @param matrix Matrix to print
     * @return HTML representation of given matrix
     * @throws InternalAlgorithmError Thrown on internal error processing matrix
     */
    @Override
    public String printMatrix(SimilarityMatrix matrix) throws InternalAlgorithmError {
        checkNotNull(matrix);

        DecimalFormat f = new DecimalFormat("###.00");
        InputStream stream = this.getClass().getResourceAsStream(templateLocation);

        if(stream == null) {
            throw new InternalAlgorithmError("Could not resolve resource for HTML output template!");
        }

        InputStreamReader template = new InputStreamReader(stream);
        StringWriter output = new StringWriter();

        VelocityContext context = new VelocityContext();
        context.put("matrix", matrix);
        context.put("floatFormatter", f);

        VelocityEngine ve = new VelocityEngine();
        ve.evaluate(context, output, "HTMLTemplate", template);

        return output.toString();
    }

    @Override
    public String getName() {
        return "html";
    }

    @Override
    public String toString() {
        return "Singleton instance of MatrixToHTMLPrinter";
    }
}
