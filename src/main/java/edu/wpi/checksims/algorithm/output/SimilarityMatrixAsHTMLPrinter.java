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

import edu.wpi.checksims.algorithm.InternalAlgorithmError;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.DecimalFormat;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Output a similarity matrix as HTML
 */
public class SimilarityMatrixAsHTMLPrinter implements SimilarityMatrixPrinter {
    private static SimilarityMatrixAsHTMLPrinter instance;

    private SimilarityMatrixAsHTMLPrinter() {}

    public static SimilarityMatrixAsHTMLPrinter getInstance() {
        if(instance == null) {
            instance = new SimilarityMatrixAsHTMLPrinter();
        }

        return instance;
    }

    @Override
    public String getName() {
        return "html";
    }

    @Override
    public String printMatrix(SimilarityMatrix matrix) throws InternalAlgorithmError {
        checkNotNull(matrix);

        DecimalFormat f = new DecimalFormat("###.00");
        InputStream stream = SimilarityMatrixAsHTMLPrinter.class.getResourceAsStream("/edu/wpi/checksims/algorithm/output/htmlOutput.vm");

        if(stream == null) {
            throw new InternalAlgorithmError("Could not resolve resource for HTML output template!");
        }

        InputStreamReader template = new InputStreamReader(stream);
        StringWriter output = new StringWriter();

        VelocityContext context = new VelocityContext();
        context.put("students", matrix.getSubmissions());
        context.put("resultArray", matrix.getResults());
        context.put("floatFormatter", f);

        VelocityEngine ve = new VelocityEngine();
        ve.evaluate(context, output, "HTMLTemplate", template);

        return output.toString();
    }
}
