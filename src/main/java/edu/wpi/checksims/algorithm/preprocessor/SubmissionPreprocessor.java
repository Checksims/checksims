package edu.wpi.checksims.algorithm.preprocessor;

import edu.wpi.checksims.Submission;


/**
 * Interface for submission preprocessors which act on submissions
 *
 * The contract for PreprocessSubmissions() requests a Function from Submission<T> to Submission<T>,
 * which this can act as via a method reference.
 */
public interface SubmissionPreprocessor<T extends Comparable<T>> {
    public Submission<T> process(Submission<T> submission);
}
