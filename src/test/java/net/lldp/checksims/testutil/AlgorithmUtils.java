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

package net.lldp.checksims.testutil;

import net.lldp.checksims.algorithm.AlgorithmResults;
import net.lldp.checksims.submission.Submission;
import net.lldp.checksims.token.TokenList;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;

import java.util.Collection;
import java.util.Set;

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
            Assert.assertEquals(results.b, b);

            assertEquals(expectedIdenticalA, results.identicalTokensA);
            assertEquals(expectedIdenticalB, results.identicalTokensB);

            Assert.assertEquals(expectedA, results.finalListA);
            Assert.assertEquals(expectedB, results.finalListB);
        } else {
            Assert.assertEquals(results.b, a);
            Assert.assertEquals(results.a, b);

            assertEquals(expectedIdenticalB, results.identicalTokensA);
            assertEquals(expectedIdenticalA, results.identicalTokensB);

            Assert.assertEquals(expectedB, results.finalListA);
            Assert.assertEquals(expectedA, results.finalListB);
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
            Assert.assertEquals(results.b, b);

            Assert.assertEquals(a.getContentAsTokens(), results.finalListA);
            Assert.assertEquals(b.getContentAsTokens(), results.finalListB);
        } else {
            Assert.assertEquals(results.b, a);
            Assert.assertEquals(results.a, b);

            Assert.assertEquals(b.getContentAsTokens(), results.finalListA);
            Assert.assertEquals(a.getContentAsTokens(), results.finalListB);
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

        Assert.assertEquals(a, results.a);
        Assert.assertEquals(a, results.b);

        assertEquals(a.getNumTokens(), results.identicalTokensA);
        assertEquals(a.getNumTokens(), results.identicalTokensB);

        TokenList expected = TokenList.cloneTokenList(a.getContentAsTokens());
        expected.stream().forEach((token) -> token.setValid(false));

        Assert.assertEquals(expected, results.finalListA);
        Assert.assertEquals(expected, results.finalListB);
    }

    /**
     * Ensure that every pair in a set of pairs is representing in algorithm results
     *
     * @param results Collection of algorithm results to check in
     * @param pairs Pairs of submissions to search for in the results
     */
    public static void checkResultsContainsPairs(Collection<AlgorithmResults> results, Set<Pair<Submission, Submission>> pairs) {
        assertNotNull(results);
        assertNotNull(pairs);

        assertEquals(results.size(), pairs.size());

        for(Pair<Submission, Submission> pair : pairs) {
            long numWithResult = results.stream().filter((result) -> {
                return (result.a.equals(pair.getLeft()) && result.b.equals(pair.getRight())) ||
                        (result.a.equals(pair.getRight()) && result.b.equals(pair.getLeft()));
            }).count();

            assertEquals(1, numWithResult);
        }
    }
}
