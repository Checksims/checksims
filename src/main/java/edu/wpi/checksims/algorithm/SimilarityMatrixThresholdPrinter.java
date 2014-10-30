package edu.wpi.checksims.algorithm;

import java.text.DecimalFormat;

/**
 * Prints all similarity matrix entries over a certain threshold
 */
public class SimilarityMatrixThresholdPrinter implements SimilarityMatrixPrinter {
    private float threshold;

    public SimilarityMatrixThresholdPrinter(float threshold) {
        this.threshold = threshold;
    }


    @Override
    public String printMatrix(SimilarityMatrix matrix) {
        StringBuilder b = new StringBuilder();
        float[][] similarityMatrix = matrix.getResults();
        int matrixSize = matrix.getSubmissions().size();

        DecimalFormat formatter = new DecimalFormat("###.##");

        for(int i = 0; i < matrixSize; i++) {
            for(int j = 0; j < matrixSize; j++) {
                if(similarityMatrix[i][j] >= threshold) {
                    b.append("Found match of ");
                    b.append(formatter.format(100 * similarityMatrix[i][j]));
                    b.append("% between submissions ");
                    b.append(matrix.getSubmissions().get(i).getName());
                    b.append(" and ");
                    b.append(matrix.getSubmissions().get(j).getName());
                    b.append("\n");
                }
            }
        }

        return b.toString();
    }

    @Override
    public String toString() {
        return "Similarity Matrix Threshold Printer, with threshold of " + threshold;
    }
}
