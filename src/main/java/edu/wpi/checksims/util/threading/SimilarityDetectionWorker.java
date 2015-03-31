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

package edu.wpi.checksims.util.threading;

import edu.wpi.checksims.ChecksimsException;
import edu.wpi.checksims.algorithm.AlgorithmResults;
import edu.wpi.checksims.algorithm.SimilarityDetector;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.util.UnorderedPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * Basic unit of thread execution for similarity detection.
 *
 * Takes two Submissions, applies an algorithm to them, returns results.
 */
public class SimilarityDetectionWorker implements Callable<AlgorithmResults> {
    private final SimilarityDetector algorithm;
    private final UnorderedPair<Submission> submissions;

    private static Logger logs = LoggerFactory.getLogger(SimilarityDetectionWorker.class);

    /**
     * Construct a Callable to perform pairwise similarity detection for one pair of assignments
     *
     * @param algorithm Algorithm to use
     * @param submissions Assignments to compare
     */
    public SimilarityDetectionWorker(SimilarityDetector algorithm, UnorderedPair<Submission> submissions) {
        this.algorithm = algorithm;
        this.submissions = submissions;
    }

    /**
     * Perform pairwise similarity detection on assignments given when constructed
     *
     * @return Results of pairwise similarity detection
     * @throws Exception Not used - all exceptions will be RuntimeException or similar
     */
    @Override
    public AlgorithmResults call() throws Exception {
        logs.debug("Running " + algorithm.getName() + " on submissions " + submissions.first.getName() +
                "(" + submissions.first.getNumTokens() + " tokens) and " + submissions.second.getName() + " (" +
                submissions.second.getNumTokens() + " tokens)");

        try {
            return algorithm.detectSimilarity(submissions.first, submissions.second);
        } catch (ChecksimsException e) {
            logs.error("Fatal error running " + algorithm.getName() + " on submissions " + submissions.first.getName() + " and " + submissions.second.getName());
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "Similarity detection worker for submissions \"" + submissions.first.getName() + "\" and \"" + submissions.second.getName() + "\"";
    }
}
