package edu.wpi.checksims.algorithm;

import edu.wpi.checksims.ChecksimException;
import edu.wpi.checksims.Submission;
import edu.wpi.checksims.util.Pair;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Run a plagiarism detection algorithm on a list of submissions
 */
public class AlgorithmRunner {
    private AlgorithmRunner() {}

    public static <T extends Comparable<T>> List<AlgorithmResults<T>> runAlgorithm(List<Submission<T>> submissions, PlagiarismDetector<T> algorithm) {
        List<AlgorithmResults<T>> results = Collections.synchronizedList(new LinkedList<>());

        Set<Pair<Submission<T>>> allPairs = Pair.generatePairsFromList(submissions);

        // Perform parallel analysis of all submission pairs to generate result lists
        allPairs.stream().parallel().forEach((pair) -> {
            try {
                AlgorithmResults<T> result = algorithm.detectPlagiarism(pair.first, pair.second);

                if(result.percentMatchedA() > 0.50) {
                    System.out.println("Submissions " + pair.first.getName() + " and " + pair.second.getName() +
                            " matched with percentage " + result.percentMatchedA());
                } else if(result.percentMatchedB() > 0.50) {
                    System.out.println("Submissions " + pair.second.getName() + " and " + pair.first.getName() +
                            " matched with percentage " + result.percentMatchedB());
                }

                results.add(result);
            } catch(ChecksimException e) {
                throw new RuntimeException(e.getMessage()); // TODO these has to be a better way to handle this
            }
        });

        return results;
    }
}
