package edu.wpi.checksims.algorithm;

import edu.wpi.checksims.ChecksimException;
import edu.wpi.checksims.Submission;

/**
 * Detect plagiarism between two submissions
 */
public interface PlagiarismDetector<T extends Comparable<T>> {
    public AlgorithmResults<T> detectPlagiarism(Submission<T> a, Submission<T> b) throws ChecksimException;
}
