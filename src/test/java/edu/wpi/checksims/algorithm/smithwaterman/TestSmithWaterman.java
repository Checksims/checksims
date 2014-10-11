package edu.wpi.checksims.algorithm.smithwaterman;

import com.google.common.base.Joiner;
import edu.wpi.checksims.util.Token;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

import static org.junit.Assert.*;

/**
 * Tests for the Smith-Waterman Algorithm plagiarism detector
 */
public class TestSmithWaterman {
    private static List<Token<String>> emptyList;
    private static List<Token<String>> singleEltListA;
    private static List<Token<String>> singleEltListB;

    private static List<Token<String>> multiEltListAB;
    private static List<Token<String>> multiEltListABC;
    private static List<Token<String>> multiEltListAC;
    private static List<Token<String>> multiEltListCD;

    @BeforeClass
    public static void setUp() {
        Token<String> a = new Token<>("a");
        Token<String> b = new Token<>("b");
        Token<String> c = new Token<>("c");
        Token<String> d = new Token<>("d");
        Token<String> e = new Token<>("e");

        emptyList = new LinkedList<>();

        singleEltListA = new LinkedList<>();
        singleEltListA.add(a);

        singleEltListB = new LinkedList<>();
        singleEltListB.add(b);

        multiEltListAB = new LinkedList<>();
        multiEltListAB.add(a);
        multiEltListAB.add(b);

        multiEltListABC = new LinkedList<>();
        multiEltListABC.add(a);
        multiEltListABC.add(b);
        multiEltListABC.add(c);

        multiEltListAC = new LinkedList<>();
        multiEltListAC.add(a);
        multiEltListAC.add(c);

        multiEltListCD = new LinkedList<>();
        multiEltListCD.add(c);
        multiEltListCD.add(d);
    }

    @Test
    public void TestSmithWatermanEmptyLists() {
        SmithWatermanResults<String> r = SmithWaterman.applySmithWaterman(emptyList, emptyList, SmithWatermanParameters.getDefaultParams());

        assertNull(r);
    }

    @Test
    public void TestSmithWatermanOneEmptyList() {
        SmithWatermanResults<String> r = SmithWaterman.applySmithWaterman(emptyList, singleEltListA, SmithWatermanParameters.getDefaultParams());

        assertNull(r);
    }

    @Test
    public void TestSmithWatermanNonMatchingSingleCharLists() {
        SmithWatermanResults<String> r = SmithWaterman.applySmithWaterman(singleEltListA, singleEltListB, SmithWatermanParameters.getDefaultParams());

        assertNotNull(r);
        assertFalse(r.hasMatch());
        assertTrue(r.getMatch().isEmpty());
        assertEquals(0, r.getMatchLength());
    }

    @Test
    public void TestSmithWatermanMatchingSingleCharLists() {
        SmithWatermanResults<String> r = SmithWaterman.applySmithWaterman(singleEltListA, singleEltListA, SmithWatermanParameters.getDefaultParams());

        assertNotNull(r);
        assertTrue(r.hasMatch());
        assertEquals(Joiner.on("").join(r.getMatch()), "a");
        assertEquals(1, r.getMatchLength());
    }

    @Test
    public void TestSmithWatermanMatchingMultiCharLists() {
        SmithWatermanResults<String> r = SmithWaterman.applySmithWaterman(multiEltListAB, multiEltListAB, SmithWatermanParameters.getDefaultParams());

        assertNotNull(r);
        assertTrue(r.hasMatch());
        assertEquals(Joiner.on("").join(r.getMatch()), "ab");
        assertEquals(2, r.getMatchLength());
    }

    @Test
    public void TestSmithWatermanNotMatchingMultiCharLists() {
        SmithWatermanResults<String> r = SmithWaterman.applySmithWaterman(multiEltListAB, multiEltListCD, SmithWatermanParameters.getDefaultParams());

        assertNotNull(r);
        assertFalse(r.hasMatch());
        assertTrue(r.getMatch().isEmpty());
        assertEquals(r.getMatchLength(), 0);
    }

    @Test
    public void TestSmithWatermanPartialMatchSameLengthCharLists() {
        SmithWatermanResults<String> r = SmithWaterman.applySmithWaterman(multiEltListAB, multiEltListAC, SmithWatermanParameters.getDefaultParams());

        assertNotNull(r);
        assertTrue(r.hasMatch());
        assertEquals(Joiner.on("").join(r.getMatch()), "a");
        assertEquals(r.getMatchLength(), 1);
    }

    @Test
    public void TestSmithWatermanMatchingDifferentLengthCharLists() {
        SmithWatermanResults<String> r = SmithWaterman.applySmithWaterman(multiEltListAB, multiEltListABC, SmithWatermanParameters.getDefaultParams());

        assertNotNull(r);
        assertTrue(r.hasMatch());
        assertEquals(Joiner.on("").join(r.getMatch()), "ab");
        assertEquals(r.getMatchLength(), 2);
    }

    @Test
    public void TestSmithWatermanPartialMatchingDifferentLengthCharLists() {
        SmithWatermanResults<String> r = SmithWaterman.applySmithWaterman(multiEltListABC, multiEltListCD, SmithWatermanParameters.getDefaultParams());

        assertNotNull(r);
        assertTrue(r.hasMatch());
        assertEquals(Joiner.on("").join(r.getMatch()), "c");
        assertEquals(r.getMatchLength(), 1);
    }
}
