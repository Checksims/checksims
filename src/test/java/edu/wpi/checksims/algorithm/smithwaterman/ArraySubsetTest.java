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

import static org.junit.Assert.*;

/**
 * Tests for ArraySubset
 */
public class ArraySubsetTest {
    private ArraySubset one;
    private ArraySubset two;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() {
        one = ArraySubset.of(1, 1, 3, 3);
        two = ArraySubset.of(2, 2, 5, 5);
    }

    @Test
    public void TestCannotInitializeWithNullOrigin() {
        expectedEx.expect(NullPointerException.class);

        new ArraySubset(null, Coordinate.of(1, 1));
    }

    @Test
    public void TestCannotInitializeWithNullMax() {
        expectedEx.expect(NullPointerException.class);

        new ArraySubset(Coordinate.of(1, 1), null);
    }

    @Test
    public void TestCannotInitializeWhenOriginGreaterThanMaxInX() {
        expectedEx.expect(IllegalArgumentException.class);

        new ArraySubset(Coordinate.of(5, 1), Coordinate.of(2, 2));
    }

    @Test
    public void TestCannotInitializeWhenOriginGreaterThanMaxInY() {
        expectedEx.expect(IllegalArgumentException.class);

        new ArraySubset(Coordinate.of(1, 5), Coordinate.of(2, 2));
    }

    @Test
    public void TestRetrieveOrigin() {
        assertEquals(Coordinate.of(1, 1), one.getOrigin());
        assertEquals(Coordinate.of(2, 2), two.getOrigin());
    }

    @Test
    public void TestRetrieveMax() {
        assertEquals(Coordinate.of(3, 3), one.getMax());
        assertEquals(Coordinate.of(5, 5), two.getMax());
    }

    @Test
    public void TestContainedWithinInsideSubset() {
        assertTrue(one.contains(Coordinate.of(2, 2)));
    }

    @Test
    public void TestContainedWithinInsideSubsetTwo() {
        assertTrue(two.contains(Coordinate.of(3, 4)));
    }

    @Test
    public void TestContainedWithinContainsVertices() {
        assertTrue(one.contains(Coordinate.of(1, 1)));
        assertTrue(one.contains(Coordinate.of(1, 3)));
        assertTrue(one.contains(Coordinate.of(3, 1)));
        assertTrue(one.contains(Coordinate.of(3, 3)));
    }

    @Test
    public void TestContainedWithinReturnsFalseIfNotContainedX() {
        assertFalse(one.contains(Coordinate.of(5, 2)));
    }

    @Test
    public void TestContainedWithinReturnsFalseIfNotContainedY() {
        assertFalse(one.contains(Coordinate.of(2, 5)));
    }

    @Test
    public void TestEqualitySameInstance() {
        assertEquals(one, one);
        assertEquals(two, two);
    }

    @Test
    public void TestEqualityNewInstance() {
        assertEquals(one, ArraySubset.of(1, 1, 3, 3));
        assertEquals(two, ArraySubset.of(2, 2, 5, 5));
    }

    @Test
    public void TestInequality() {
        assertNotEquals(one, two);
    }
}
