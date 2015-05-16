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

import net.lldp.checksims.token.TokenList;
import net.lldp.checksims.token.TokenType;
import net.lldp.checksims.testutil.TokenUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.*;

import static net.lldp.checksims.testutil.SubmissionUtils.setFromElements;
import static net.lldp.checksims.testutil.TokenUtils.makeTokenListCharacter;
import static org.junit.Assert.*;

/**
 * Tests for the actual Smith-Waterman Algorithm
 */
public class SmithWatermanAlgorithmTest {
    private TokenList empty;
    private TokenList hello;
    private TokenList bigTest1;
    private TokenList bigTest2;

    private int[][] bigExpectedS;

    private SmithWatermanAlgorithm bigTest;
    private SmithWatermanAlgorithm helloTest;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() {
        empty = new TokenList(TokenType.CHARACTER);

        hello = TokenUtils.makeTokenListCharacter('h', 'e', 'l', 'l', 'o');
        bigTest1 = TokenUtils.makeTokenListCharacter('a', 'b', 'c', 'x', 'd', 'e', 'f', 'g', 'h', 'i', 'y', 'm', 'z', 'j', 'l',
                'u', 'k', 'p', 'q', 's', 'j', 't', 'u', 'v');
        bigTest2 = TokenUtils.makeTokenListCharacter('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
                'p', 'q', 'r', 's', 'j', 't', 'u', 'v');

        bigTest = new SmithWatermanAlgorithm(bigTest1, bigTest2);
        helloTest = new SmithWatermanAlgorithm(hello, hello);

        bigExpectedS = new int[][] {
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 1, 3, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 2, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 1, 3, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 2, 4, 3, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 1, 3, 5, 4, 3, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 2, 4, 6, 5, 4, 3, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 1, 3, 5, 7, 6, 5, 4, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 2, 4, 6, 8, 7, 6, 5, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 1, 3, 5, 7, 7, 6, 5, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 2, 4, 6, 6, 6, 5, 6, 5, 4, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 3, 5, 5, 5, 5, 5, 5, 4, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 6, 5, 4, 4, 4, 4, 0, 0, 0, 0, 1, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 6, 5, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 5, 5, 4, 0, 0, 0, 0, 0, 0, 0, 1, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 1, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 2, 1, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 4, 3, 2 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 5, 4 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 4, 6 }
        };
    }

    @Test
    public void TestInitSmithWatermanNullFirst() {
        expectedEx.expect(NullPointerException.class);

        new SmithWatermanAlgorithm(null, hello);
    }

    @Test
    public void TestInitSmithWatermanNullSecond() {
        expectedEx.expect(NullPointerException.class);

        new SmithWatermanAlgorithm(hello, null);
    }

    @Test
    public void TestInitSmithWatermanEmptyFirst() {
        expectedEx.expect(IllegalArgumentException.class);

        new SmithWatermanAlgorithm(empty, hello);
    }

    @Test
    public void TestInitSmithWatermanEmptySecond() {
        expectedEx.expect(IllegalArgumentException.class);

        new SmithWatermanAlgorithm(hello, empty);
    }

    @Test
    public void TestInitSmithWatermanGetBounds() {
        SmithWatermanAlgorithm alg = new SmithWatermanAlgorithm(hello, hello);

        assertEquals(ArraySubset.of(1, 1, 6, 6), alg.getWholeArray());
    }

    @Test
    public void TestInitSmithWatermanGetBoundsTwo() {
        assertEquals(ArraySubset.of(1, 1, bigTest1.size() + 1, bigTest2.size() + 1), bigTest.getWholeArray());
    }

    @Test
    public void TestSAndMInitToSame() {
        assertTrue(Arrays.deepEquals(bigTest.getS(), bigTest.getM()));
        assertTrue(Arrays.deepEquals(helloTest.getS(), helloTest.getM()));
    }

    @Test
    public void TestInitSmithWatermanBoundsMatchArraySize() {
        assertEquals(bigTest.getWholeArray().getMax().getX(), bigTest.getS().length);
        assertEquals(bigTest.getWholeArray().getMax().getY(), bigTest.getS()[0].length);
    }

    @Test
    public void TestInitSmithWatermanBoundsMatchArraySizeTwo() {
        assertEquals(helloTest.getWholeArray().getMax().getX(), helloTest.getS().length);
        assertEquals(helloTest.getWholeArray().getMax().getY(), helloTest.getS()[0].length);
    }

    @Test
    public void TestInitSmithWatermanCandidatesIsEmpty() {
        assertTrue(helloTest.getCandidates().isEmpty());
        assertTrue(bigTest.getCandidates().isEmpty());
    }

    @Test
    public void TestInitSmithWatermanListsInitializedProperly() {
        assertEquals(hello, helloTest.getXList());
        assertEquals(hello, helloTest.getYList());
        assertEquals(bigTest1, bigTest.getXList());
        assertEquals(bigTest2, bigTest.getYList());
    }

    @Test
    public void TestInitSmithWatermanListsAreClones() {
        helloTest.getXList().get(0).setValid(false);

        assertNotEquals(hello, helloTest.getXList());
        assertEquals(hello, helloTest.getYList());
    }

    @Test
    public void TestMergeCandidatesNull() {
        expectedEx.expect(NullPointerException.class);

        helloTest.mergeIntoCandidates(null);
    }

    @Test
    public void TestMergeCandidatesEmpty() {
        helloTest.mergeIntoCandidates(new HashMap<>());

        assertTrue(helloTest.getCandidates().isEmpty());
    }

    @Test
    public void TestMergeCandidatesIntoEmpty() {
        Map<Integer, Set<Coordinate>> toMerge = new HashMap<>();
        toMerge.put(5, setFromElements(Coordinate.of(1, 1), Coordinate.of(2, 2)));

        helloTest.mergeIntoCandidates(toMerge);

        assertEquals(toMerge, helloTest.getCandidates());
    }

    @Test
    public void TestMergeCandidatesNoConflicts() {
        Map<Integer, Set<Coordinate>> original = helloTest.getCandidates();
        original.put(6, setFromElements(Coordinate.of(1, 1), Coordinate.of(2, 2)));

        Map<Integer, Set<Coordinate>> toMerge = new HashMap<>();
        toMerge.put(5, setFromElements(Coordinate.of(3, 3), Coordinate.of(4, 4), Coordinate.of(5, 5)));

        Map<Integer, Set<Coordinate>> expected = new HashMap<>();
        expected.put(6, original.get(6));
        expected.put(5, toMerge.get(5));

        helloTest.mergeIntoCandidates(toMerge);

        assertEquals(expected, helloTest.getCandidates());
    }

    @Test
    public void TestMergeCandidatesSameKey() {
        Map<Integer, Set<Coordinate>> original = helloTest.getCandidates();
        original.put(5, setFromElements(Coordinate.of(1, 1), Coordinate.of(2, 2)));

        Map<Integer, Set<Coordinate>> toMerge = new HashMap<>();
        toMerge.put(5, setFromElements(Coordinate.of(3, 3), Coordinate.of(4, 4), Coordinate.of(5, 5)));

        Map<Integer, Set<Coordinate>> expected = new HashMap<>();
        expected.put(5, setFromElements(Coordinate.of(1, 1), Coordinate.of(2, 2), Coordinate.of(3, 3),
                Coordinate.of(4, 4), Coordinate.of(5, 5)));

        helloTest.mergeIntoCandidates(toMerge);

        assertEquals(expected, helloTest.getCandidates());
    }

    @Test
    public void TestMergeCandidatesFollowsSetInvariants() {
        Map<Integer, Set<Coordinate>> original = helloTest.getCandidates();
        original.put(5, setFromElements(Coordinate.of(1, 1), Coordinate.of(2, 2)));

        Map<Integer, Set<Coordinate>> toMerge = new HashMap<>();
        toMerge.put(5, setFromElements(Coordinate.of(2, 2), Coordinate.of(4, 4), Coordinate.of(5, 5)));

        Map<Integer, Set<Coordinate>> expected = new HashMap<>();
        expected.put(5, setFromElements(Coordinate.of(1, 1), Coordinate.of(2, 2), Coordinate.of(4, 4),
                Coordinate.of(5, 5)));

        helloTest.mergeIntoCandidates(toMerge);

        assertEquals(expected, helloTest.getCandidates());
    }

    @Test
    public void TestMergeCandidatesDoesNotModifyExistingElements() {
        Map<Integer, Set<Coordinate>> original = helloTest.getCandidates();
        original.put(5, setFromElements(Coordinate.of(1, 1), Coordinate.of(2, 2)));
        original.put(6, setFromElements(Coordinate.of(10, 10)));

        Map<Integer, Set<Coordinate>> toMerge = new HashMap<>();
        toMerge.put(5, setFromElements(Coordinate.of(3, 3), Coordinate.of(4, 4), Coordinate.of(5, 5)));

        Map<Integer, Set<Coordinate>> expected = new HashMap<>();
        expected.put(5, setFromElements(Coordinate.of(1, 1), Coordinate.of(2, 2), Coordinate.of(3, 3),
                Coordinate.of(4, 4), Coordinate.of(5, 5)));
        expected.put(6, setFromElements(Coordinate.of(10, 10)));

        helloTest.mergeIntoCandidates(toMerge);

        assertEquals(expected, helloTest.getCandidates());
    }

    @Test
    public void TestComputeArraySubsetNullSubset() {
        expectedEx.expect(NullPointerException.class);

        helloTest.computeArraySubset(null);
    }

    @Test
    public void TestComputeArraySubsetSubsetOutOfBoundsX() {
        expectedEx.expect(IllegalArgumentException.class);

        helloTest.computeArraySubset(ArraySubset.of(1, 1, 200, 2));
    }

    @Test
    public void TestComputeArraySubsetOutOfBoundsY() {
        expectedEx.expect(IllegalArgumentException.class);

        helloTest.computeArraySubset(ArraySubset.of(1, 1, 2, 200));
    }

    @Test
    public void TestComputeArraySubsetOneRow() {
        helloTest.computeArraySubset(ArraySubset.of(1, 1, 2, 6));

        int[][] expected = new int[6][6];
        expected[1][1] = 1;

        assertTrue(Arrays.deepEquals(expected, helloTest.getS()));
        assertTrue(Arrays.deepEquals(new int[6][6], helloTest.getM()));
    }

    @Test
    public void TestComputeArraySubsetTwoColumns() {
        helloTest.computeArraySubset(ArraySubset.of(1, 1, 6, 3));

        int[][] expectedS = new int[6][6];
        expectedS[1][1] = 1;
        expectedS[2][2] = 2;
        expectedS[3][2] = 1;

        int[][] expectedM = new int[6][6];
        expectedM[2][2] = 1;
        expectedM[3][2] = 2;

        assertTrue(Arrays.deepEquals(expectedS, helloTest.getS()));
        assertTrue(Arrays.deepEquals(expectedM, helloTest.getM()));
    }

    @Test
    public void TestComputeEntireArrayHello() {
        int[][] expectedS = new int[][] {
                { 0, 0, 0, 0, 0, 0 },
                { 0, 1, 0, 0, 0, 0 },
                { 0, 0, 2, 1, 0, 0 },
                { 0, 0, 1, 3, 2, 1 },
                { 0, 0, 0, 2, 4, 3 },
                { 0, 0, 0, 1, 3, 5 }
        };

        int[][] expectedM = new int[][] {
                { 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0 },
                { 0, 0, 1, 2, 0, 0 },
                { 0, 0, 2, 2, 2, 2 },
                { 0, 0, 0, 2, 3, 4 },
                { 0, 0, 0, 2, 4, 4 }
        };

        helloTest.computeArraySubset(helloTest.getWholeArray());

        assertTrue(Arrays.deepEquals(expectedS, helloTest.getS()));
        assertTrue(Arrays.deepEquals(expectedM, helloTest.getM()));
    }

    @Test
    public void TestComputeEntireArrayBigTest() {
        bigTest.computeArraySubset(bigTest.getWholeArray());

        assertTrue(Arrays.deepEquals(bigExpectedS, bigTest.getS()));
    }

    @Test
    public void TestComputeEntireArrayHelloGetsOneMatch() {
        Map<Integer, Set<Coordinate>> expected = new HashMap<>();
        expected.put(5, setFromElements(Coordinate.of(5, 5)));

        Map<Integer, Set<Coordinate>> result = helloTest.computeArraySubset(helloTest.getWholeArray());

        assertEquals(expected, result);
    }

    @Test
    public void TestComputeEntireArrayBigTestReturnsAppropriateMatches() {
        Map<Integer, Set<Coordinate>> results = bigTest.computeArraySubset(bigTest.getWholeArray());

        assertTrue(results.containsKey(8));
        assertTrue(results.containsKey(6));
        assertTrue(results.get(8).contains(Coordinate.of(10, 9)));
        assertTrue(results.get(6).contains(Coordinate.of(24, 23)));
    }

    @Test
    public void TestRecomputeArrayAfterCompute() {
        helloTest.computeArraySubset(helloTest.getWholeArray());

        helloTest.getXList().get(4).setValid(false);

        helloTest.computeArraySubset(ArraySubset.of(5, 5, 6, 6));

        int[][] expectedS = new int[][] {
                { 0, 0, 0, 0, 0, 0 },
                { 0, 1, 0, 0, 0, 0 },
                { 0, 0, 2, 1, 0, 0 },
                { 0, 0, 1, 3, 2, 1 },
                { 0, 0, 0, 2, 4, 3 },
                { 0, 0, 0, 1, 3, 3 }
        };

        int[][] expectedM = new int[][] {
                { 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0 },
                { 0, 0, 1, 2, 0, 0 },
                { 0, 0, 2, 2, 2, 2 },
                { 0, 0, 0, 2, 3, 4 },
                { 0, 0, 0, 2, 4, 4 }
        };

        assertTrue(Arrays.deepEquals(expectedS, helloTest.getS()));
        assertTrue(Arrays.deepEquals(expectedM, helloTest.getM()));
    }

    @Test
    public void TestComputeArrayNoSignificantResults() {
        helloTest.getXList().get(4).setValid(false);

        Map<Integer, Set<Coordinate>> results = helloTest.computeArraySubset(helloTest.getWholeArray());

        assertTrue(results.isEmpty());
    }

    @Test
    public void TestGetMatchCoordinatesNull() {
        expectedEx.expect(NullPointerException.class);

        helloTest.getMatchCoordinates(null);
    }

    @Test
    public void TestGetMatchCoordinateOutOfArray() {
        expectedEx.expect(IllegalArgumentException.class);

        helloTest.getMatchCoordinates(Coordinate.of(100, 200));
    }

    @Test
    public void TestGetMatchCoordinateZeroInArray() {
        expectedEx.expect(IllegalArgumentException.class);

        helloTest.getMatchCoordinates(Coordinate.of(1, 1));
    }

    @Test
    public void TestGetMatchCoordinatesHello() {
        helloTest.computeArraySubset(helloTest.getWholeArray());

        Set<Coordinate> expected = setFromElements(Coordinate.of(1, 1), Coordinate.of(2, 2), Coordinate.of(3, 3),
                Coordinate.of(4, 4), Coordinate.of(5, 5));

        Set<Coordinate> results = helloTest.getMatchCoordinates(Coordinate.of(5, 5));

        assertEquals(expected, results);
    }

    @Test
    public void TestGetMatchCoordinatesBigTest() {
        bigTest.computeArraySubset(bigTest.getWholeArray());

        Set<Coordinate> expected = setFromElements(Coordinate.of(10, 9), Coordinate.of(9, 8), Coordinate.of(8, 7),
                Coordinate.of(7, 6), Coordinate.of(6, 5), Coordinate.of(5, 4), Coordinate.of(3, 3), Coordinate.of(2, 2),
                Coordinate.of(1, 1));

        Set<Coordinate> results = bigTest.getMatchCoordinates(Coordinate.of(10, 9));

        assertEquals(expected, results);
    }

    @Test
    public void TestGetMatchCoordinatesBigTestTwo() {
        bigTest.computeArraySubset(bigTest.getWholeArray());

        Set<Coordinate> expected = setFromElements(Coordinate.of(19, 17), Coordinate.of(18, 16));
        Set<Coordinate> results = bigTest.getMatchCoordinates(Coordinate.of(19, 18));

        assertEquals(expected, results);
    }

    @Test
    public void TestGetMatchCoordinatesBigTestThree() {
        bigTest.getYList().get(6).setValid(false);

        bigTest.computeArraySubset(bigTest.getWholeArray());

        Set<Coordinate> expected = setFromElements(Coordinate.of(10, 9), Coordinate.of(9, 8), Coordinate.of(7, 6),
                Coordinate.of(6, 5), Coordinate.of(5, 4), Coordinate.of(3, 3), Coordinate.of(2, 2),
                Coordinate.of(1, 1));
        Set<Coordinate> results = bigTest.getMatchCoordinates(Coordinate.of(10, 9));

        assertEquals(expected, results);
    }

    @Test
    public void TestSetMatchesInvalidNullThrowsException() {
        expectedEx.expect(NullPointerException.class);

        helloTest.setMatchesInvalid(null);
    }

    @Test
    public void TestSetMatchesInvalidNoChangesIfEmpty() {
        helloTest.setMatchesInvalid(new HashSet<>());

        assertEquals(hello, helloTest.getXList());
        assertEquals(hello, helloTest.getYList());
    }

    @Test
    public void TestSetMatchesInvalidHello() {
        Set<Coordinate> match = setFromElements(Coordinate.of(1, 1), Coordinate.of(2, 2), Coordinate.of(3, 3),
                Coordinate.of(4, 4), Coordinate.of(5, 5));
        TokenList expected = TokenList.cloneTokenList(hello);
        expected.stream().forEach((token) -> token.setValid(false));

        helloTest.setMatchesInvalid(match);

        assertEquals(expected, helloTest.getXList());
        assertEquals(expected, helloTest.getYList());
    }

    @Test
    public void TestSetMatchesInvalidBigTest() {
        Set<Coordinate> match = setFromElements(Coordinate.of(10, 9), Coordinate.of(9, 8), Coordinate.of(8, 7),
                Coordinate.of(7, 6), Coordinate.of(6, 5), Coordinate.of(5, 4), Coordinate.of(3, 3), Coordinate.of(2, 2),
                Coordinate.of(1, 1));

        TokenList expectedX = TokenList.cloneTokenList(bigTest1);
        TokenList expectedY = TokenList.cloneTokenList(bigTest2);

        match.stream().forEach((coord) -> {
            expectedX.get(coord.getX() - 1).setValid(false);
            expectedY.get(coord.getY() - 1).setValid(false);
        });

        bigTest.setMatchesInvalid(match);

        assertEquals(expectedX, bigTest.getXList());
        assertEquals(expectedY, bigTest.getYList());
    }

    @Test
    public void TestGetFirstMatchNull() {
        expectedEx.expect(NullPointerException.class);

        SmithWatermanAlgorithm.getFirstMatchCoordinate(null);
    }

    @Test
    public void TestGetFirstMatchEmpty() {
        expectedEx.expect(IllegalArgumentException.class);

        SmithWatermanAlgorithm.getFirstMatchCoordinate(new HashSet<>());
    }

    @Test
    public void TestOneElementGetFirstMatch() {
        Coordinate expected = Coordinate.of(1, 2);
        Coordinate result = SmithWatermanAlgorithm.getFirstMatchCoordinate(setFromElements(expected));

        assertEquals(expected, result);
    }

    @Test
    public void TestTwoElementsGetFirstMatch() {
        Coordinate expected = Coordinate.of(1, 1);
        Set<Coordinate> testWith = setFromElements(expected, Coordinate.of(2, 2));

        Coordinate result = SmithWatermanAlgorithm.getFirstMatchCoordinate(testWith);

        assertEquals(expected, result);
    }

    @Test
    public void TestBigTestGetFirstMatch() {
        Set<Coordinate> match = setFromElements(Coordinate.of(10, 9), Coordinate.of(9, 8), Coordinate.of(8, 7),
                Coordinate.of(7, 6), Coordinate.of(6, 5), Coordinate.of(5, 4), Coordinate.of(3, 3), Coordinate.of(2, 2),
                Coordinate.of(1, 1));
        Coordinate expected = Coordinate.of(1, 1);

        Coordinate result = SmithWatermanAlgorithm.getFirstMatchCoordinate(match);

        assertEquals(expected, result);
    }

    @Test
    public void TestFilterPostDominatedNullFirst() {
        expectedEx.expect(NullPointerException.class);

        helloTest.filterPostdominated(null, Coordinate.of(4, 5));
    }

    @Test
    public void TestFilterPostDominatedNullSecond() {
        expectedEx.expect(NullPointerException.class);

        helloTest.filterPostdominated(Coordinate.of(4, 5), null);
    }

    @Test
    public void TestFilterPostDominatedFirstOutOfArray() {
        expectedEx.expect(IllegalArgumentException.class);

        helloTest.filterPostdominated(Coordinate.of(100, 100), Coordinate.of(3, 3));
    }

    @Test
    public void TestFilterPostDominatedSecondOutOfArray() {
        expectedEx.expect(IllegalArgumentException.class);

        helloTest.filterPostdominated(Coordinate.of(1, 1), Coordinate.of(8, 8));
    }

    @Test
    public void TestFilterPostDominatedEmpty() {
        Map<Integer, Set<Coordinate>> results = helloTest.filterPostdominated(Coordinate.of(1, 1), Coordinate.of(6, 6));

        assertTrue(results.isEmpty());
    }

    @Test
    public void TestFilterPostDominatedAllOfHelloFiltered() {
        helloTest.mergeIntoCandidates(helloTest.computeArraySubset(helloTest.getWholeArray()));

        Map<Integer, Set<Coordinate>> results = helloTest.filterPostdominated(Coordinate.of(1, 1), Coordinate.of(6, 6));

        assertTrue(results.isEmpty());
    }

    @Test
    public void TestFilterPostDominatedBigTestFiltered() {
        Map<Integer, Set<Coordinate>> expected = new HashMap<>();
        expected.put(5, setFromElements(Coordinate.of(23, 22)));
        expected.put(6, setFromElements(Coordinate.of(24, 23)));

        bigTest.mergeIntoCandidates(bigTest.computeArraySubset(bigTest.getWholeArray()));

        Map<Integer, Set<Coordinate>> results = bigTest.filterPostdominated(Coordinate.of(1, 1), Coordinate.of(10, 9));

        assertEquals(expected, results);
    }

    @Test
    public void TestZeroMatchNullFirst() {
        expectedEx.expect(NullPointerException.class);

        helloTest.zeroMatch(null, Coordinate.of(1, 1));
    }

    @Test
    public void TestZeroMatchNullSecond() {
        expectedEx.expect(NullPointerException.class);

        helloTest.zeroMatch(Coordinate.of(3, 3), null);
    }

    @Test
    public void TestZeroMatchOutOfBoundsFirst() {
        expectedEx.expect(IllegalArgumentException.class);

        helloTest.zeroMatch(Coordinate.of(100, 100), Coordinate.of(6, 6));
    }

    @Test
    public void TestZeroMatchOutOfBoundsSecond() {
        expectedEx.expect(IllegalArgumentException.class);

        helloTest.zeroMatch(Coordinate.of(1, 1), Coordinate.of(100, 100));
    }

    @Test
    public void TestZeroMatchHello() {
        int[][] expected = new int[6][6];

        helloTest.computeArraySubset(helloTest.getWholeArray());

        helloTest.zeroMatch(Coordinate.of(1, 1), Coordinate.of(5, 5));

        assertTrue(Arrays.deepEquals(expected, helloTest.getS()));
        assertTrue(Arrays.deepEquals(expected, helloTest.getM()));
    }

    @Test
    public void TestZeroMatchBigTest() {
        int[][] expected = new int[][] {
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 6, 5, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 6, 5, 6, 5, 4, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 5, 5, 5, 4, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 5, 4, 4, 4, 4, 0, 0, 0, 0, 1, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 6, 5, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 5, 5, 4, 0, 0, 0, 0, 0, 0, 0, 1, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 1, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 2, 1, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 4, 3, 2 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 5, 4 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 4, 6 }
        };


        bigTest.computeArraySubset(bigTest.getWholeArray());

        bigTest.zeroMatch(Coordinate.of(1, 1), Coordinate.of(10, 9));

        assertTrue(Arrays.deepEquals(expected, bigTest.getS()));
    }

    @Test
    public void TestGetMaxAllZeroes() {
        int max = SmithWatermanAlgorithm.getMaxOfInts(0, 0, 0);

        assertEquals(0, max);
    }

    @Test
    public void TestGetMaxAllSameNonzero() {
        int max = SmithWatermanAlgorithm.getMaxOfInts(3, 3, 3);

        assertEquals(3, max);
    }

    @Test
    public void TestGetMaxAllSameNegative() {
        int max = SmithWatermanAlgorithm.getMaxOfInts(-5, -5, -5);

        assertEquals(-5, max);
    }

    @Test
    public void TestGetMaxALargest() {
        int max = SmithWatermanAlgorithm.getMaxOfInts(10, 4, 3);

        assertEquals(10, max);
    }

    @Test
    public void TestGetMaxALargestBCSame() {
        int max = SmithWatermanAlgorithm.getMaxOfInts(5, 4, 4);

        assertEquals(5, max);
    }

    @Test
    public void TestGetMaxBLargest() {
        int max = SmithWatermanAlgorithm.getMaxOfInts(9, 18, 17);

        assertEquals(18, max);
    }

    @Test
    public void TestGetMaxBLargestACSame() {
        int max = SmithWatermanAlgorithm.getMaxOfInts(9, 120, 9);

        assertEquals(120, max);
    }

    @Test
    public void TestGetMaxCLargest() {
        int max = SmithWatermanAlgorithm.getMaxOfInts(1200, 1300, 1400);

        assertEquals(1400, max);
    }

    @Test
    public void TestGetMaxCLargestABSame() {
        int max = SmithWatermanAlgorithm.getMaxOfInts(123, 123, 456);

        assertEquals(456, max);
    }

    @Test
    public void TestUntestedBranch() {
        int max = SmithWatermanAlgorithm.getMaxOfInts(3, 1, 2);

        assertEquals(3, max);
    }

    @Test
    public void TestGetMaxOfCoordinatesPassNull() {
        expectedEx.expect(NullPointerException.class);

        bigTest.computeArraySubset(bigTest.getWholeArray());
        bigTest.getMaxOfCoordinates(null);
    }

    @Test
    public void TestGetMaxOfCoordinatesPassEmpty() {
        expectedEx.expect(IllegalArgumentException.class);

        bigTest.computeArraySubset(bigTest.getWholeArray());
        bigTest.getMaxOfCoordinates(new HashSet<>());
    }

    @Test
    public void TestGetMaxOfCoordinatesOneCoordinate() {
        Set<Coordinate> toCheck = setFromElements(Coordinate.of(10, 10));

        bigTest.computeArraySubset(bigTest.getWholeArray());
        Coordinate result = bigTest.getMaxOfCoordinates(toCheck);

        assertEquals(Coordinate.of(10, 10), result);
    }

    @Test
    public void TestGetMaxOfCoordinatesBigTest() {
        Set<Coordinate> toCheck = setFromElements( Coordinate.of(9, 8), Coordinate.of(8, 7), Coordinate.of(10, 9),
                Coordinate.of(7, 6), Coordinate.of(6, 5), Coordinate.of(5, 4), Coordinate.of(3, 3), Coordinate.of(2, 2),
                Coordinate.of(1, 1));
        Coordinate expected = Coordinate.of(10, 9);

        bigTest.computeArraySubset(bigTest.getWholeArray());
        Coordinate result = bigTest.getMaxOfCoordinates(toCheck);

        assertEquals(expected, result);
    }
}
