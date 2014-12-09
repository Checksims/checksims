package edu.wpi.checksims.algorithm.output;

import com.google.common.collect.ImmutableList;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.algorithm.AlgorithmResults;
import edu.wpi.checksims.algorithm.AlgorithmRunner;
import edu.wpi.checksims.algorithm.SimilarityDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A matrix of submissions, for reporting similarities
 */
public class SimilarityMatrix {
    private final List<Submission> submissions;
    private final float[][] results;

    private SimilarityMatrix(List<Submission> submissions, float[][] results) {
        this.submissions = ImmutableList.copyOf(submissions);
        this.results = results;
    }

    // TODO should really clone() this list, or set it to immutable
    public List<Submission> getSubmissions() {
        return this.submissions;
    }

    public float[][] getResults() {
        return this.results.clone();
    }

    /**
     * Fill the results matrix
     *
     * NOTE that this is inefficient; we calculate twice for each pair, as (i,j) and (j,i).
     * If speed optimizations are required, recommend starting here.
     *
     * @param submissions Submissions to generate results matrix from
     * @param algorithm Algorithm to use when detecting plagiarism
     * @return Array of algorithm results, with results[i,j] being the results of comparing students i and j
     */
    public static SimilarityMatrix generate(List<Submission> submissions, SimilarityDetector algorithm) {
        float[][] results = new float[submissions.size()][submissions.size()];
        List<Submission> submissionsSorted = new LinkedList<>(submissions);
        Logger logs = LoggerFactory.getLogger(SimilarityMatrix.class);

        logs.debug("Sorting submissions prior to algorithm pass...");
        Collections.sort(submissionsSorted, (a, b) -> a.getName().compareTo(b.getName()));

        // Get results for all possible pairs of submissions
        List<AlgorithmResults> algorithmResults = AlgorithmRunner.runAlgorithm(submissionsSorted, algorithm);

        // First, null the diagonal of the results array
        for(int i = 0; i < submissionsSorted.size(); i++) {
            results[i][i] = 0.00f; // Same submission, ignore
        }

        // For each result, fill corresponding spots in the results matrix
        algorithmResults.stream().forEach((result) -> {
            int indexFirst = submissionsSorted.indexOf(result.a);
            int indexSecond = submissionsSorted.indexOf(result.b);

            if(indexFirst == -1) {
                throw new RuntimeException("Could not find index of submission " + result.a.getName());
            } else if(indexSecond == -1) {
                throw new RuntimeException("Could not find index of submission " + result.b.getName());
            }

            results[indexFirst][indexSecond] = result.percentMatchedA();
            results[indexSecond][indexFirst] = result.percentMatchedB();
        });

        logs.info("Done performing similarity detection");

        return new SimilarityMatrix(submissionsSorted, results);
    }

    @Override
    public String toString() {
        return "A similarity matrix of " + submissions.size() + " submissions";
    }
}
