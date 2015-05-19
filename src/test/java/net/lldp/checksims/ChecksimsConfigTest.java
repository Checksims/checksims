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

package net.lldp.checksims;

import net.lldp.checksims.algorithm.preprocessor.LowercasePreprocessor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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
    public void TestSetPreprocessorsDuplicated() {
        expectedEx.expect(IllegalArgumentException.class);

        ChecksimsConfig config = new ChecksimsConfig();
        config.setPreprocessors(Arrays.asList(LowercasePreprocessor.getInstance(), LowercasePreprocessor.getInstance()));
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
    public void TestSetArchiveSubmissionsNull() {
        expectedEx.expect(NullPointerException.class);

        ChecksimsConfig config = new ChecksimsConfig();
        config.setArchiveSubmissions(null);
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
        config.setOutputPrinters(new HashSet<>());
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

    @Test
    public void TestBaseConfigEquality() {
        assertEquals(new ChecksimsConfig(), new ChecksimsConfig());
    }

    @Test
    public void TestBasicInequality() {
        ChecksimsConfig config = new ChecksimsConfig();
        ChecksimsConfig config2 = new ChecksimsConfig().setNumThreads(config.getNumThreads() + 1);

        assertNotEquals(config, config2);
    }

    @Test
    public void TestEqualityCopyConstructor() {
        ChecksimsConfig config = new ChecksimsConfig();
        ChecksimsConfig config2 = new ChecksimsConfig(config);

        assertEquals(config, config2);
    }

    @Test
    public void TestCopyConstructorCopies() {
        ChecksimsConfig config = new ChecksimsConfig();
        ChecksimsConfig config2 = new ChecksimsConfig(config);
        config2.setNumThreads(config.getNumThreads() + 1);

        assertNotEquals(config, config2);
    }
}
