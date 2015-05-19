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
 * Copyright (c) 2014-2015 Nicholas DeMarinis, Matthew Heon, and Dolan Murvihill
 */

package net.lldp.checksims.algorithm.similaritymatrix.output;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import net.lldp.checksims.algorithm.AlgorithmResults;
import net.lldp.checksims.algorithm.InternalAlgorithmError;
import net.lldp.checksims.algorithm.similaritymatrix.SimilarityMatrix;
import net.lldp.checksims.submission.Submission;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Print all results over a certain threshold.
 */
public final class MatrixThresholdPrinter implements MatrixPrinter {
    private static MatrixThresholdPrinter instance;

    private static final double threshold = 0.65;

    private MatrixThresholdPrinter() {}

    /**
     * @return Singleton instance of MatrixThresholdPrinter
     */
    public static MatrixThresholdPrinter getInstance() {
        if(instance == null) {
            instance = new MatrixThresholdPrinter();
        }

        return instance;
    }

    /**
     * Print significant results in a similarity matrix.
     *
     * @param matrix Matrix to print
     * @return Results in matrix over threshold
     * @throws InternalAlgorithmError Thrown on internal error processing matrix
     */
    @Override
    public String printMatrix(SimilarityMatrix matrix) throws InternalAlgorithmError {
        checkNotNull(matrix);

        StringBuilder builder = new StringBuilder();
        DecimalFormat formatter = new DecimalFormat("0.##");

        ImmutableSet<AlgorithmResults> results = matrix.getBaseResults();

        Set<AlgorithmResults> filteredBelowThreshold = results.stream()
                .filter((result) -> result.percentMatchedA() >= threshold || result.percentMatchedB() >= threshold)
                .collect(Collectors.toCollection(HashSet::new));

        if(filteredBelowThreshold.isEmpty()) {
            builder.append("No significant matches found.\n");
        }

        // Loop until all results over threshold consumed
        while(!filteredBelowThreshold.isEmpty()) {
            // Find the largest single result
            double largest = 0.00;
            AlgorithmResults largestResult = Iterables.get(filteredBelowThreshold, 0);
            for(AlgorithmResults result : filteredBelowThreshold) {
                if(result.percentMatchedA() > largest) {
                    largest = result.percentMatchedA();
                    largestResult = result;
                }
                if(result.percentMatchedB() > largest) {
                    largest = result.percentMatchedB();
                    largestResult = result;
                }
            }

            double largerOfTwo;
            double smallerOfTwo;
            Submission largerSubmission;
            Submission smallerSubmission;

            if(largestResult.percentMatchedA() >= largestResult.percentMatchedB()) {
                largerOfTwo = largestResult.percentMatchedA() * 100;
                smallerOfTwo = largestResult.percentMatchedB() * 100;
                largerSubmission = largestResult.a;
                smallerSubmission = largestResult.b;
            } else {
                largerOfTwo = largestResult.percentMatchedB() * 100;
                smallerOfTwo = largestResult.percentMatchedA() * 100;
                largerSubmission = largestResult.b;
                smallerSubmission = largestResult.a;
            }

            // We have the largest single result, print it
            builder.append("Found match of ");
            builder.append(formatter.format(largerOfTwo));
            builder.append("% (inverse match ");
            builder.append(formatter.format(smallerOfTwo));
            builder.append("%) between submissions \"");
            builder.append(largerSubmission.getName());
            builder.append("\" and \"");
            builder.append(smallerSubmission.getName());
            builder.append("\"\n");

            // Remove the largest results
            filteredBelowThreshold.remove(largestResult);
        }

        return builder.toString();
    }

    @Override
    public String getName() {
        return "threshold";
    }

    @Override
    public String toString() {
        return "Singleton instance of MatrixThresholdPrinter";
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof MatrixThresholdPrinter;
    }
}
