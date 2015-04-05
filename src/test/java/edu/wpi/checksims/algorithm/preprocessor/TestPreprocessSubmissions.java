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
 * Copyright (c) 2014 Matthew Heon and Dolan Murvihill
 */

package edu.wpi.checksims.algorithm.preprocessor;

import edu.wpi.checksims.submission.ConcreteSubmission;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.token.ConcreteToken;
import edu.wpi.checksims.token.TokenList;
import edu.wpi.checksims.token.TokenType;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for PreprocessSubmissions class
 */
public class TestPreprocessSubmissions {
    private static List<Submission> empty;
    private static List<Submission> oneSubmission;
    private static List<Submission> twoSubmissions;
    private static SubmissionPreprocessor identity;
    private static SubmissionPreprocessor renamer;

    @BeforeClass
    public static void setUp() {
        TokenList tokensA = new TokenList(TokenType.LINE);
        tokensA.add(new ConcreteToken("Submission A", TokenType.LINE));
        Submission a = new ConcreteSubmission("A", "", tokensA);

        TokenList tokensB = new TokenList(TokenType.LINE);
        tokensB.add(new ConcreteToken("Submission B", TokenType.LINE));
        Submission b = new ConcreteSubmission("B", "", tokensB);

        empty = new LinkedList<>();

        oneSubmission = new LinkedList<>();
        oneSubmission.add(a);

        twoSubmissions = new LinkedList<>();
        twoSubmissions.add(a);
        twoSubmissions.add(b);

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
        assertEquals(results.size(), 1);
        assertEquals(results, oneSubmission);
        assertTrue(results.contains(oneSubmission.get(0)));
    }

    @Test
    public void testOneSubmissionRename() {
        Collection<Submission> results = PreprocessSubmissions.process(renamer, oneSubmission);

        Submission expected = new ConcreteSubmission("renamed " + oneSubmission.get(0).getName(), oneSubmission.get(0).getContentAsString(), oneSubmission.get(0).getContentAsTokens());

        assertNotNull(results);
        assertEquals(results.size(), 1);
        assertTrue(results.contains(expected));
    }

    @Test
    public void testTwoSubmissionIdentity() {
        Collection<Submission> results = PreprocessSubmissions.process(identity, twoSubmissions);

        assertNotNull(results);
        assertEquals(results.size(), 2);
        assertTrue(results.contains(twoSubmissions.get(0)));
        assertTrue(results.contains(twoSubmissions.get(1)));
    }

    @Test
    public void testTwoSubmissionRename() {
        Collection<Submission> results = PreprocessSubmissions.process(renamer, twoSubmissions);

        List<Submission> expected = new LinkedList<>();
        expected.add(new ConcreteSubmission("renamed " + twoSubmissions.get(0).getName(), twoSubmissions.get(0).getContentAsString(), twoSubmissions.get(0).getContentAsTokens()));
        expected.add(new ConcreteSubmission("renamed " + twoSubmissions.get(1).getName(), twoSubmissions.get(1).getContentAsString(), twoSubmissions.get(1).getContentAsTokens()));

        assertNotNull(results);
        assertEquals(results.size(), 2);
        assertTrue(results.contains(expected.get(0)));
        assertTrue(results.contains(expected.get(1)));
    }
}
