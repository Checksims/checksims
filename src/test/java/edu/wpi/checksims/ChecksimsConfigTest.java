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

package edu.wpi.checksims;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * Tests of validity checking on ChecksimsConfig
 *
 * TODO tests for all the getters
 */
public class ChecksimsConfigTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void TestSetAlgorithmToNull() {
        expectedEx.expect(NullPointerException.class);

        ChecksimsConfig config = new ChecksimsConfig();
        config.setAlgorithm(null);
    }

    @Test
    public void TestSetTokenizationNull() {
        expectedEx.expect(NullPointerException.class);

        ChecksimsConfig config = new ChecksimsConfig();
        config.setTokenization(null);
    }

    @Test
    public void TestSetPreprocessorsNull() {
        expectedEx.expect(NullPointerException.class);

        ChecksimsConfig config = new ChecksimsConfig();
        config.setPreprocessors(null);
    }

    @Test
    public void TestSetSubmissionsNull() {
        expectedEx.expect(NullPointerException.class);

        ChecksimsConfig config = new ChecksimsConfig();
        config.setSubmissions(null);
    }

    @Test
    public void TestSetSubmissionsEmpty() {
        expectedEx.expect(IllegalArgumentException.class);

        ChecksimsConfig config = new ChecksimsConfig();
        config.setSubmissions(new HashSet<>());
    }

    @Test
    public void TestSetCommonCodeHandlerNull() {
        expectedEx.expect(NullPointerException.class);

        ChecksimsConfig config = new ChecksimsConfig();
        config.setCommonCodeHandler(null);
    }

    @Test
    public void TestSetOutputPrintersNull() {
        expectedEx.expect(NullPointerException.class);

        ChecksimsConfig config = new ChecksimsConfig();
        config.setOutputPrinters(null);
    }

    @Test
    public void TestSetOutputPrintersEmpty() {
        expectedEx.expect(IllegalArgumentException.class);

        ChecksimsConfig config = new ChecksimsConfig();
        config.setOutputPrinters(new LinkedList<>());
    }

    @Test
    public void TestSetOutputMethodNull() {
        expectedEx.expect(NullPointerException.class);

        ChecksimsConfig config = new ChecksimsConfig();
        config.setOutputMethod(null);
    }

    @Test
    public void TestSetThreadsNegative() {
        expectedEx.expect(IllegalArgumentException.class);

        ChecksimsConfig config = new ChecksimsConfig();
        config.setNumThreads(-1);
    }

    @Test
    public void TestSetThreadsZero() {
        expectedEx.expect(IllegalArgumentException.class);

        ChecksimsConfig config = new ChecksimsConfig();
        config.setNumThreads(0);
    }
}
