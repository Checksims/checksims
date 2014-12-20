/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright (c) 2014 Matthew Heon and Dolan Murvihill
 */

package edu.wpi.checksims.algorithm;

import edu.wpi.checksims.ChecksimException;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.util.UnorderedPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Run a plagiarism detection algorithm on a list of submissions
 */
public class AlgorithmRunner {
    private AlgorithmRunner() {}

    public static List<AlgorithmResults> runAlgorithm(List<Submission> submissions, SimilarityDetector algorithm) {
        Logger logs = LoggerFactory.getLogger(AlgorithmRunner.class);
        AtomicInteger submissionsProcessed = new AtomicInteger(0);
        long startTime = System.currentTimeMillis();

        List<AlgorithmResults> results = Collections.synchronizedList(new LinkedList<>());

        Set<UnorderedPair<Submission>> allPairs = UnorderedPair.generatePairsFromList(submissions);

        // Perform parallel analysis of all submission pairs to generate result lists
        allPairs.stream().parallel().forEach((pair) -> {
            try {
                int curProcessed = submissionsProcessed.incrementAndGet();
                logs.info("Processing submission pair " + curProcessed + "/" + allPairs.size());

                logs.debug("Running " + algorithm.getName() + " on submissions " + pair.first.getName() +
                        "(" + pair.first.getNumTokens() + " tokens) and " + pair.second.getName() + " (" +
                        pair.second.getNumTokens() + " tokens)");

                AlgorithmResults result = algorithm.detectSimilarity(pair.first, pair.second);

                results.add(result);
            } catch(ChecksimException e) {
                logs.error("Fatal error running " + algorithm.getName() + " on submissions " + pair.first.getName() + " and " + pair.second.getName());
                throw new RuntimeException(e);
            }
        });

        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;

        logs.info("Finished similarity detection in " + timeElapsed + " ms");

        return results;
    }
}
