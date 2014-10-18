package edu.wpi.checksims.algorithm.preprocessor;

import edu.wpi.checksims.Submission;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * Apply a preprocessor (maps Submission to Submission) to a given list of submissions
 */
public class PreprocessSubmissions {
    private PreprocessSubmissions() {}

    /**
     * Apply a given mapping function to each submission in a list of submissions
     *
     * @param mapping Mapping function to apply. SHOULD NOT MUTATE THE EXISTING TOKEN - should return a new token
     * @param submissions Input list of submissions to apply to
     * @param <T> Inner type of the submissions's Token Lists - typically will be String or Character
     * @return New list formed by applying the mapping function to each submission. Retains order of input list.
     */
    public static <T extends Comparable<T>> List<Submission<T>> preprocessSubmissions(Function<Submission<T>,Submission<T>> mapping, List<Submission<T>> submissions) {
        List<Submission<T>> output = new LinkedList<Submission<T>>();

        submissions.stream().forEachOrdered((s) -> output.add(mapping.apply(s)));

        return output;
    }
}
