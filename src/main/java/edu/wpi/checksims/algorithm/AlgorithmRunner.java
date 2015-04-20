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
import edu.wpi.checksims.util.threading.ParallelAlgorithm;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Run a pairwise similarity detection algorithm on a number of submission pairs
 */
public final class AlgorithmRunner {
    private AlgorithmRunner() {}

    /**
     * Run a pairwise similarity detection algorithm
     *
     * @param submissions Pairs to run on
     * @param algorithm Algorithm to use
     * @return Collection of AlgorithmResults, one for each input pair
     */
    public static Set<AlgorithmResults> runAlgorithm(Set<Pair<Submission, Submission>> submissions, SimilarityDetector algorithm) {
        checkNotNull(submissions);
        checkArgument(submissions.size() > 0);
        checkNotNull(algorithm);

        Logger logs = LoggerFactory.getLogger(AlgorithmRunner.class);
        long startTime = System.currentTimeMillis();

        logs.info("Performing similarity detection on " + submissions.size() + " pairs using algorithm " + algorithm.getName());

        // Perform parallel analysis of all submission pairs to generate a results list
        Set<AlgorithmResults> results = ParallelAlgorithm.parallelSimilarityDetection(algorithm, submissions);

        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;

        logs.info("Finished similarity detection in " + timeElapsed + " ms");

        return results;
    }
}
