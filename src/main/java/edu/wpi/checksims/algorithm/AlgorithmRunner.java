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
 * Copyright (c) 2014-2015 Matthew Heon and Dolan Murvihill
 */

package edu.wpi.checksims.algorithm;

import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.util.UnorderedPair;
import edu.wpi.checksims.util.threading.ParallelAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Run a plagiarism detection algorithm on a list of submissions
 */
public final class AlgorithmRunner {
    private AlgorithmRunner() {}

    public static Collection<AlgorithmResults> runAlgorithm(List<Submission> submissions, SimilarityDetector algorithm) {
        checkNotNull(submissions);
        checkArgument(submissions.size() >= 2);
        checkNotNull(algorithm);

        Logger logs = LoggerFactory.getLogger(AlgorithmRunner.class);
        long startTime = System.currentTimeMillis();

        Set<UnorderedPair<Submission>> allPairs = UnorderedPair.generatePairsFromList(submissions);

        logs.info("Performing similarity detection on " + allPairs.size() + " pairs using algorithm " + algorithm.getName());

        // Perform parallel analysis of all submission pairs to generate a results list
        Collection<AlgorithmResults> results = ParallelAlgorithm.parallelSimilarityDetection(algorithm, allPairs);

        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;

        logs.info("Finished similarity detection in " + timeElapsed + " ms");

        return results;
    }
}
