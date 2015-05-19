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

package net.lldp.checksims.algorithm.preprocessor;

import net.lldp.checksims.submission.Submission;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static net.lldp.checksims.testutil.SubmissionUtils.*;

/**
 * Tests for the Common Code Line Removal preprocessor
 */
public class CommonCodeLineRemovalPreprocessorTest {
    private Submission empty;
    private Submission abc;
    private Submission abcde;
    private Submission def;

    @Before
    public void setUp() throws Exception {
        empty = charSubmissionFromString("Empty", "");
        abc = charSubmissionFromString("ABC", "A\nB\nC\n");
        abcde = charSubmissionFromString("ABCDE", "A\nB\nC\nD\nE\n");
        def = charSubmissionFromString("DEF", "D\nE\nF\n");
    }

    @Test
    public void TestRemoveCommonCodeFromEmpty() throws Exception {
        SubmissionPreprocessor handler = new CommonCodeLineRemovalPreprocessor(abc);
        Set<Submission> removeFrom = singleton(empty);

        Collection<Submission> result = PreprocessSubmissions.process(handler, removeFrom);

        checkSubmissionCollections(result, removeFrom);
    }

    @Test
    public void TestRemoveIdenticalCommonCodeReturnsEmpty() throws Exception {
        SubmissionPreprocessor handler = new CommonCodeLineRemovalPreprocessor(abc);
        Set<Submission> removeFrom = singleton(abc);
        Submission expected = charSubmissionFromString(abc.getName(), empty.getContentAsString());

        Collection<Submission> results = PreprocessSubmissions.process(handler, removeFrom);

        checkSubmissionCollections(results, singletonList(expected));
    }

    @Test
    public void TestRemoveCommonCodeNoOverlapReturnsIdentical() throws Exception {
        SubmissionPreprocessor handler = new CommonCodeLineRemovalPreprocessor(def);
        Set<Submission> removeFrom = singleton(abc);

        Collection<Submission> results = PreprocessSubmissions.process(handler, removeFrom);

        checkSubmissionCollections(results, removeFrom);
    }

    @Test
    public void TestRemoveCommonCodePartialOverlap() throws Exception {
        SubmissionPreprocessor handler = new CommonCodeLineRemovalPreprocessor(abc);
        Set<Submission> removeFrom = singleton(abcde);
        Submission expected = charSubmissionFromString(abcde.getName(), "D\nE\n");

        Collection<Submission> results = PreprocessSubmissions.process(handler, removeFrom);

        checkSubmissionCollections(results, singletonList(expected));
    }

    @Test
    public void TestRemoveCommonCodeSubsetOfCommon() throws Exception {
        SubmissionPreprocessor handler = new CommonCodeLineRemovalPreprocessor(abcde);
        Set<Submission> removeFrom = singleton(abc);
        Submission expected = charSubmissionFromString(abc.getName(), empty.getContentAsString());

        Collection<Submission> results = PreprocessSubmissions.process(handler, removeFrom);

        checkSubmissionCollections(results, singletonList(expected));
    }

    @Test
    public void TestRemoveCommonCodeMultipleSubmissions() throws Exception {
        SubmissionPreprocessor handler = new CommonCodeLineRemovalPreprocessor(abc);
        Set<Submission> removeFrom = setFromElements(abc, abcde, def);
        Submission expected1 = charSubmissionFromString(abc.getName(), empty.getContentAsString());
        Submission expected2 = charSubmissionFromString(abcde.getName(), "D\nE\n");
        Submission expected3 = def;
        Collection<Submission> expected = Arrays.asList(expected1, expected2, expected3);

        Collection<Submission> results = PreprocessSubmissions.process(handler, removeFrom);

        checkSubmissionCollections(results, expected);
    }
}
