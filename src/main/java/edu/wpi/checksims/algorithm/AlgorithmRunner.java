package edu.wpi.checksims.algorithm;

import edu.wpi.checksims.ChecksimException;
import edu.wpi.checksims.Submission;
import edu.wpi.checksims.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Run a plagiarism detection algorithm on a list of submissions
 */
public class AlgorithmRunner {
    private AlgorithmRunner() {}

    public static List<AlgorithmResults> runAlgorithm(List<Submission> submissions, PlagiarismDetector algorithm) {
        Logger logs = LoggerFactory.getLogger(AlgorithmRunner.class);

        List<AlgorithmResults> results = Collections.synchronizedList(new LinkedList<>());

        Set<Pair<Submission>> allPairs = Pair.generatePairsFromList(submissions);

        // Perform parallel analysis of all submission pairs to generate result lists
        allPairs.stream().parallel().forEach((pair) -> {
            try {
                logs.trace("Running " + algorithm.getName() + " on submissions " + pair.first.getName() + " and " + pair.second.getName());

                AlgorithmResults result = algorithm.detectPlagiarism(pair.first, pair.second);

                results.add(result);
            } catch(ChecksimException e) {
                throw new RuntimeException(e.getMessage()); // TODO these has to be a better way to handle this
            }
        });

        return results;
    }
}
