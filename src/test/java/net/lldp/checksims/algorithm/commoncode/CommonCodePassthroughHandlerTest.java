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

package net.lldp.checksims.algorithm.commoncode;

import net.lldp.checksims.submission.Submission;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static net.lldp.checksims.testutil.SubmissionUtils.charSubmissionFromString;
import static net.lldp.checksims.testutil.SubmissionUtils.checkSubmissionCollections;
import static net.lldp.checksims.testutil.SubmissionUtils.setFromElements;
import static java.util.Collections.*;

/**
 * Tests for the Common Code passthrough handler
 */
public class CommonCodePassthroughHandlerTest {
    private CommonCodeHandler passthrough;
    private Set<Submission> empty;
    private Set<Submission> oneSubmission;
    private Set<Submission> twoSubmissions;
    private Set<Submission> threeSubmissions;

    @Before
    public void setUp() {
        passthrough = CommonCodePassthroughHandler.getInstance();

        Submission a = charSubmissionFromString("A", "A");
        Submission b = charSubmissionFromString("B", "B");
        Submission c = charSubmissionFromString("C", "C");

        empty = new HashSet<>();

        oneSubmission = singleton(a);

        twoSubmissions = setFromElements(a, b);

        threeSubmissions = setFromElements(a, b, c);
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
