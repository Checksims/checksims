package edu.wpi.checksims.algorithm;

import edu.wpi.checksims.Submission;

import java.util.List;

/**
 * A matrix of submissions, for reporting similarities
 */
public class SimilarityMatrix<T extends Comparable<T>> {
    private final List<Submission<T>> submissions;
    private final float[][] results;

    // TODO the list should probably be a set, but need a good equals() and hashCode() for Submission<T> first
    public SimilarityMatrix(List<Submission<T>> submissions, PlagiarismDetector<T> algorithm) {
        this.submissions = submissions;
        this.results = getResults(submissions, algorithm);
    }

    // TODO should really clone() this list, or set it to immutable
    public List<Submission<T>> getSubmissions() {
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
    private float[][] getResults(List<Submission<T>> submissions, PlagiarismDetector<T> algorithm) {
        float[][] results = new float[submissions.size()][submissions.size()];

        // Get results for all possible pairs of submissions
        List<AlgorithmResults<T>> algorithmResults = AlgorithmRunner.runAlgorithm(submissions, algorithm);

        // First, null the diagonal of the results array
        for(int i = 0; i < submissions.size(); i++) {
            results[i][i] = 0.00f; // Same submission, ignore
        }

        // For each result, fill corresponding spots in the results matrix
        algorithmResults.stream().forEach((result) -> {
            int indexFirst = submissions.indexOf(result.a);
            int indexSecond = submissions.indexOf(result.b);

            results[indexFirst][indexSecond] = result.percentMatchedA();
            results[indexSecond][indexFirst] = result.percentMatchedB();
        });

        return results;
    }

    @Override
    public String toString() {
        return "A similarity matrix of " + submissions.size() + " submissions";
    }
}
