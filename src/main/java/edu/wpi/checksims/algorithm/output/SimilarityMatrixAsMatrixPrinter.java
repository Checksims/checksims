package edu.wpi.checksims.algorithm.output;

import java.text.DecimalFormat;

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
    public String printMatrix(SimilarityMatrix matrix) {
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
