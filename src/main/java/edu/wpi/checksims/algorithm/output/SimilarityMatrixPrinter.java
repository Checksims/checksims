package edu.wpi.checksims.algorithm.output;

/**
 * Interface for various approaches for outputting a similarity matrix as a string
 */
public interface SimilarityMatrixPrinter {
    public String getName();
    public String printMatrix(SimilarityMatrix matrix);
}
