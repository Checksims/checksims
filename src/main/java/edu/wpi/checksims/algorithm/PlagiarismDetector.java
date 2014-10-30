package edu.wpi.checksims.algorithm;

import edu.wpi.checksims.ChecksimException;
import edu.wpi.checksims.Submission;

/**
 * Detect plagiarism between two submissions
 */
public interface PlagiarismDetector {
    /**
     * MUST BE UNIQUE FOR EACH PLAGIARISM DETECTOR
     *
     * @return Name of the plagiarism detector used to invoke it at the CLI
     */
    public String getName();

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
