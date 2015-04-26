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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import edu.wpi.checksims.algorithm.AlgorithmResults;
import edu.wpi.checksims.algorithm.InternalAlgorithmError;
import edu.wpi.checksims.submission.NoSuchSubmissionException;
import edu.wpi.checksims.submission.Submission;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A Similarity Matrix represents the similarities between a given group of submissions
 *
 * TODO consider offering Iterators for the entire similarity matrix, and for individual submissions on the X axis
 */
public final class SimilarityMatrix {
    private final MatrixEntry[][] entries;
    private final ImmutableList<Submission> xSubmissions;
    private final ImmutableList<Submission> ySubmissions;
    private final ImmutableSet<AlgorithmResults> builtFrom;

    /**
     * Create a Similarity Matrix with given parameters. Internal constructor used by factory methods.
     * <p/>
     * Lists, not sets, of submissions, to ensure we have an ordering. We maintain the invariant that there are no
     * duplicates in the factories.
     *
     * @param entries      The matrix itself
     * @param xSubmissions Submissions on the X axis
     * @param ySubmissions Submissions on the Y axis
     * @param builtFrom    Set of Algorithm Results used to build the matrix
     */
    protected SimilarityMatrix(MatrixEntry[][] entries, List<Submission> xSubmissions, List<Submission> ySubmissions, Set<AlgorithmResults> builtFrom) {
        checkNotNull(entries);
        checkNotNull(xSubmissions);
        checkNotNull(ySubmissions);
        checkNotNull(builtFrom);
        checkArgument(!xSubmissions.isEmpty(), "Cannot make similarity matrix with empty list of submissions to be compared!");
        checkArgument(!ySubmissions.isEmpty(), "Cannot make similarity matrix with empty list of submissions to compare to!");
        checkArgument(xSubmissions.size() == entries.length, "Array size mismatch when creating Similarity Matrix - X direction, found " + xSubmissions.size() + ", expecting " + entries.length);
        checkArgument(ySubmissions.size() == entries[0].length, "Array size mismatch when creating Similarity Matrix - Y direction, found " + ySubmissions.size() + ", expecting " + entries[0].length);
        checkArgument(!builtFrom.isEmpty(), "Must provide Algorithm Results used to build similarity matrix - instead got empty set!");

        this.entries = entries;
        this.xSubmissions = ImmutableList.copyOf(xSubmissions);
        this.ySubmissions = ImmutableList.copyOf(ySubmissions);
        this.builtFrom = ImmutableSet.copyOf(builtFrom);
    }

    /**
     * @param index Index of submission to retrieve
     * @return Submission for the given row in the array
     */
    public Submission getXSubmission(int index) {
        checkArgument(index >= 0, "Index into X submissions must be greater than 0!");
        checkArgument(index < xSubmissions.size(), "Index into X submissions must be less than X submissions size ("
                + xSubmissions.size() + ")!");

        return xSubmissions.get(index);
    }

    /**
     * @return List of submissions used to build the X axis, in order they are used
     */
    public ImmutableList<Submission> getXSubmissions() {
        return xSubmissions;
    }

    /**
     * @param index Index of submission to retrieve
     * @return Submission for the given column in the array
     */
    public Submission getYSubmission(int index) {
        checkArgument(index >= 0, "Index into Y submissions must be greater than 0!");
        checkArgument(index < ySubmissions.size(), "Index into Y submissions must be less than Y submissions size ("
                + ySubmissions.size() + ")!");

        return ySubmissions.get(index);
    }

    /**
     * @return List of submissions used to build the Y axis, in order they are used
     */
    public ImmutableList<Submission> getYSubmissions() {
        return ySubmissions;
    }

    /**
     * @return Size of the Similarity Matrix
     */
    public Pair<Integer, Integer> getArrayBounds() {
        return Pair.of(xSubmissions.size(), ySubmissions.size());
    }

    /**
     * @return Get the Algorithm Results that were used to build this similarity matrix
     */
    public ImmutableSet<AlgorithmResults> getBaseResults() {
        return builtFrom;
    }

    /**
     * Get similarities for one submission compared to another
     *
     * @param xIndex Index into similarity matrix on the X axis
     * @param yIndex Index into similarity matrix on the Y axis
     * @return Matrix Entry for given X and Y index
     */
    public MatrixEntry getEntryFor(int xIndex, int yIndex) {
        checkArgument(xIndex >= 0, "X index must be greater than 0!");
        checkArgument(xIndex < xSubmissions.size(), "X index must be less than X submissions size (" + xSubmissions.size() + ")!");
        checkArgument(yIndex >= 0, "Y index must be greater than 0!");
        checkArgument(yIndex < ySubmissions.size(), "Y index must be less than Y submissions size (" + ySubmissions.size() + ")!");

        return entries[xIndex][yIndex];
    }

    /**
     * Get similarity of X submission to Y submission
     *
     * @param xSubmission Submission to get similarities for
     * @param ySubmission Submission to get similarities relative to (IE retrieve similarities of xSubmission to ySubmission)
     * @return Similarities of xSubmission to ySubmission
     * @throws NoSuchSubmissionException Thrown if either xSubmission or ySubmission are not present in the similarity matrix
     */
    public MatrixEntry getEntryFor(Submission xSubmission, Submission ySubmission) throws NoSuchSubmissionException {
        checkNotNull(xSubmission);
        checkNotNull(ySubmission);

        if (!xSubmissions.contains(xSubmission)) {
            throw new NoSuchSubmissionException("X Submission with name " + xSubmission.getName() + " not found in similarity matrix!");
        } else if (!ySubmissions.contains(ySubmission)) {
            throw new NoSuchSubmissionException("Y Submission with name " + ySubmission.getName() + " not found in similarity matrix!");
        }

        int xIndex = xSubmissions.indexOf(xSubmission);
        int yIndex = ySubmissions.indexOf(ySubmission);

        return entries[xIndex][yIndex];
    }

    @Override
    public String toString() {
        return "A similarity matrix comparing " + xSubmissions.size() + " submissions to " + ySubmissions.size();
    }

    @Override
    public int hashCode() {
        return builtFrom.stream().mapToInt(AlgorithmResults::hashCode).sum();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof SimilarityMatrix)) {
            return false;
        }

        SimilarityMatrix otherMatrix = (SimilarityMatrix) other;

        return otherMatrix.builtFrom.equals(builtFrom) && otherMatrix.xSubmissions.equals(xSubmissions)
                && otherMatrix.ySubmissions.equals(ySubmissions) && Arrays.deepEquals(otherMatrix.entries, entries);
    }

    /**
     * Generate a similarity matrix from a given set of submissions
     *
     * @param inputSubmissions Submissions to generate from
     * @param results          Results to build from. Must contain results for every possible unordered pair of input submissions.
     * @return Similarity Matrix built from given results
     * @throws InternalAlgorithmError Thrown on missing results, or results containing a submission not in the input
     */
    public static SimilarityMatrix generateMatrix(Set<Submission> inputSubmissions, Set<AlgorithmResults> results) throws InternalAlgorithmError {
        checkNotNull(inputSubmissions);
        checkNotNull(results);
        checkArgument(!inputSubmissions.isEmpty(), "Must provide at least 1 submission to build matrix from");
        checkArgument(!results.isEmpty(), "Must provide at least 1 AlgorithmResults to build matrix from!");

        // Generate the matrix we'll use
        MatrixEntry[][] matrix = new MatrixEntry[inputSubmissions.size()][inputSubmissions.size()];

        // Order the submissions
        List<Submission> orderedSubmissions = Ordering.natural().immutableSortedCopy(inputSubmissions);

        // Generate the matrix

        // Start with the diagonal, filling with 100% similarity
        for (int i = 0; i < orderedSubmissions.size(); i++) {
            Submission s = orderedSubmissions.get(i);

            matrix[i][i] = new MatrixEntry(s, s, s.getNumTokens());
        }

        // Now go through all the results, and build appropriate two MatrixEntry objects for each
        for (AlgorithmResults result : results) {
            int aIndex = orderedSubmissions.indexOf(result.a);
            int bIndex = orderedSubmissions.indexOf(result.b);

            if (aIndex == -1) {
                throw new InternalAlgorithmError("Processed Algorithm Result with submission not in given input submissions with name \""
                        + result.a.getName() + "\"");
            } else if (bIndex == -1) {
                throw new InternalAlgorithmError("Processed Algorithm Result with submission not in given input submissions with name \""
                        + result.b.getName() + "\"");
            }

            matrix[aIndex][bIndex] = new MatrixEntry(result.a, result.b, result.identicalTokensA);
            matrix[bIndex][aIndex] = new MatrixEntry(result.b, result.a, result.identicalTokensB);
        }

        // Verification pass: Go through and ensure that the entire array was populated
        for (int x = 0; x < orderedSubmissions.size(); x++) {
            for (int y = 0; y < orderedSubmissions.size(); y++) {
                if (matrix[x][y] == null) {
                    throw new InternalAlgorithmError("Missing Algorithm Results for comparison of submissions \""
                            + orderedSubmissions.get(x).getName() + "\" and \"" + orderedSubmissions.get(y).getName()
                            + "\"");
                }
            }
        }

        return new SimilarityMatrix(matrix, orderedSubmissions, orderedSubmissions, results);
    }

    /**
     * Generate a Similarity Matrix with archive submissions
     *
     * The result is not a square matrix. Only the input submissions are on the X axis, but the Y axis contains both
     * input and archive submissions.
     *
     * @param inputSubmissions Submissions used to generate matrix
     * @param archiveSubmissions Archive submissions - only compared to input submissions, not to each other
     * @param results Results used to build matrix
     * @return Similarity matrix built from given results
     * @throws InternalAlgorithmError Thrown on missing results, or results containing a submission not in the input
     */
    public static SimilarityMatrix generateMatrix(Set<Submission> inputSubmissions, Set<Submission> archiveSubmissions, Set<AlgorithmResults> results) throws InternalAlgorithmError {
        checkNotNull(inputSubmissions);
        checkNotNull(archiveSubmissions);
        checkNotNull(results);
        checkArgument(!inputSubmissions.isEmpty(), "Must provide at least 1 submission to build matrix from");
        checkArgument(!results.isEmpty(), "Must provide at least 1 AlgorithmResults to build matrix from!");

        // TODO consider ensuring that no submissions are shared between archiveSubmissions and inputSubmissions

        // If there are no archive submissions, just generate using the other function
        if(archiveSubmissions.isEmpty()) {
            return generateMatrix(inputSubmissions, results);
        }

        Set<Submission> ySubmissionsUnsorted = new HashSet<>();
        ySubmissionsUnsorted.addAll(inputSubmissions);
        ySubmissionsUnsorted.addAll(archiveSubmissions);

        List<Submission> xSubmissions = Ordering.natural().immutableSortedCopy(inputSubmissions);
        List<Submission> ySubmissions = Ordering.natural().immutableSortedCopy(ySubmissionsUnsorted);

        MatrixEntry[][] matrix = new MatrixEntry[xSubmissions.size()][ySubmissions.size()];

        // Generate the matrix

        // First, handle identical submissions
        for(Submission xSub : xSubmissions) {
            // Get the X index
            int xIndex = xSubmissions.indexOf(xSub);
            int yIndex = ySubmissions.indexOf(xSub);

            matrix[xIndex][yIndex] = new MatrixEntry(xSub, xSub, xSub.getNumTokens());
        }

        // Now iterate through all given algorithm results
        for(AlgorithmResults result : results) {
            int aXCoord = xSubmissions.indexOf(result.a);
            int bXCoord = xSubmissions.indexOf(result.b);

            if(aXCoord == -1 && bXCoord == -1) {
                throw new InternalAlgorithmError("Neither submission \"" + result.a.getName() + "\" nor \"" +
                        result.b.getName() + "\" were found in input submissions!");
            }

            if(aXCoord != -1) {
                int bYCoord = ySubmissions.indexOf(result.b);

                matrix[aXCoord][bYCoord] = new MatrixEntry(result.a, result.b, result.identicalTokensA);
            }

            if(bXCoord != -1) {
                int aYCoord = ySubmissions.indexOf(result.a);

                matrix[bXCoord][aYCoord] = new MatrixEntry(result.b, result.a, result.identicalTokensB);
            }
        }

        // Verification pass - ensure we built a matrix with no nulls
        for(int x = 0; x < xSubmissions.size(); x++) {
            for(int y = 0; y < ySubmissions.size(); y++) {
                if(matrix[x][y] == null) {
                    throw new InternalAlgorithmError("Missing Algorithm Results for comparison of submissions \""
                            + xSubmissions.get(x).getName() + "\" and \"" + ySubmissions.get(y).getName()
                            + "\"");
                }
            }
        }

        return new SimilarityMatrix(matrix, xSubmissions, ySubmissions, results);
    }
}
