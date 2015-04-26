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

package edu.wpi.checksims.algorithm.preprocessor;

import edu.wpi.checksims.submission.ConcreteSubmission;
import edu.wpi.checksims.submission.Submission;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static edu.wpi.checksims.testutil.SubmissionUtils.checkSubmissionCollections;
import static edu.wpi.checksims.testutil.SubmissionUtils.lineSubmissionFromString;
import static edu.wpi.checksims.testutil.SubmissionUtils.setFromElements;
import static org.junit.Assert.*;

/**
 * Tests for PreprocessSubmissions class
 */
public class PreprocessSubmissionsTest {
    private Submission a;
    private Submission b;
    private Set<Submission> empty;
    private Set<Submission> oneSubmission;
    private Set<Submission> twoSubmissions;
    private SubmissionPreprocessor identity;
    private SubmissionPreprocessor renamer;

    @Before
    public void setUp() {
        a = lineSubmissionFromString("Submission A", "A");
        b = lineSubmissionFromString("Submission B", "B");

        empty = new HashSet<>();
        oneSubmission = setFromElements(a);
        twoSubmissions = setFromElements(a, b);

        identity = new SubmissionPreprocessor() {
            @Override
            public Submission process(Submission submission) {
                return submission;
            }

            @Override
            public String getName() {
                return "identity";
            }
        };

        renamer = new SubmissionPreprocessor() {
            @Override
            public Submission process(Submission submission) {
                return new ConcreteSubmission("renamed " + submission.getName(), submission.getContentAsString(), submission.getContentAsTokens());
            }

            @Override
            public String getName() {
                return "renamer";
            }
        };
    }

    @Test
    public void testEmptyReturnsEmpty() {
        Collection<Submission> results = PreprocessSubmissions.process(identity, empty);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testOneSubmissionIdentity() {
        Collection<Submission> results = PreprocessSubmissions.process(identity, oneSubmission);

        assertNotNull(results);
        assertEquals(results, oneSubmission);
    }

    @Test
    public void testOneSubmissionRename() {
        Collection<Submission> results = PreprocessSubmissions.process(renamer, oneSubmission);
        Submission expected = lineSubmissionFromString("renamed " + a.getName(), a.getContentAsString());

        assertNotNull(results);
        assertTrue(results.contains(expected));
    }

    @Test
    public void testTwoSubmissionIdentity() {
        Collection<Submission> results = PreprocessSubmissions.process(identity, twoSubmissions);

        checkSubmissionCollections(twoSubmissions, results);
    }

    @Test
    public void testTwoSubmissionRename() {
        Collection<Submission> results = PreprocessSubmissions.process(renamer, twoSubmissions);
        Submission expectedA = lineSubmissionFromString("renamed " + a.getName(), a.getContentAsString());
        Submission expectedB = lineSubmissionFromString("renamed " + b.getName(), b.getContentAsString());
        List<Submission> expected = Arrays.asList(expectedA, expectedB);

        checkSubmissionCollections(expected, results);
    }
}
