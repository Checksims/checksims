package edu.wpi.checksims.algorithm;

import edu.wpi.checksims.ChecksimException;
import edu.wpi.checksims.Submission;
import edu.wpi.checksims.util.token.TokenType;

/**
 * Detect plagiarism between two submissions
 *
 * NOTE that, in addition to the methods listed here, all plagiarism detectors MUST support a no-arguments getInstance()
 * method, and be contained in edu.wpi.checksims.algorithm or a subpackage thereof.
 *
 * This is required as reflection is used to automatically detect and instantiate all plagiarism detection algorithms
 * present at runtime.
 */
public interface PlagiarismDetector {
    /**
     * MUST BE UNIQUE FOR EACH PLAGIARISM DETECTOR
     *
     * @return Name of the plagiarism detector used to invoke it at the CLI
     */
    public String getName();

    public TokenType getDefaultTokenType();

    /**
     * Apply a pairwise plagiarism detection algorithm
     *
     * Token list types of A and B must match
     *
     * @param a First submission to apply to
     * @param b Second submission to apply to
     * @return Similarity results of comparing submissions A and B
     * @throws ChecksimException Thrown on algorithm error or mismatched tokenization list types
     */
    public AlgorithmResults detectPlagiarism(Submission a, Submission b) throws ChecksimException;
}
