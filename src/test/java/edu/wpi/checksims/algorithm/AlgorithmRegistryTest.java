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

package edu.wpi.checksims.algorithm;

import edu.wpi.checksims.ChecksimsException;
import edu.wpi.checksims.algorithm.linesimilarity.LineSimilarityChecker;
import edu.wpi.checksims.algorithm.smithwaterman.SmithWaterman;
import org.junit.Before;
import org.junit.Test;

import static edu.wpi.checksims.testutil.RegistryUtils.checkRegistryContainsImpl;
import static edu.wpi.checksims.testutil.RegistryUtils.checkRegistryDefault;

/**
 * Tests for the Algorithm Registry
 *
 * These are a tad iffy, because the registry itself only scans once
 * We're just hoping it scans the right things during that once
 */
public class AlgorithmRegistryTest {
    private AlgorithmRegistry instance;

    @Before
    public void setUp() {
        instance = AlgorithmRegistry.getInstance();
    }

    @Test
    public void TestIncludesLineCompare() throws ChecksimsException {
        String lineCompareName = LineSimilarityChecker.getInstance().getName();

        checkRegistryContainsImpl(lineCompareName, instance);
    }

    @Test
    public void TestIncludeSmithWaterman() throws ChecksimsException {
        String smithWatermanName = SmithWaterman.getInstance().getName();

        checkRegistryContainsImpl(smithWatermanName, instance);
    }

    @Test
    public void TestDefaultAlgorithmIsLineCompare() {
        String lineCompareName = LineSimilarityChecker.getInstance().getName();

        checkRegistryDefault(lineCompareName, instance);
    }

    @Test(expected = ChecksimsException.class)
    public void TestExceptionOnNonexistantAlgorithm() throws ChecksimsException {
        AlgorithmRegistry.getInstance().getImplementationInstance("does not exist");
    }
}
