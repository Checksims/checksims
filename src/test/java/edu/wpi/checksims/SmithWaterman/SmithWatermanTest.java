package edu.wpi.checksims.SmithWaterman;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;


/**
 * Tests for the Smith-Waterman algorithm
 */
public class SmithWatermanTest {
    @Test
    public void TestSmithWatermanEmptyString() {
        String a = "";

        SmithWatermanParameters p = new SmithWatermanParameters(a, a);
        SmithWatermanResults r = SmithWaterman.applySmithWaterman(p);

        assertEquals(r.getMaxOverlay(), 0);
        assertTrue(r.isEmpty());
        assertFalse(r.areMatchesPresent());
    }

    @Test
    public void TestSmithWatermanOneEmptyString() {
        String a = "";
        String b = "abc";

        SmithWatermanParameters p = new SmithWatermanParameters(a, b);
        SmithWatermanResults r = SmithWaterman.applySmithWaterman(p);

        assertEquals(r.getMaxOverlay(), 0);
        assertTrue(r.isEmpty());
        assertFalse(r.areMatchesPresent());
    }

    @Test
    public void TestSmithWatermanIdenticalOneCharacterStrings() {
        String a = "a";

        SmithWatermanParameters p = new SmithWatermanParameters(a, a);
        SmithWatermanResults r = SmithWaterman.applySmithWaterman(p);

        assertEquals(r.getMaxOverlay(), a.length());
        assertFalse(r.isEmpty());
        assertTrue(r.areMatchesPresent());
        assertEquals(r.getMatch(), a + "\n" + a);
    }

    @Test
    public void TestSmithWatermanIdenticalStrings() {
        String a = "abcdefgh";

        SmithWatermanParameters p = new SmithWatermanParameters(a, a);
        SmithWatermanResults r = SmithWaterman.applySmithWaterman(p);

        assertEquals(r.getMaxOverlay(), a.length());
        assertFalse(r.isEmpty());
        assertTrue(r.areMatchesPresent());
        assertEquals(r.getMatch(), a + "\n" + a);
    }

    @Test
    public void TestSmithWatermanNonMatchingChars() {
        String a = "a";
        String b = "b";

        SmithWatermanParameters p = new SmithWatermanParameters(a, b);
        SmithWatermanResults r = SmithWaterman.applySmithWaterman(p);

        assertEquals(r.getMaxOverlay(), 0);
        assertFalse(r.isEmpty());
        assertFalse(r.areMatchesPresent());
        assertEquals(r.getMatch(), "No matches");
    }

    @Test
    public void TestSmithWatermanNonMatchingStrings() {
        String a = "abcd";
        String b = "efgh";

        SmithWatermanParameters p = new SmithWatermanParameters(a, b);
        SmithWatermanResults r = SmithWaterman.applySmithWaterman(p);

        assertEquals(r.getMaxOverlay(), 0);
        assertFalse(r.isEmpty());
        assertFalse(r.areMatchesPresent());
        assertEquals(r.getMatch(), "No matches");
    }

    @Test
    public void TestSmithWatermanPartialMatchSameLength() {
        String match = "ab";
        String a = match + "cd";
        String b = match + "ef";

        SmithWatermanParameters p = new SmithWatermanParameters(a, b);
        SmithWatermanResults r = SmithWaterman.applySmithWaterman(p);

        assertEquals(r.getMaxOverlay(), match.length());
        assertFalse(r.isEmpty());
        assertTrue(r.areMatchesPresent());
        assertEquals(r.getMatch(), match + "\n" + match);
    }

    @Test
    public void TestSmithWatermanPartialMatchSameLengthEnd() {
        String match = "ab";
        String a = "cd" + match;
        String b = "ef" + match;

        SmithWatermanParameters p = new SmithWatermanParameters(a, b);
        SmithWatermanResults r = SmithWaterman.applySmithWaterman(p);

        assertEquals(r.getMaxOverlay(), match.length());
        assertFalse(r.isEmpty());
        assertTrue(r.areMatchesPresent());
        assertEquals(r.getMatch(), match + "\n" + match);
    }

    @Test
    public void TestSmithWatermanPartialMatchSameLengthMiddle() {
        String match = "ab";
        String a = "c" + match + "d";
        String b = "e" + match + "f";

        SmithWatermanParameters p = new SmithWatermanParameters(a, b);
        SmithWatermanResults r = SmithWaterman.applySmithWaterman(p);

        assertEquals(r.getMaxOverlay(), match.length());
        assertFalse(r.isEmpty());
        assertTrue(r.areMatchesPresent());
        assertEquals(r.getMatch(), match + "\n" + match);
    }

    @Test
    public void TestSmithWatermanInterruptedMatchSameLength() {
        String match1 = "ab";
        String match2 = "c";
        String a = match1 + "x" + match2;
        String b = match1 + "y" + match2;

        SmithWatermanParameters p = new SmithWatermanParameters(a, b);
        SmithWatermanResults r = SmithWaterman.applySmithWaterman(p);

        assertEquals(r.getMaxOverlay(), match1.length()); // Again no increment
        assertFalse(r.isEmpty());
        assertTrue(r.areMatchesPresent());
        assertEquals(r.getMatch(), match1 + "-" + match2 + "\n" + match1 + "-" + match2);
    }

    @Test
    public void TestSmithWatermanInterruptedMiddleOfStringSameLength() {
        String match1 = "ab";
        String match2 = "c";
        String a = "z" + match1 + "x" + match2 + "p";
        String b = "q" + match1 + "y" + match2 + "l";

        SmithWatermanParameters p = new SmithWatermanParameters(a, b, 10);
        SmithWatermanResults r = SmithWaterman.applySmithWaterman(p);

        assertEquals(r.getMaxOverlay(), match1.length()); // No match2 length - extra space prevents increment
        assertFalse(r.isEmpty());
        assertTrue(r.areMatchesPresent());
        assertEquals(r.getMatch(), match1 + "-" + match2 + "\n" + match1 + "-" + match2);
    }

    @Test
    public void TestSmithWatermanNoMatchDifferentStringLength() {
        String a = "abcdef";
        String b = "xyz";

        SmithWatermanParameters p = new SmithWatermanParameters(a, b);
        SmithWatermanResults r = SmithWaterman.applySmithWaterman(p);

        assertEquals(r.getMaxOverlay(), 0);
        assertFalse(r.isEmpty());
        assertFalse(r.areMatchesPresent());
        assertEquals(r.getMatch(), "No matches");
    }

    @Test
    public void TestSmithWatermanUninterruptedDifferentStringLength() {
        String match = "ab";
        String a = match + "xyz";
        String b = match;

        SmithWatermanParameters p = new SmithWatermanParameters(a, b);
        SmithWatermanResults r = SmithWaterman.applySmithWaterman(p);

        assertEquals(r.getMaxOverlay(), match.length());
        assertFalse(r.isEmpty());
        assertTrue(r.areMatchesPresent());
        assertEquals(r.getMatch(), match + "\n" + match);
    }

    @Test
    public void TestSmithWatermanInterruptedDifferentStringLength() {
        String match1 = "abcd";
        String match2 = "efghi";
        String a = match1 + "x" + match2 + "p";
        String b = "q" + match1 + "yz" + match2;

        SmithWatermanParameters p = new SmithWatermanParameters(a, b);
        SmithWatermanResults r = SmithWaterman.applySmithWaterman(p);

        assertEquals(r.getMaxOverlay(), 7);
        assertFalse(r.isEmpty());
        assertTrue(r.areMatchesPresent());
        assertTrue(r.getMatch().startsWith(match1));
        assertTrue(r.getMatch().endsWith(match2));
        assertEquals(r.getMatch().length(), (11 * 2) + 1);
    }

    @Test
    public void TestSmithWatermanLargeTestSample() {
        String a = "abcdefghijklmnopqrsjtuv";
        String b = "abcxdefghiymzjlukpqsjtuv";

        SmithWatermanParameters p = new SmithWatermanParameters(a, b);
        SmithWatermanResults r = SmithWaterman.applySmithWaterman(p);

        assertEquals(r.getMaxOverlay(), 8);
        assertFalse(r.isEmpty());
        assertTrue(r.areMatchesPresent());
        assertEquals(r.getMatch(), "abc-defghi\nabcxdefghi");
    }

    @Test
    public void TestSmithWatermanLoopingLargeTestSample() {
        String a = "abcdefghijklmnopqrsjtuv";
        String b = "abcxdefghiymzjlukpqsjtuv";

        SmithWatermanParameters p = new SmithWatermanParameters(a, b);
        int totalResult = SmithWaterman.applySmithWatermanLooping(p);

        assertEquals(totalResult, 14);
    }
}
