package edu.wpi.checksims;

import edu.wpi.checksims.util.Token;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test for FileLineSplitter, which is itself very simple, and thus not extensively tested
 */
public class FileLineSplitterTest {
    private static List<String> empty;
    private static List<String> oneString;
    private static List<String> twoStrings;
    private static FileLineSplitter l;

    @BeforeClass
    public static void setUp() {
        empty = new LinkedList<>();

        oneString = new LinkedList<>();
        oneString.add("hello");

        twoStrings = new LinkedList<>();
        twoStrings.add("hello");
        twoStrings.add("world");

        l = FileLineSplitter.getInstance();
    }

    @Test
    public void TestEmptyReturnsEmpty() {
        List<Token<String>> results = l.splitFile(empty);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void TestOneStringReturnsString() {
        List<Token<String>> results = l.splitFile(oneString);

        List<Token<String>> expected = new LinkedList<>();
        expected.add(new Token<>("hello"));

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(results.size(), 1);
        assertEquals(results, expected);
    }

    @Test
    public void TestTwoStringsReturnsTwoStrings() {
        List<Token<String>> results = l.splitFile(twoStrings);

        List<Token<String>> expected = new LinkedList<>();
        expected.add(new Token<>("hello"));
        expected.add(new Token<>("world"));

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(results.size(), 2);
        assertEquals(results, expected);
    }
}
