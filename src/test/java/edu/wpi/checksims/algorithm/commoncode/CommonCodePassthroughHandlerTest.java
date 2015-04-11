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

package edu.wpi.checksims.algorithm.commoncode;

import edu.wpi.checksims.submission.Submission;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import static edu.wpi.checksims.testutil.SubmissionUtils.charSubmissionFromString;
import static edu.wpi.checksims.testutil.SubmissionUtils.checkSubmissionCollections;

/**
 * Tests for the Common Code passthrough handler
 */
public class CommonCodePassthroughHandlerTest {
    private CommonCodeHandler passthrough;
    private Collection<Submission> empty;
    private Collection<Submission> oneSubmission;
    private Collection<Submission> twoSubmissions;
    private Collection<Submission> threeSubmissions;

    @Before
    public void setUp() {
        passthrough = CommonCodePassthroughHandler.getInstance();

        Submission a = charSubmissionFromString("A", "A");
        Submission b = charSubmissionFromString("B", "B");
        Submission c = charSubmissionFromString("C", "C");

        empty = new LinkedList<>();

        oneSubmission = Arrays.asList(a);

        twoSubmissions = Arrays.asList(a, b);

        threeSubmissions = Arrays.asList(a, b, c);
    }

    @Test
    public void TestCommonCodePassthroughHandlerEmpty() {
        Collection<Submission> result = passthrough.handleCommonCode(empty);

        checkSubmissionCollections(result, empty);
    }

    @Test
    public void TestCommonCodePassthroughSingleSubmission() {
        Collection<Submission> result = passthrough.handleCommonCode(oneSubmission);

        checkSubmissionCollections(result, oneSubmission);
    }

    @Test
    public void TestCommonCodePassthroughTwoSubmissions() {
        Collection<Submission> result = passthrough.handleCommonCode(twoSubmissions);

        checkSubmissionCollections(result, twoSubmissions);
    }

    @Test
    public void TestCommonCodePassthroughThreeSubmissions() {
        Collection<Submission> result = passthrough.handleCommonCode(threeSubmissions);

        checkSubmissionCollections(result, threeSubmissions);
    }
}
