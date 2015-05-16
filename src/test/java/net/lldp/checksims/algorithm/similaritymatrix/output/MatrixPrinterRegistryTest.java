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

package net.lldp.checksims.algorithm.similaritymatrix.output;

import net.lldp.checksims.util.reflection.NoSuchImplementationException;
import org.junit.Before;
import org.junit.Test;

import static net.lldp.checksims.testutil.RegistryUtils.checkRegistryContainsImpl;
import static net.lldp.checksims.testutil.RegistryUtils.checkRegistryDefault;

/**
 * Tests for the Matrix Printer registry
 */
public class MatrixPrinterRegistryTest {
    private MatrixPrinterRegistry instance;

    @Before
    public void setUp() {
        instance = MatrixPrinterRegistry.getInstance();
    }

    @Test
    public void TestContainsCSV() throws Exception {
        checkRegistryContainsImpl("csv", instance);
    }

    @Test
    public void TestContainsHTML() throws Exception {
        checkRegistryContainsImpl("html", instance);
    }

    @Test
    public void TestContainsThreshold() throws Exception {
        checkRegistryContainsImpl("threshold", instance);
    }

    @Test
    public void TestDefault() {
        checkRegistryDefault("threshold", instance);
    }

    @Test(expected = NoSuchImplementationException.class)
    public void TestThrowsExceptionOnNoSuchImpl() throws Exception {
        instance.getImplementationInstance("does_not_exist");
    }
}
