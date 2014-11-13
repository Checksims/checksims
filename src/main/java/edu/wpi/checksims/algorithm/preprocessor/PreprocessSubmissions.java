package edu.wpi.checksims.algorithm.preprocessor;

import edu.wpi.checksims.Submission;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Apply a preprocessor (maps Submission to Submission) to a given list of submissions
 */
public class PreprocessSubmissions {
    private PreprocessSubmissions() {}

    /**
     * Apply a given mapping function to each submission in a list of submissions
     *
     * Is NOT expected to preserve tokenization validity, as these are to be applied before that is significant
     *
     * @param mapping Mapping function to apply. SHOULD NOT MUTATE THE EXISTING TOKEN - should return a new tokenization
     * @param submissions Input list of submissions to apply to
     * @return New list formed by applying the mapping function to each submission. Retains order of input list.
     */
    public static List<Submission> process(Function<Submission, Submission> mapping, List<Submission> submissions) {
        Supplier<List<Submission>> linkedListFactory = LinkedList::new;

        return submissions.stream().map(mapping::apply).collect(Collectors.toCollection(linkedListFactory));
    }
}
