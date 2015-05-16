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

package net.lldp.checksims.algorithm.linesimilarity;

import net.lldp.checksims.ChecksimsException;
import net.lldp.checksims.algorithm.AlgorithmResults;
import net.lldp.checksims.algorithm.SimilarityDetector;
import net.lldp.checksims.submission.ConcreteSubmission;
import net.lldp.checksims.submission.Submission;
import net.lldp.checksims.token.TokenList;
import net.lldp.checksims.token.TokenType;
import org.junit.Before;
import org.junit.Test;

import static net.lldp.checksims.testutil.AlgorithmUtils.checkResults;
import static net.lldp.checksims.testutil.AlgorithmUtils.checkResultsIdenticalSubmissions;
import static net.lldp.checksims.testutil.AlgorithmUtils.checkResultsNoMatch;
import static net.lldp.checksims.testutil.SubmissionUtils.lineSubmissionFromString;

/**
 * Tests for the Line Comparison algorithm
 */
public class LineSimilarityCheckerTest {
    private Submission empty;
    private Submission abc;
    private Submission aabc;
    private Submission abcde;
    private Submission def;
    private SimilarityDetector lineCompare;

    @Before
    public void setUp() throws Exception {
        empty = lineSubmissionFromString("Empty", "");
        abc = lineSubmissionFromString("ABC", "A\nB\nC\n");
        aabc = lineSubmissionFromString("AABC", "A\nA\nB\nC\n");
        abcde = lineSubmissionFromString("ABCDE", "A\nB\nC\nD\nE\n");
        def = lineSubmissionFromString("DEF", "D\nE\nF\n");

        lineCompare = LineSimilarityChecker.getInstance();
    }

    @Test(expected = ChecksimsException.class)
    public void TestErrorOnTokenTypeMismatch() throws ChecksimsException {
        lineCompare.detectSimilarity(empty, new ConcreteSubmission("Error", "", new TokenList(TokenType.CHARACTER)));
    }

    @Test
    public void TestEmptySubmissionIsZeroPercentSimilar() throws ChecksimsException {
        AlgorithmResults results = lineCompare.detectSimilarity(empty, empty);

        checkResultsIdenticalSubmissions(results, empty);
    }

    @Test
    public void TestEmptySubmissionAndNonemptySubmission() throws ChecksimsException {
        AlgorithmResults results = lineCompare.detectSimilarity(empty, abc);

        checkResultsNoMatch(results, empty, abc);
    }

    @Test
    public void TestIdenticalSubmissions() throws Exception {
        AlgorithmResults results = lineCompare.detectSimilarity(abc, abc);

        checkResultsIdenticalSubmissions(results, abc);
    }

    @Test
    public void TestSubmissionStrictSubset() throws ChecksimsException {
        AlgorithmResults results = lineCompare.detectSimilarity(abc, abcde);

        TokenList expectedAbc = TokenList.cloneTokenList(abc.getContentAsTokens());
        expectedAbc.stream().forEach((token) -> token.setValid(false));
        TokenList expectedAbcde = TokenList.cloneTokenList(abcde.getContentAsTokens());
        for(int i = 0; i <= 2; i++) {
            expectedAbcde.get(i).setValid(false);
        }

        checkResults(results, abc, abcde, expectedAbc, expectedAbcde);
    }

    @Test
    public void TestSubmissionsNoOverlap() throws ChecksimsException {
        AlgorithmResults results = lineCompare.detectSimilarity(abc, def);

        checkResultsNoMatch(results, abc, def);
    }

    @Test
    public void TestSubmissionsSomeOverlap() throws ChecksimsException {
        AlgorithmResults results = lineCompare.detectSimilarity(abcde, def);

        TokenList expectedAbcde = TokenList.cloneTokenList(abcde.getContentAsTokens());
        expectedAbcde.get(3).setValid(false);
        expectedAbcde.get(4).setValid(false);

        TokenList expectedDef = TokenList.cloneTokenList(def.getContentAsTokens());
        expectedDef.get(0).setValid(false);
        expectedDef.get(1).setValid(false);

        checkResults(results, abcde, def, expectedAbcde, expectedDef);
    }

    @Test
    public void TestSubmissionsDuplicatedToken() throws ChecksimsException {
        AlgorithmResults results = lineCompare.detectSimilarity(aabc, abc);

        TokenList expectedAbc = TokenList.cloneTokenList(abc.getContentAsTokens());
        expectedAbc.stream().forEach((token) -> token.setValid(false));

        TokenList expectedAabc = TokenList.cloneTokenList(aabc.getContentAsTokens());
        expectedAabc.stream().forEach((token) -> token.setValid(false));

        checkResults(results, aabc, abc, expectedAabc, expectedAbc);
    }

    @Test
    public void TestSubmissionDuplicatedTokenNotInOtherSubmission() throws ChecksimsException {
        AlgorithmResults results = lineCompare.detectSimilarity(aabc, def);

        checkResultsNoMatch(results, aabc, def);
    }
}
