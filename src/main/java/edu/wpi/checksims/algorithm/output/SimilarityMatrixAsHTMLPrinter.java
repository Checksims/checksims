package edu.wpi.checksims.algorithm.output;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.DecimalFormat;

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
    public String printMatrix(SimilarityMatrix matrix) {
        DecimalFormat f = new DecimalFormat("###.00");
        InputStream stream = SimilarityMatrixAsHTMLPrinter.class.getResourceAsStream("/edu/wpi/checksims/algorithm/output/htmlOutput.vm");

        if(stream == null) {
            throw new RuntimeException("Could not resolve resource for HTML output template!");
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
