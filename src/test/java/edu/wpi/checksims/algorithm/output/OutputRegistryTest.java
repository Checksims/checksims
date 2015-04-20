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

package edu.wpi.checksims.algorithm.output;

import edu.wpi.checksims.ChecksimsException;
import org.junit.Before;
import org.junit.Test;

import static edu.wpi.checksims.testutil.RegistryUtils.checkRegistryContainsImpl;
import static edu.wpi.checksims.testutil.RegistryUtils.checkRegistryDefault;

/**
 * Tests for the output registry
 */
public class OutputRegistryTest {
    private OutputRegistry instance;

    @Before
    public void setUp() {
        instance = OutputRegistry.getInstance();
    }

    @Test
    public void TestOutputRegistryContainsThreshold() throws ChecksimsException {
        String thresholdName = SimilarityMatrixThresholdPrinter.getInstance().getName();

        checkRegistryContainsImpl(thresholdName, instance);
    }

    @Test
    public void TestOutputRegistryContainsCSV() throws ChecksimsException {
        String csvName = SimilarityMatrixAsCSVPrinter.getInstance().getName();

        checkRegistryContainsImpl(csvName, instance);
    }

    @Test
    public void TestOutputRegistryContainsHTML() throws ChecksimsException {
        String htmlName = SimilarityMatrixAsHTMLPrinter.getInstance().getName();

        checkRegistryContainsImpl(htmlName, instance);
    }

    @Test
    public void TestDefaultStrategyIsThreshold() {
        String thresholdName = SimilarityMatrixThresholdPrinter.getInstance().getName();

        checkRegistryDefault(thresholdName, instance);
    }

    @Test(expected = ChecksimsException.class)
    public void TestInvalidNameThrowsException() throws ChecksimsException {
        OutputRegistry.getInstance().getImplementationInstance("does not exist");
    }
}
