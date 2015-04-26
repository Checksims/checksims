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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static edu.wpi.checksims.testutil.SubmissionUtils.charSubmissionFromString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Tests for MatrixEntry
 */
public class MatrixEntryTest {
    private Submission a;
    private Submission b;
    private Submission ab;
    private Submission empty;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() {
        a = charSubmissionFromString("A", "A");
        b = charSubmissionFromString("B", "B");
        ab = charSubmissionFromString("AB", "AB");
        empty = charSubmissionFromString("Empty", "");
    }

    @Test
    public void TestMatrixEntryNullFirstSubmission() {
        expectedEx.expect(NullPointerException.class);

        new MatrixEntry(null, a, 0);
    }

    @Test
    public void TestMatrixEntryNullSecondSubmission() {
        expectedEx.expect(NullPointerException.class);

        new MatrixEntry(a, null, 0);
    }

    @Test
    public void TestMatrixEntryNegativeNumberOfTokens() {
        expectedEx.expect(IllegalArgumentException.class);

        new MatrixEntry(a, b, -1);
    }

    @Test
    public void TestMatrixEntrySimilarTokensGreaterThanNumberOfTokensInBase() {
        expectedEx.expect(IllegalArgumentException.class);

        new MatrixEntry(a, b, 10);
    }

    @Test
    public void TestPercentageGeneration() {
        MatrixEntry test = new MatrixEntry(ab, a, 1);

        assertEquals(0.5, test.getSimilarityPercent(), 0.0);
    }

    @Test
    public void TestPercentageGenerateEmptyBase() {
        MatrixEntry test = new MatrixEntry(empty, a, 0);

        assertEquals(0.0, test.getSimilarityPercent(), 0.0);
    }

    @Test
    public void TestBasicEquality() {
        MatrixEntry test1 = new MatrixEntry(a, b, 0);
        MatrixEntry test2 = new MatrixEntry(a, b, 0);

        assertEquals(test1, test2);
    }

    @Test
    public void TestInequality() {
        MatrixEntry test1 = new MatrixEntry(a, b, 0);
        MatrixEntry test2 = new MatrixEntry(ab, a, 1);

        assertNotEquals(test1, test2);
    }

    @Test
    public void TestGetTotalTokens() {
        MatrixEntry test1 = new MatrixEntry(a, b, 0);
        MatrixEntry test2 = new MatrixEntry(ab, a, 1);

        assertEquals(1, test1.getTotalTokens());
        assertEquals(2, test2.getTotalTokens());
    }
}
