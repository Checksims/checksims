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

package edu.wpi.checksims.util;

import edu.wpi.checksims.submission.Submission;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashSet;
import java.util.Set;

import static edu.wpi.checksims.testutil.SubmissionUtils.charSubmissionFromString;
import static edu.wpi.checksims.testutil.SubmissionUtils.setFromElements;
import static java.util.Collections.singleton;
import static org.junit.Assert.*;

/**
 * Test for unordered pair generation
 */
public class PairGeneratorTest {
    private Submission a;
    private Submission b;
    private Submission c;
    private Submission d;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() {
        a = charSubmissionFromString("A", "");
        b = charSubmissionFromString("B", "");
        c = charSubmissionFromString("C", "");
        d = charSubmissionFromString("D", "");
    }

    public static void checkPairsAreInSet(Set<Pair<Submission, Submission>> toCheck, Set<Pair<Submission, Submission>> expected) {
        assertNotNull(toCheck);
        assertNotNull(expected);

        assertEquals(expected.size(), toCheck.size());

        expected.stream().forEach((pair) -> assertTrue(toCheck.contains(pair) || toCheck.contains(Pair.of(pair.getRight(), pair.getLeft()))));
    }

    @Test
    public void TestGenerateFromNullThrowsException() {
        expectedEx.expect(NullPointerException.class);

        PairGenerator.generatePairs(null);
    }

    @Test
    public void TestGenerateFromEmptySetThrowsException() {
        expectedEx.expect(IllegalArgumentException.class);

        PairGenerator.generatePairs(new HashSet<>());
    }

    @Test
    public void TestGenerateFromOneElementSetThrowsException() {
        expectedEx.expect(IllegalArgumentException.class);

        Set<Submission> submissions = singleton(a);
        PairGenerator.generatePairs(submissions);
    }

    @Test
    public void TestGenerateFromTwoElementSet() {
        Set<Submission> submissions = setFromElements(a, b);
        Set<Pair<Submission, Submission>> expected = singleton(Pair.of(a, b));
        Set<Pair<Submission, Submission>> results = PairGenerator.generatePairs(submissions);

        checkPairsAreInSet(results, expected);
    }

    @Test
    public void TestGenerateFromThreeElementSet() {
        Set<Submission> submissions = setFromElements(a, b, c);
        Set<Pair<Submission, Submission>> expected = setFromElements(Pair.of(a, b), Pair.of(a, c), Pair.of(b, c));
        Set<Pair<Submission, Submission>> results = PairGenerator.generatePairs(submissions);

        checkPairsAreInSet(results, expected);
    }

    @Test
    public void TestGenerateFromFourElementSet() {
        Set<Submission> submissions = setFromElements(a, b, c, d);
        Set<Pair<Submission, Submission>> expected = setFromElements(Pair.of(a, b), Pair.of(a, c), Pair.of(a, d), Pair.of(b, c), Pair.of(b, d), Pair.of(c, d));
        Set<Pair<Submission, Submission>> results = PairGenerator.generatePairs(submissions);

        checkPairsAreInSet(results, expected);
    }
}
