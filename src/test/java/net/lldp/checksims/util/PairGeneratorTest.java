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

package net.lldp.checksims.util;

import net.lldp.checksims.submission.Submission;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashSet;
import java.util.Set;

import static net.lldp.checksims.testutil.SubmissionUtils.charSubmissionFromString;
import static net.lldp.checksims.testutil.SubmissionUtils.setFromElements;
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
    private Submission e;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() {
        a = charSubmissionFromString("A", "");
        b = charSubmissionFromString("B", "");
        c = charSubmissionFromString("C", "");
        d = charSubmissionFromString("D", "");
        e = charSubmissionFromString("E", "");
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

    @Test
    public void TestGeneratePairsWithArchiveNullSubmissions() {
        expectedEx.expect(NullPointerException.class);

        PairGenerator.generatePairsWithArchive(null, new HashSet<>());
    }

    @Test
    public void TestGeneratePairsWithArchiveNullArchive() {
        expectedEx.expect(NullPointerException.class);

        PairGenerator.generatePairsWithArchive(setFromElements(a, b), null);
    }

    @Test
    public void TestGeneratePairsWithArchiveEmptyArchiveIdenticalToNormal() {
        Set<Submission> submissions1 = setFromElements(a, b);
        Set<Pair<Submission, Submission>> expected1 = PairGenerator.generatePairs(submissions1);
        Set<Pair<Submission, Submission>> results1 = PairGenerator.generatePairsWithArchive(submissions1, new HashSet<>());

        checkPairsAreInSet(results1, expected1);

        Set<Submission> submissions2 = setFromElements(a, b, c);
        Set<Pair<Submission, Submission>> expected2 = PairGenerator.generatePairs(submissions2);
        Set<Pair<Submission, Submission>> results2 = PairGenerator.generatePairsWithArchive(submissions2, new HashSet<>());

        checkPairsAreInSet(results2, expected2);

        Set<Submission> submissions3 = setFromElements(a, b, c, d);
        Set<Pair<Submission, Submission>> expected3 = PairGenerator.generatePairs(submissions3);
        Set<Pair<Submission, Submission>> results3 = PairGenerator.generatePairsWithArchive(submissions3, new HashSet<>());

        checkPairsAreInSet(results3, expected3);
    }

    @Test
    public void TestGeneratePairsWithArchiveOneElementArchive() {
        Set<Submission> submissions = setFromElements(a, b);
        Set<Submission> archive = singleton(c);

        Set<Pair<Submission, Submission>> expected = setFromElements(Pair.of(a, b), Pair.of(a, c), Pair.of(b, c));
        Set<Pair<Submission, Submission>> results = PairGenerator.generatePairsWithArchive(submissions, archive);

        checkPairsAreInSet(results, expected);
    }

    @Test
    public void TestGeneratePairsWithArchiveTwoElementArchive() {
        Set<Submission> submissions = setFromElements(a, b, c);
        Set<Submission> archive = setFromElements(d, e);

        Set<Pair<Submission, Submission>> expected = setFromElements(Pair.of(a, b), Pair.of(a, c), Pair.of(b, c), Pair.of(a, d),
                Pair.of(b, d), Pair.of(c, d), Pair.of(a, e), Pair.of(b, e), Pair.of(c, e));
        Set<Pair<Submission, Submission>> results = PairGenerator.generatePairsWithArchive(submissions, archive);

        checkPairsAreInSet(results, expected);
    }
}
