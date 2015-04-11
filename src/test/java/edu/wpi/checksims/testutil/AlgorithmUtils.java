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

package edu.wpi.checksims.testutil;

import edu.wpi.checksims.algorithm.AlgorithmResults;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.token.TokenList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Convenience functions for testing algorithms
 */
public class AlgorithmUtils {
    private AlgorithmUtils() {}

    /**
     * Check algorithm results to verify expected tokens were matched
     *
     * @param results Results to check
     * @param a First submission to check for
     * @param b Second submission to check for
     * @param expectedA Expected matching tokens for a
     * @param expectedB Expected matching tokens for b
     */
    public static void checkResults(AlgorithmResults results, Submission a, Submission b, TokenList expectedA, TokenList expectedB) {
        assertNotNull(results);
        assertNotNull(a);
        assertNotNull(b);
        assertNotNull(expectedA);
        assertNotNull(expectedB);

        int expectedIdenticalA = (int)expectedA.stream().filter((token) -> !token.isValid()).count();
        int expectedIdenticalB = (int)expectedB.stream().filter((token) -> !token.isValid()).count();

        if(results.a.equals(a)) {
            assertEquals(results.b, b);

            assertEquals(expectedIdenticalA, results.identicalTokensA);
            assertEquals(expectedIdenticalB, results.identicalTokensB);

            assertEquals(expectedA, results.finalListA);
            assertEquals(expectedB, results.finalListB);
        } else {
            assertEquals(results.b, a);
            assertEquals(results.a, b);

            assertEquals(expectedIdenticalB, results.identicalTokensA);
            assertEquals(expectedIdenticalA, results.identicalTokensB);

            assertEquals(expectedB, results.finalListA);
            assertEquals(expectedA, results.finalListB);
        }
    }

    /**
     * Check algorithm results to verify that two submissions did not match
     *
     * @param results Results to check
     * @param a First submission to check for
     * @param b Second submission to check for
     */
    public static void checkResultsNoMatch(AlgorithmResults results, Submission a, Submission b) {
        assertNotNull(results);
        assertNotNull(a);
        assertNotNull(b);

        assertEquals(0, results.identicalTokensA);
        assertEquals(0, results.identicalTokensB);

        if(results.a.equals(a)) {
            assertEquals(results.b, b);

            assertEquals(a.getContentAsTokens(), results.finalListA);
            assertEquals(b.getContentAsTokens(), results.finalListB);
        } else {
            assertEquals(results.b, a);
            assertEquals(results.a, b);

            assertEquals(b.getContentAsTokens(), results.finalListA);
            assertEquals(a.getContentAsTokens(), results.finalListB);
        }
    }

    /**
     * Check algorithm results if two identical submissions are given
     *
     * We are expecting that they are 100% similar
     *
     * @param results Results to check
     * @param a Submission used in results
     */
    public static void checkResultsIdenticalSubmissions(AlgorithmResults results, Submission a) {
        assertNotNull(results);
        assertNotNull(a);

        assertEquals(a, results.a);
        assertEquals(a, results.b);

        assertEquals(a.getNumTokens(), results.identicalTokensA);
        assertEquals(a.getNumTokens(), results.identicalTokensB);

        TokenList expected = TokenList.cloneTokenList(a.getContentAsTokens());
        expected.stream().forEach((token) -> token.setValid(false));

        assertEquals(expected, results.finalListA);
        assertEquals(expected, results.finalListB);
    }
}
