package edu.wpi.checksims.algorithm;

import edu.wpi.checksims.ChecksimException;
import edu.wpi.checksims.Submission;

/**
 * Detect plagiarism between two submissions
 */
public interface PlagiarismDetector {
    public AlgorithmResults detectPlagiarism(Submission a, Submission b) throws ChecksimException;
}
