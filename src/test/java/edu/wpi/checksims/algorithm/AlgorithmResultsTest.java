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
import edu.wpi.checksims.token.TokenList;
import edu.wpi.checksims.token.TokenType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static edu.wpi.checksims.testutil.SubmissionUtils.charSubmissionFromString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Tests for Algorithm Results
 */
public class AlgorithmResultsTest {
    private Submission a;
    private Submission b;
    private Submission abcd;
    private Submission empty;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() {
        a = charSubmissionFromString("A", "A");
        b = charSubmissionFromString("B", "B");
        abcd = charSubmissionFromString("ABCD", "ABCD");
        empty = charSubmissionFromString("Empty", "");
    }

    @Test
    public void TestCreateAlgorithmResultsNullA() {
        expectedEx.expect(NullPointerException.class);

        new AlgorithmResults(null, b, a.getContentAsTokens(), b.getContentAsTokens());
    }

    @Test
    public void TestCreateAlgorithmResultsNullB() {
        expectedEx.expect(NullPointerException.class);

        new AlgorithmResults(a, null, a.getContentAsTokens(), b.getContentAsTokens());
    }

    @Test
    public void TestCreateAlgorithmResultsNullFinalA() {
        expectedEx.expect(NullPointerException.class);

        new AlgorithmResults(a, b, null, b.getContentAsTokens());
    }

    @Test
    public void TestCreateAlgorithmResultsNullFinalB() {
        expectedEx.expect(NullPointerException.class);

        new AlgorithmResults(a, b, a.getContentAsTokens(), null);
    }

    @Test
    public void TestCreateAlgorithmResultsSizeMismatchA() {
        expectedEx.expect(IllegalArgumentException.class);

        new AlgorithmResults(a, b, new TokenList(TokenType.CHARACTER), b.getContentAsTokens());
    }

    @Test
    public void TestCreateAlgorithmResultsSizeMismatchB() {
        expectedEx.expect(IllegalArgumentException.class);

        new AlgorithmResults(a, b, a.getContentAsTokens(), new TokenList(TokenType.CHARACTER));
    }

    @Test
    public void TestAlgorithmResultsGetPercentSimilarA() {
        AlgorithmResults test1 = new AlgorithmResults(a, b, a.getContentAsTokens(), b.getContentAsTokens());

        assertEquals(0, test1.identicalTokensA);
        assertEquals(0.0, test1.percentMatchedA(), 0.0);
    }

    @Test
    public void TestAlgorithmResultsGetPercentSimilarB() {
        AlgorithmResults test1 = new AlgorithmResults(a, b, a.getContentAsTokens(), b.getContentAsTokens());

        assertEquals(0, test1.identicalTokensB);
        assertEquals(0.0, test1.percentMatchedB(), 0.0);
    }

    @Test
    public void TestAlgorithmResultsPercentSimilarAEmpty() {
        AlgorithmResults test = new AlgorithmResults(empty, b, empty.getContentAsTokens(), b.getContentAsTokens());

        assertEquals(0.0, test.percentMatchedA(), 0.0);
    }

    @Test
    public void TestAlgorithmResultsGetPercentSimilarBEmpty() {
        AlgorithmResults test = new AlgorithmResults(a, empty, a.getContentAsTokens(), empty.getContentAsTokens());

        assertEquals(0.0, test.percentMatchedB(), 0.0);
    }

    @Test
    public void TestAlgorithmResultsGetPercentSimilarANonzero() {
        TokenList one = TokenList.cloneTokenList(abcd.getContentAsTokens());
        one.get(0).setValid(false);
        TokenList two = TokenList.cloneTokenList(abcd.getContentAsTokens());
        for(int i = 0; i < 2; i++) {
            two.get(i).setValid(false);
        }

        AlgorithmResults testOne = new AlgorithmResults(abcd, b, one, b.getContentAsTokens());
        AlgorithmResults testTwo = new AlgorithmResults(abcd, b, two, b.getContentAsTokens());

        assertEquals(1, testOne.identicalTokensA);
        assertEquals(2, testTwo.identicalTokensA);
        assertEquals(0.25, testOne.percentMatchedA(), 0.0);
        assertEquals(0.50, testTwo.percentMatchedA(), 0.0);
    }

    @Test
    public void TestAlgorithmResultsGetPercentSimilarBNonzero() {
        TokenList one = TokenList.cloneTokenList(abcd.getContentAsTokens());
        one.get(0).setValid(false);
        TokenList two = TokenList.cloneTokenList(abcd.getContentAsTokens());
        for(int i = 0; i < 2; i++) {
            two.get(i).setValid(false);
        }

        AlgorithmResults testOne = new AlgorithmResults(a, abcd, a.getContentAsTokens(), one);
        AlgorithmResults testTwo = new AlgorithmResults(a, abcd, a.getContentAsTokens(), two);

        assertEquals(1, testOne.identicalTokensB);
        assertEquals(2, testTwo.identicalTokensB);
        assertEquals(0.25, testOne.percentMatchedB(), 0.0);
        assertEquals(0.50, testTwo.percentMatchedB(), 0.0);
    }

    @Test
    public void TestBasicEquality() {
        AlgorithmResults one = new AlgorithmResults(a, b, a.getContentAsTokens(), b.getContentAsTokens());
        AlgorithmResults two = new AlgorithmResults(a, b, a.getContentAsTokens(), b.getContentAsTokens());

        assertEquals(one, two);
    }

    @Test
    public void TestBasicInequality() {
        AlgorithmResults one = new AlgorithmResults(a, b, a.getContentAsTokens(), b.getContentAsTokens());
        AlgorithmResults two = new AlgorithmResults(a, abcd, a.getContentAsTokens(), abcd.getContentAsTokens());

        assertNotEquals(one, two);
    }
}
