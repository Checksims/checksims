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

package edu.wpi.checksims.algorithm.smithwaterman;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Tests for the Coordinate class
 */
public class CoordinateTest {
    private Coordinate one;
    private Coordinate two;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() {
        one = Coordinate.of(1, 2);
        two = Coordinate.of(3, 4);
    }

    @Test
    public void TestRetrieveXOne() {
        assertEquals(1, one.getX());
    }

    @Test
    public void TestRetrieveYOne() {
        assertEquals(2, one.getY());
    }

    @Test
    public void TestRetrieveXTwo() {
        assertEquals(3, two.getX());
    }

    @Test
    public void TestRetrieveYTwo() {
        assertEquals(4, two.getY());
    }

    @Test
    public void TestGetXAndGetLeftIdentical() {
        assertEquals(one.getX(), one.getLeft().intValue());
        assertEquals(two.getX(), two.getLeft().intValue());
    }

    @Test
    public void TestGetYAndGetRightIdentical() {
        assertEquals(one.getY(), one.getRight().intValue());
        assertEquals(two.getY(), two.getRight().intValue());
    }

    @Test
    public void TestUnsupportedOperationOnUnusedFunction() {
        expectedEx.expect(UnsupportedOperationException.class);

        one.setValue(5);
    }

    @Test
    public void TestEqualitySameInstance() {
        assertEquals(one, one);
        assertEquals(two, two);
    }

    @Test
    public void TestEqualityNewInstance() {
        assertEquals(one, Coordinate.of(1, 2));
        assertEquals(two, Coordinate.of(3, 4));
    }

    @Test
    public void TestInequality() {
        assertNotEquals(one, two);
    }
}
