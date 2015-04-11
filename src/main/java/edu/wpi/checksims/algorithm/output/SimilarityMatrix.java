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

package edu.wpi.checksims.algorithm.output;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import edu.wpi.checksims.algorithm.AlgorithmResults;
import edu.wpi.checksims.algorithm.AlgorithmRunner;
import edu.wpi.checksims.algorithm.SimilarityDetector;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.util.PairGenerator;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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
        checkNotNull(submissions);
        checkArgument(submissions.size() >= 2);
        checkNotNull(algorithm);

        Logger logs = LoggerFactory.getLogger(SimilarityMatrix.class);

        float[][] results = new float[submissions.size()][submissions.size()];
        logs.debug("Sorting submissions prior to algorithm pass...");
        List<Submission> submissionsSorted = Ordering.from((Submission a, Submission b) -> a.getName().compareTo(b.getName())).immutableSortedCopy(submissions);

        // Generate all possible unordered pairs of submissions
        Set<Submission> submissionsAsSet = ImmutableSet.copyOf(submissions);
        Set<Pair<Submission, Submission>> pairs = PairGenerator.generatePairs(submissionsAsSet);

        // Get results for all possible pairs of submissions
        Collection<AlgorithmResults> algorithmResults = AlgorithmRunner.runAlgorithm(pairs, algorithm);

        // First, zero the diagonal of the results array
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
