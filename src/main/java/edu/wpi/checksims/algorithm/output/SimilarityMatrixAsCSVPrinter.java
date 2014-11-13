package edu.wpi.checksims.algorithm.output;

import edu.wpi.checksims.submission.Submission;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Print similarity matrix to CSV format
 */
public class SimilarityMatrixAsCSVPrinter implements SimilarityMatrixPrinter {
    private static SimilarityMatrixAsCSVPrinter instance;

    private SimilarityMatrixAsCSVPrinter() {}

    public static SimilarityMatrixAsCSVPrinter getInstance() {
        if(instance == null) {
            instance = new SimilarityMatrixAsCSVPrinter();
        }

        return instance;
    }


    @Override
    public String getName() {
        return "csv";
    }

    @Override
    public String printMatrix(SimilarityMatrix matrix) {
        StringBuilder buffer = new StringBuilder();
        float[][] similarityMatrix = matrix.getResults();
        List<Submission> submissions = matrix.getSubmissions();
        int matrixSize = matrix.getSubmissions().size();

        // Print first cell
        buffer.append("NULL, ");

        // Print first row
        for(int i = 0; i < matrixSize; i++) {
            buffer.append(submissions.get(i).getName());
            buffer.append(", ");
        }
        buffer.append("\n");

        DecimalFormat formatter = new DecimalFormat("#.##");

        for(int i = 0; i < matrixSize; i++) {
            // Print the Y axis
            buffer.append(submissions.get(i).getName());
            buffer.append(", ");

            for(int j = 0; j < matrixSize; j++) {
                if(i == j) {
                    buffer.append("1, ");
                    continue;
                }

                buffer.append(formatter.format(similarityMatrix[i][j]));
                buffer.append(", ");
            }

            buffer.append("\n");
        }

        return buffer.toString();
    }
}
