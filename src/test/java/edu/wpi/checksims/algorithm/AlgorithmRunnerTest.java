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
import edu.wpi.checksims.testutil.AlgorithmUtils;
import edu.wpi.checksims.token.TokenType;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static edu.wpi.checksims.testutil.SubmissionUtils.charSubmissionFromString;
import static edu.wpi.checksims.testutil.SubmissionUtils.setFromElements;
import static java.util.Collections.singleton;

/**
 * Tests for the AlgorithmRunner class
 */
public class AlgorithmRunnerTest {
    private Submission a;
    private Submission b;
    private Submission c;
    private Submission d;

    private SimilarityDetector detectNothing;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() {
        detectNothing = new SimilarityDetector() {
            @Override
            public TokenType getDefaultTokenType() {
                return TokenType.CHARACTER;
            }

            @Override
            public AlgorithmResults detectSimilarity(Submission a, Submission b) {
                return new AlgorithmResults(a, b, a.getContentAsTokens(), b.getContentAsTokens());
            }

            @Override
            public String getName() {
                return "nothing";
            }
        };

        a = charSubmissionFromString("A", "A");
        b = charSubmissionFromString("B", "B");
        c = charSubmissionFromString("C", "C");
        d = charSubmissionFromString("D", "D");
    }

    @Test
    public void TestRunAlgorithmNull() {
        expectedEx.expect(NullPointerException.class);

        AlgorithmRunner.runAlgorithm(null, detectNothing);
    }

    @Test
    public void TestRunAlgorithmNullAlgorithm() {
        expectedEx.expect(NullPointerException.class);

        AlgorithmRunner.runAlgorithm(singleton(Pair.of(a, b)), null);
    }

    @Test
    public void TestRunAlgorithmEmptySet() {
        expectedEx.expect(IllegalArgumentException.class);

        AlgorithmRunner.runAlgorithm(new HashSet<>(), null);
    }

    @Test
    public void TestRunAlgorithmSinglePair() {
        Set<Pair<Submission, Submission>> submissions = singleton(Pair.of(a, b));
        Collection<AlgorithmResults> results = AlgorithmRunner.runAlgorithm(submissions, detectNothing);

        AlgorithmUtils.checkResultsContainsPairs(results, submissions);
    }

    @Test
    public void TestRunAlgorithmTwoPairs() {
        Set<Pair<Submission, Submission>> submissions = setFromElements(Pair.of(a, b), Pair.of(a, c));
        Collection<AlgorithmResults> results = AlgorithmRunner.runAlgorithm(submissions, detectNothing);

        AlgorithmUtils.checkResultsContainsPairs(results, submissions);
    }

    @Test
    public void TestRunAlgorithmThreePairs() {
        Set<Pair<Submission, Submission>> submissions = setFromElements(Pair.of(a, b), Pair.of(a, c), Pair.of(b, c));
        Collection<AlgorithmResults> results = AlgorithmRunner.runAlgorithm(submissions, detectNothing);

        AlgorithmUtils.checkResultsContainsPairs(results, submissions);
    }

    @Test
    public void TestRunAlgorithmAllPossiblePairs() {
        Set<Pair<Submission, Submission>> submissions = setFromElements(Pair.of(a, b), Pair.of(a, c), Pair.of(a, d), Pair.of(b, c), Pair.of(b, d), Pair.of(c, d));
        Collection<AlgorithmResults> results = AlgorithmRunner.runAlgorithm(submissions, detectNothing);

        AlgorithmUtils.checkResultsContainsPairs(results, submissions);
    }
}
