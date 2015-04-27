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

package edu.wpi.checksims.algorithm.similaritymatrix;

import edu.wpi.checksims.submission.Submission;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An entry in the Similarity Matrix
 */
public final class MatrixEntry {
    private final Submission base;
    private final Submission comparedTo;
    private final double similarityPercent;
    private final int similarTokens;
    private final int totalTokens;

    /**
     * Construct a Similarity Matrix entry
     *
     * @param base Submission we are reporting relative to
     * @param comparedTo Submission being compared to
     * @param similarTokens Number of tokens shared by both submissions
     */
    public MatrixEntry(Submission base, Submission comparedTo, int similarTokens) {
        checkNotNull(base);
        checkNotNull(comparedTo);
        checkArgument(similarTokens >= 0, "There cannot be a negative number of similar tokens");
        checkArgument(similarTokens <= base.getNumTokens(), "Attempted to created MatrixEntry with " + similarTokens
                + " similar tokens --- only " + base.getNumTokens() + " tokens in base!");

        if(base.getNumTokens() == 0) {
            this.similarityPercent = 0.0;
        } else {
            this.similarityPercent = (double)similarTokens / (double)base.getNumTokens();
        }

        this.base = base;
        this.comparedTo = comparedTo;
        this.similarTokens = similarTokens;
        this.totalTokens = base.getNumTokens();
    }

    /**
     * @return Base submission we are comparing
     */
    public Submission getBase() {
        return base;
    }

    /**
     * @return Submission the base is being compared to
     */
    public Submission getComparedTo() {
        return comparedTo;
    }

    /**
     * @return Percentage similarity of base submission to compared submission
     */
    public double getSimilarityPercent() {
        return similarityPercent;
    }

    /**
     * @return Number of identical tokens between the two submissions
     */
    public int getSimilarTokens() {
        return similarTokens;
    }

    /**
     * @return Total number of tokens in base submission
     */
    public int getTotalTokens() {
        return totalTokens;
    }

    @Override
    public String toString() {
        return "Similarity Matrix Entry comparing " + base.getName() + " and " + comparedTo.getName();
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof MatrixEntry)) {
            return false;
        }

        MatrixEntry otherEntry = (MatrixEntry)other;

        return otherEntry.getBase().equals(base) && otherEntry.getComparedTo().equals(comparedTo)
                && otherEntry.getSimilarTokens() == similarTokens;
    }

    @Override
    public int hashCode() {
        return (base.hashCode() ^ comparedTo.hashCode()) * similarTokens;
    }
}
