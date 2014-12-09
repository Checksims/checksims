package edu.wpi.checksims.algorithm;

import edu.wpi.checksims.ChecksimException;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.token.TokenType;

/**
 * Detect similarities between two submissions
 *
 * NOTE that, in addition to the methods listed here, all plagiarism detectors MUST support a no-arguments getInstance()
 * method, and be contained in edu.wpi.checksims.algorithm or a subpackage thereof.
 *
 * This is required as reflection is used to automatically detect and instantiate all similarity detection algorithms
 * present at runtime.
 */
public interface SimilarityDetector {
    /**
     * MUST BE UNIQUE FOR EACH SIMILARITY DETECTOR
     *
     * @return Name of the plagiarism detector used to invoke it at the CLI
     */
    public String getName();

    public TokenType getDefaultTokenType();

    /**
     * Apply a pairwise similarity detection algorithm
     *
     * Token list types of A and B must match
     *
     * @param a First submission to apply to
     * @param b Second submission to apply to
     * @return Similarity results of comparing submissions A and B
     * @throws ChecksimException Thrown on algorithm error or mismatched tokenization list types
     */
    public AlgorithmResults detectSimilarity(Submission a, Submission b) throws ChecksimException;
}
