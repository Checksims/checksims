package edu.wpi.checksims.algorithm;

/**
 * Interface for various approaches for outputting a similarity matrix as a string
 */
public interface SimilarityMatrixPrinter<T extends  Comparable> {
    public String printMatrix(SimilarityMatrix<T> matrix);
}
