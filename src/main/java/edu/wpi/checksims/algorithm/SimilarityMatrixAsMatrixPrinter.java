package edu.wpi.checksims.algorithm;

import java.util.Comparator;

/**
 * Output a similarity matrix as a matrix
 */
public class SimilarityMatrixAsMatrixPrinter<T extends Comparable<T>> implements SimilarityMatrixPrinter<T> {
    // TODO can this be a singleton? May need to instantiate various versions because of the generic...
    public SimilarityMatrixAsMatrixPrinter() {}

    @Override
    public String printMatrix(SimilarityMatrix<T> matrix) {
        StringBuilder b = new StringBuilder();

        AlgorithmResults[][] results = matrix.getResults();
        int matrixSize = matrix.getSubmissions().size();

        // Print X axis
        // TODO should print more than just a number?
        b.append("     ");
        for(int i = 0; i < matrixSize; i++) {
            b.append(String.format("%-4d ", i));
        }
        b.append("\n");

        for(int i = 0; i < matrixSize; i++) {
            b.append(String.format("%-4d ", i));

            for(int j = 0; j < matrixSize; j++) {
                if(i == j) {
                    b.append("N/A   ");
                    continue;
                }

                b.append(String.format("%-4f ", results[i][j].percentMatchedA()));
            }
            b.append("\n");
        }

        return b.toString();
    }
}
