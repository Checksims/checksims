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

package edu.wpi.checksims.submission;

import org.junit.Before;
import org.junit.Test;

import static edu.wpi.checksims.testutil.SubmissionUtils.charSubmissionFromString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Tests for Submissions
 */
public class SubmissionTest {
    private Submission a;
    private Submission aTwo;
    private Submission aInval;
    private Submission abc;

    @Before
    public void setUp() {
        a = charSubmissionFromString("a", "a");
        aTwo = charSubmissionFromString("a", "a");
        aInval = charSubmissionFromString("a", "a");
        aInval.getContentAsTokens().get(0).setValid(false);
        abc = charSubmissionFromString("abc", "abc");
    }

    @Test
    public void TestSubmissionEquality() {
        assertEquals(a, aTwo);
    }

    @Test
    public void TestBasicSubmissionOperations() {
        assertEquals(a.getContentAsString(), a.getContentAsTokens().join(false));
        assertEquals(a.getNumTokens(), a.getContentAsTokens().size());
        assertEquals(a.getTokenType(), a.getContentAsTokens().type);
        assertEquals("a", a.getName());

        assertEquals(3, abc.getNumTokens());
    }

    @Test
    public void TestSubmissionEqualityIsValiditySensitive() {
        assertNotEquals(a, aInval);
    }

    @Test
    public void TestValidityIgnoringSubmissionEquality() {
        Submission aIgnoring = new ValidityIgnoringSubmission(a);

        assertEquals(aIgnoring, aInval);
        assertEquals(aIgnoring, a);
    }

    @Test
    public void TestValidityEnforcingSubmissionEquality() {
        Submission aEnforcing = new ValidityEnsuringSubmission(a);
        Submission aInvalEnforcing = new ValidityEnsuringSubmission(aInval);

        assertEquals(aEnforcing, a);
        assertNotEquals(aEnforcing, aInval);
        assertNotEquals(aInvalEnforcing, a);
        assertNotEquals(aInvalEnforcing, aInval);
    }
}
