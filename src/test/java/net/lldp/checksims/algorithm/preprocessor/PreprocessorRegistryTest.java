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

import net.lldp.checksims.ChecksimsException;
import net.lldp.checksims.testutil.RegistryUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the preprocessor registry
 */
public class PreprocessorRegistryTest {
    private PreprocessorRegistry instance;

    @Before
    public void setUp() {
        instance = PreprocessorRegistry.getInstance();
    }

    @Test
    public void TestLowerCaseIsIncluded() throws ChecksimsException {
        String lowerCaseName = LowercasePreprocessor.getInstance().getName();

        RegistryUtils.checkRegistryContainsImpl(lowerCaseName, instance);
    }

    @Test
    public void TestWhitespaceDedupIsIncluded() throws ChecksimsException {
        String dedupName = WhitespaceDeduplicationPreprocessor.getInstance().getName();

        RegistryUtils.checkRegistryContainsImpl(dedupName, instance);
    }

    @Test(expected = ChecksimsException.class)
    public void TestNonExistantThrowsException() throws ChecksimsException {
        PreprocessorRegistry.getInstance().getImplementationInstance("does not exist");
    }
}
