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

package net.lldp.checksims.algorithm.smithwaterman;

import net.lldp.checksims.algorithm.AlgorithmResults;
import net.lldp.checksims.submission.Submission;
import net.lldp.checksims.token.TokenList;
import net.lldp.checksims.token.TokenTypeMismatchException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static net.lldp.checksims.testutil.AlgorithmUtils.*;
import static net.lldp.checksims.testutil.SubmissionUtils.*;

/**
 * Tests for the Smith-Waterman Algorithm plagiarism detector
 */
public class SmithWatermanTest {
    private Submission empty;
    private Submission typeMismatch;
    private Submission oneToken;
    private Submission twoTokens;
    private Submission hello;
    private Submission world;
    private Submission helloWorld;
    private Submission helloWerld;
    private Submission helloLongPauseWorld;
    private Submission wrappedHelloPauseWorldIsWrapped;

    private SmithWaterman instance;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() {
        instance = SmithWaterman.getInstance();

        empty = whitespaceSubmissionFromString("Empty", "");
        typeMismatch = lineSubmissionFromString("Type Mismatch", "hello");
        oneToken = whitespaceSubmissionFromString("One Token", "hello");
        twoTokens = whitespaceSubmissionFromString("Two Tokens", "hello world");
        hello = charSubmissionFromString("Hello", "hello");
        world = charSubmissionFromString("World", "world");
        helloWorld = charSubmissionFromString("Hello World", "hello world");
        helloWerld = charSubmissionFromString("Hello Werld", "hello werld");
        helloLongPauseWorld = charSubmissionFromString("Hello World with Pause", "hello long pause world");
        wrappedHelloPauseWorldIsWrapped = charSubmissionFromString("Wrapped Hello World with Pause", "wrapped hello random world is wrapped");
    }

    // Tests for Smith-Waterman algorithm

    @Test
    public void TestNullSubmissionAThrowsException() throws Exception {
        expectedEx.expect(NullPointerException.class);

        instance.detectSimilarity(null, empty);
    }

    @Test
    public void TestNullSubmissionBThrowsException() throws Exception {
        expectedEx.expect(NullPointerException.class);

        instance.detectSimilarity(empty, null);
    }

    @Test(expected = TokenTypeMismatchException.class)
    public void TestTokenTypeMismatchThrowsException() throws Exception {
        instance.detectSimilarity(empty, typeMismatch);
    }

    @Test
    public void TestTwoEmptySubmissionsAreNotSimilar() throws Exception {
        AlgorithmResults results = instance.detectSimilarity(empty, empty);

        checkResultsIdenticalSubmissions(results, empty);
    }

    @Test
    public void TestOneEmptyOneNonEmptySubmissionsAreNotSimilar() throws Exception {
        AlgorithmResults results = instance.detectSimilarity(empty, oneToken);

        checkResultsNoMatch(results, empty, oneToken);
    }

    @Test
    public void TestIdenticalNonEmptySubmissionsAreIdentical() throws Exception {
        AlgorithmResults results = instance.detectSimilarity(oneToken, oneToken);

        checkResultsIdenticalSubmissions(results, oneToken);
    }

    @Test
    public void TestIdenticalNonEmptySubmissionsMoreThanOneTokenAreIdentical() throws Exception {
        AlgorithmResults results = instance.detectSimilarity(twoTokens, twoTokens);

        checkResultsIdenticalSubmissions(results, twoTokens);
    }

    @Test
    public void TestDifferentSubmissionsNoMatches() throws Exception {
        AlgorithmResults results = instance.detectSimilarity(hello, world);

        checkResultsNoMatch(results, hello, world);
    }

    @Test
    public void TestDifferentSubmissionsPartialOverlay() throws Exception {
        AlgorithmResults results = instance.detectSimilarity(helloWorld, hello);

        TokenList expectedHelloWorld = TokenList.cloneTokenList(helloWorld.getContentAsTokens());
        for(int i = 0; i < 5; i++) {
            expectedHelloWorld.get(i).setValid(false);
        }

        TokenList expectedHello = TokenList.cloneTokenList(hello.getContentAsTokens());
        expectedHello.stream().forEach((token) -> token.setValid(false));

        checkResults(results, helloWorld, hello, expectedHelloWorld, expectedHello);
    }

    @Test
    public void TestDifferentSubmissionsSameSizeInterruptedOverlay() throws Exception {
        AlgorithmResults results = instance.detectSimilarity(helloWorld, helloWerld);

        TokenList expectedHelloWorld = TokenList.cloneTokenList(helloWorld.getContentAsTokens());
        expectedHelloWorld.stream().forEach((token) -> token.setValid(false));
        expectedHelloWorld.get(7).setValid(true);

        TokenList expectedHelloWerld = TokenList.cloneTokenList(helloWerld.getContentAsTokens());
        expectedHelloWerld.stream().forEach((token) -> token.setValid(false));
        expectedHelloWerld.get(7).setValid(true);

        checkResults(results, helloWorld, helloWerld, expectedHelloWorld, expectedHelloWerld);
    }

    @Test
    public void TestDifferentSubmissionsTwoOverlays() throws Exception {
        AlgorithmResults results = instance.detectSimilarity(helloLongPauseWorld, helloWorld);

        TokenList expectedHelloLongPauseWorld = TokenList.cloneTokenList(helloLongPauseWorld.getContentAsTokens());
        for(int i = 0; i < 6; i++) {
            expectedHelloLongPauseWorld.get(i).setValid(false);
        }
        for(int i = 17; i < 22; i++) {
            expectedHelloLongPauseWorld.get(i).setValid(false);
        }

        TokenList expectedHelloWorld = TokenList.cloneTokenList(helloWorld.getContentAsTokens());
        expectedHelloWorld.stream().forEach((token) -> token.setValid(false));

        checkResults(results, helloLongPauseWorld, helloWorld, expectedHelloLongPauseWorld, expectedHelloWorld);
    }

    @Test
    public void TestDifferentSubmissionsTwoOverlaysWrapped() throws Exception {
        AlgorithmResults results = instance.detectSimilarity(helloLongPauseWorld, wrappedHelloPauseWorldIsWrapped);

        TokenList expectedHelloLongPauseWorld = TokenList.cloneTokenList(helloLongPauseWorld.getContentAsTokens());
        for(int i = 0; i < 6; i++) {
            expectedHelloLongPauseWorld.get(i).setValid(false);
        }
        for(int i = 16; i < 22; i++) {
            expectedHelloLongPauseWorld.get(i).setValid(false);
        }

        TokenList expectedWrappedHelloPauseWorldIsWrapped = TokenList.cloneTokenList(wrappedHelloPauseWorldIsWrapped.getContentAsTokens());
        for(int i = 8; i < 14; i++) {
            expectedWrappedHelloPauseWorldIsWrapped.get(i).setValid(false);
        }
        for(int i = 20; i < 26; i++) {
            expectedWrappedHelloPauseWorldIsWrapped.get(i).setValid(false);
        }

        checkResults(results, helloLongPauseWorld, wrappedHelloPauseWorldIsWrapped, expectedHelloLongPauseWorld, expectedWrappedHelloPauseWorldIsWrapped);
    }
}
