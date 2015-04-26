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

import edu.wpi.checksims.algorithm.AlgorithmResults;
import edu.wpi.checksims.algorithm.InternalAlgorithmError;
import edu.wpi.checksims.submission.NoSuchSubmissionException;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.token.TokenList;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static edu.wpi.checksims.testutil.SubmissionUtils.charSubmissionFromString;
import static edu.wpi.checksims.testutil.SubmissionUtils.setFromElements;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Tests for similarity matrix
 */
public class SimilarityMatrixTest {
    private Submission a;
    private Submission b;
    private Submission c;
    private AlgorithmResults aToA;
    private AlgorithmResults aToB;
    private AlgorithmResults aToC;
    private AlgorithmResults cToA;
    private AlgorithmResults bToC;
    private AlgorithmResults cToD;
    SimilarityMatrix baseMatrixTwoSubmission;
    SimilarityMatrix archiveMatrixTwoSubmissionOneArchive;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() {
        a = charSubmissionFromString("A", "A");
        b = charSubmissionFromString("B", "B");
        c = charSubmissionFromString("C", "C");
        Submission d = charSubmissionFromString("D", "D");

        TokenList aInval = TokenList.cloneTokenList(a.getContentAsTokens());
        aInval.get(0).setValid(false);

        aToA = new AlgorithmResults(a, a, 1, 1, aInval, aInval);

        aToB = new AlgorithmResults(a, b, 0, 0, a.getContentAsTokens(), b.getContentAsTokens());
        aToC = new AlgorithmResults(a, c, 0, 0, a.getContentAsTokens(), c.getContentAsTokens());
        cToA = new AlgorithmResults(c, a, 0, 0, c.getContentAsTokens(), a.getContentAsTokens());
        bToC = new AlgorithmResults(b, c, 0, 0, b.getContentAsTokens(), c.getContentAsTokens());
        cToD = new AlgorithmResults(c, d, 0, 0, c.getContentAsTokens(), d.getContentAsTokens());

        MatrixEntry[][] matrix2 = new MatrixEntry[2][2];
        matrix2[0][0] = new MatrixEntry(a, a, 1);
        matrix2[1][1] = new MatrixEntry(b, b, 1);
        matrix2[1][0] = new MatrixEntry(b, a, 0);
        matrix2[0][1] = new MatrixEntry(a, b, 0);

        baseMatrixTwoSubmission = new SimilarityMatrix(matrix2, Arrays.asList(a, b), Arrays.asList(a, b), singleton(aToB));

        MatrixEntry[][] matrixArchive = new MatrixEntry[2][3];
        matrixArchive[0][0] = new MatrixEntry(a, a, 1);
        matrixArchive[0][1] = new MatrixEntry(a, b, 0);
        matrixArchive[0][2] = new MatrixEntry(a, c, 0);
        matrixArchive[1][0] = new MatrixEntry(b, a, 0);
        matrixArchive[1][1] = new MatrixEntry(b, b, 1);
        matrixArchive[1][2] = new MatrixEntry(b, c, 0);

        archiveMatrixTwoSubmissionOneArchive = new SimilarityMatrix(matrixArchive, Arrays.asList(a, b), Arrays.asList(a, b, c), setFromElements(aToB, aToC, bToC));
    }

    @Test
    public void TestConstructSimilarityMatrixNullMatrix() {
        expectedEx.expect(NullPointerException.class);

        new SimilarityMatrix(null, singletonList(a), singletonList(a), singleton(aToA));
    }

    @Test
    public void TestConstructSimilarityMatrixNullListX() {
        expectedEx.expect(NullPointerException.class);

        new SimilarityMatrix(new MatrixEntry[1][1], null, singletonList(a), singleton(aToA));
    }

    @Test
    public void TestConstructSimilarityMatrixNullListY() {
        expectedEx.expect(NullPointerException.class);

        new SimilarityMatrix(new MatrixEntry[1][1], singletonList(a), null, singleton(aToA));
    }

    @Test
    public void TestConstructSimilarityMatrixNullResults() {
        expectedEx.expect(NullPointerException.class);

        new SimilarityMatrix(new MatrixEntry[1][1], singletonList(a), singletonList(a), null);
    }

    @Test
    public void TestConstructSimilarityMatrixEmptyX() {
        expectedEx.expect(IllegalArgumentException.class);

        new SimilarityMatrix(new MatrixEntry[1][1], new ArrayList<>(), singletonList(a), singleton(aToA));
    }

    @Test
    public void TestConstructSimilarityMatrixEmptyY() {
        expectedEx.expect(IllegalArgumentException.class);

        new SimilarityMatrix(new MatrixEntry[1][1], singletonList(a), new ArrayList<>(), singleton(aToA));
    }

    @Test
    public void TestConstructSimilarityMatrixArraySizeMismatchX() {
        expectedEx.expect(IllegalArgumentException.class);

        new SimilarityMatrix(new MatrixEntry[1][1], Arrays.asList(a, b), singletonList(a), singleton(aToA));
    }

    @Test
    public void TestConstructSimilarityMatrixArraySizeMismatchY() {
        expectedEx.expect(IllegalArgumentException.class);

        new SimilarityMatrix(new MatrixEntry[1][1], singletonList(a), Arrays.asList(a, b), singleton(aToA));
    }

    @Test
    public void TestConstructSimilarityMatrixNoResults() {
        expectedEx.expect(IllegalArgumentException.class);

        new SimilarityMatrix(new MatrixEntry[1][1], singletonList(a), singletonList(a), new HashSet<>());
    }

    @Test
    public void TestSimilarityMatrixGetSubmissions() {
        assertEquals(Arrays.asList(a, b), baseMatrixTwoSubmission.getXSubmissions());
        assertEquals(Arrays.asList(a, b), baseMatrixTwoSubmission.getYSubmissions());
    }

    @Test
    public void TestSimilarityMatrixGetBounds() {
        assertEquals(Pair.of(2, 2), baseMatrixTwoSubmission.getArrayBounds());
    }

    @Test
    public void TestGetXSubmissionNegativeIndex() {
        expectedEx.expect(IllegalArgumentException.class);

        baseMatrixTwoSubmission.getXSubmission(-1);
    }

    @Test
    public void TestGetXSubmissionTooLargeIndex() {
        expectedEx.expect(IllegalArgumentException.class);

        baseMatrixTwoSubmission.getXSubmission(3);
    }

    @Test
    public void TestGetXSubmission() {
        assertEquals(a, baseMatrixTwoSubmission.getXSubmission(0));
        assertEquals(b, baseMatrixTwoSubmission.getXSubmission(1));
    }

    @Test
    public void TestGetYSubmissionNegativeIndex() {
        expectedEx.expect(IllegalArgumentException.class);

        baseMatrixTwoSubmission.getYSubmission(-1);
    }

    @Test
    public void TestGetYSubmissionTooLargeIndex() {
        expectedEx.expect(IllegalArgumentException.class);

        baseMatrixTwoSubmission.getYSubmission(3);
    }

    @Test
    public void TestGetYSubmission() {
        assertEquals(a, baseMatrixTwoSubmission.getYSubmission(0));
        assertEquals(b, baseMatrixTwoSubmission.getYSubmission(1));
    }

    @Test
    public void TestGetBaseResults() {
        assertEquals(singleton(aToB), baseMatrixTwoSubmission.getBaseResults());
    }

    @Test
    public void TestGetEntryNegativeXIndex() {
        expectedEx.expect(IllegalArgumentException.class);

        baseMatrixTwoSubmission.getEntryFor(-1, 1);
    }

    @Test
    public void TestGetEntryNegativeYIndex() {
        expectedEx.expect(IllegalArgumentException.class);

        baseMatrixTwoSubmission.getEntryFor(1, -1);
    }

    @Test
    public void TestGetEntryTooLargeXIndex() {
        expectedEx.expect(IllegalArgumentException.class);

        baseMatrixTwoSubmission.getEntryFor(3, 1);
    }

    @Test
    public void TestGetEntryTooLargeYIndex() {
        expectedEx.expect(IllegalArgumentException.class);

        baseMatrixTwoSubmission.getEntryFor(1, 3);
    }

    @Test
    public void TestGetEntry() {
        assertEquals(new MatrixEntry(a, b, 0), baseMatrixTwoSubmission.getEntryFor(0, 1));
        assertEquals(new MatrixEntry(a, a, 1), baseMatrixTwoSubmission.getEntryFor(0, 0));
        assertEquals(new MatrixEntry(b, a, 0), baseMatrixTwoSubmission.getEntryFor(1, 0));
        assertEquals(new MatrixEntry(b, b, 1), baseMatrixTwoSubmission.getEntryFor(1, 1));
    }

    @Test
    public void TestGetEntryBySubmissionNullXSubmission() throws Exception {
        expectedEx.expect(NullPointerException.class);

        baseMatrixTwoSubmission.getEntryFor(null, a);
    }

    @Test
    public void TestGetEntryBySubmissionNullYSubmission() throws Exception {
        expectedEx.expect(NullPointerException.class);

        baseMatrixTwoSubmission.getEntryFor(a, null);
    }

    @Test(expected = NoSuchSubmissionException.class)
    public void TestGetEntryBySubmissionInvalXSubmission() throws Exception {
        baseMatrixTwoSubmission.getEntryFor(c, a);
    }

    @Test(expected = NoSuchSubmissionException.class)
    public void TestGetEntryBySubmissionInvalYSubmission() throws Exception {
        baseMatrixTwoSubmission.getEntryFor(a, c);
    }

    @Test
    public void TestGetEntryBySubmission() throws Exception {
        assertEquals(new MatrixEntry(a, b, 0), baseMatrixTwoSubmission.getEntryFor(a, b));
        assertEquals(new MatrixEntry(a, a, 1), baseMatrixTwoSubmission.getEntryFor(a, a));
        assertEquals(new MatrixEntry(b, a, 0), baseMatrixTwoSubmission.getEntryFor(b, a));
        assertEquals(new MatrixEntry(b, b, 1), baseMatrixTwoSubmission.getEntryFor(b, b));
    }

    @Test
    public void TestBasicEquality() {
        assertEquals(baseMatrixTwoSubmission, baseMatrixTwoSubmission);
    }

    @Test
    public void TestBasicInequality() {
        assertNotEquals(baseMatrixTwoSubmission, archiveMatrixTwoSubmissionOneArchive);
    }

    @Test
    public void TestGenerateMatrixNullSubmissions() throws Exception {
        expectedEx.expect(NullPointerException.class);

        SimilarityMatrix.generateMatrix(null, setFromElements(aToA));
    }

    @Test
    public void TestGenerateMatrixNullResults() throws Exception {
        expectedEx.expect(NullPointerException.class);

        SimilarityMatrix.generateMatrix(setFromElements(a, b), null);
    }

    @Test
    public void TestGenerateMatrixEmptySubmissions() throws Exception {
        expectedEx.expect(IllegalArgumentException.class);

        SimilarityMatrix.generateMatrix(new HashSet<>(), singleton(aToA));
    }

    @Test
    public void TestGenerateMatrixEmptyResults() throws Exception {
        expectedEx.expect(IllegalArgumentException.class);

        SimilarityMatrix.generateMatrix(setFromElements(a, b), new HashSet<>());
    }

    @Test(expected = InternalAlgorithmError.class)
    public void TestGenerateMatrixTooFewResults() throws Exception {
        SimilarityMatrix.generateMatrix(setFromElements(a, b), singleton(aToA));
    }

    @Test(expected = InternalAlgorithmError.class)
    public void TestGenerateMatrixSubmissionNotInGiven() throws Exception {
        SimilarityMatrix.generateMatrix(setFromElements(a, b), singleton(aToC));
    }

    @Test(expected = InternalAlgorithmError.class)
    public void TestGenerateMatrixOtherSubmissionNotGiven() throws Exception {
        SimilarityMatrix.generateMatrix(setFromElements(a, b), singleton(cToA));
    }

    @Test
    public void TestGenerate2D2ElementMatrix() throws Exception {
        assertEquals(baseMatrixTwoSubmission, SimilarityMatrix.generateMatrix(setFromElements(a, b), singleton(aToB)));
    }

    @Test
    public void TestGenerateMatrixArchiveNullSubmissions() throws Exception {
        expectedEx.expect(NullPointerException.class);

        SimilarityMatrix.generateMatrix(null, singleton(c), setFromElements(aToB, aToC, bToC));
    }

    @Test
    public void TestGenerateMatrixArchiveNullArchive() throws Exception {
        expectedEx.expect(NullPointerException.class);

        SimilarityMatrix.generateMatrix(setFromElements(a, b), null, setFromElements(aToB, aToC, bToC));
    }

    @Test
    public void TestGenerateMatrixArchiveNullResults() throws Exception {
        expectedEx.expect(NullPointerException.class);

        SimilarityMatrix.generateMatrix(setFromElements(a, b), singleton(c), null);
    }

    @Test
    public void TestGenerateMatrixArchiveEmptySubmissions() throws Exception {
        expectedEx.expect(IllegalArgumentException.class);

        SimilarityMatrix.generateMatrix(new HashSet<>(), singleton(c), setFromElements(aToB, aToC, bToC));
    }

    @Test
    public void TestGenerateMatrixArchiveEmptyResults() throws Exception {
        expectedEx.expect(IllegalArgumentException.class);

        SimilarityMatrix.generateMatrix(setFromElements(a, b), singleton(c), new HashSet<>());
    }

    @Test
    public void TestGenerateMatrixArchiveEmptyArchive() throws Exception {
        assertEquals(baseMatrixTwoSubmission, SimilarityMatrix.generateMatrix(setFromElements(a, b), new HashSet<>(), singleton(aToB)));
    }

    @Test(expected = InternalAlgorithmError.class)
    public void TestGenerateMatrixArchiveSubmissionInResultsNotNotInput() throws Exception {
        SimilarityMatrix.generateMatrix(setFromElements(a, b), singleton(c), setFromElements(aToB, aToC, bToC, cToD));
    }

    @Test(expected = InternalAlgorithmError.class)
    public void TestGenerateMatrixArchiveSubmissionPairMissing() throws Exception {
        SimilarityMatrix.generateMatrix(setFromElements(a, b), singleton(c), setFromElements(aToB, aToC));
    }

    @Test
    public void TestGenerateMatrixArchiveSubmission() throws Exception {
        assertEquals(archiveMatrixTwoSubmissionOneArchive, SimilarityMatrix.generateMatrix(setFromElements(a, b), singleton(c), setFromElements(aToB, aToC, bToC)));
    }
}
