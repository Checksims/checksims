package edu.wpi.checksims.algorithm;

import edu.wpi.checksims.ChecksimException;
import edu.wpi.checksims.Submission;

import java.util.List;

/**
 * A matrix of submissions, for reporting similarities
 */
public class SimilarityMatrix<T extends Comparable> {
    private final List<Submission<T>> submissions;
    private final AlgorithmResults<T>[][] results;

    // TODO the list should probably be a set, but need a good equals() and hashCode() for Submission<T> first
    public SimilarityMatrix(List<Submission<T>> submissions, PlagiarismDetector<T> algorithm) {
        this.submissions = submissions;
        this.results = getResults(submissions, algorithm);
    }

    public List<Submission<T>> getSubmissions() {
        return this.submissions;
    }

    // TODO should write function to clone matrix (clone() don't work so well with nonprimitive types) and return that
    public AlgorithmResults<T>[][] getResults() {
        return this.results;
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
    private AlgorithmResults[][] getResults(List<Submission<T>> submissions, PlagiarismDetector<T> algorithm) {
        // Need at least two submissions to compare
        if(submissions.size()  <= 1) {
            return null;
        }

        int arraySize = submissions.size();

        AlgorithmResults[][] resultsMatrix = new AlgorithmResults[arraySize][arraySize];

        for(int i = 0; i < arraySize; i++) {
            for(int j = 0; j < arraySize; j++) {
                if(i == j) {
                    // The diagonal means the submissions are the same, just keep the results null.
                    resultsMatrix[i][j] = null;
                    continue;
                }

                Submission<T> a = submissions.get(i);
                Submission<T> b = submissions.get(i);

                try {
                    AlgorithmResults<T> results = algorithm.detectPlagiarism(a, b);

                    resultsMatrix[i][j] = results;
                } catch (ChecksimException e) {
                    // TODO handle this exception in a more sane manner
                    throw new RuntimeException("Error getting results for student " + a.getName() + " and " +
                            b.getName() + ": " + e.getMessage());
                }
            }
        }

        return resultsMatrix;
    }

    @Override
    public String toString() {
        return "A similarity matrix of " + submissions.size() + " submissions";
    }
}
