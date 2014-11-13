package edu.wpi.checksims.util.token;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test for FileLineTokenizer, which is itself very simple, and thus not extensively tested
 */
public class FileLineTokenizerTest {
    private static List<String> empty;
    private static List<String> oneString;
    private static List<String> twoStrings;
    private static FileLineTokenizer l;

    @BeforeClass
    public static void setUp() {
        empty = new LinkedList<>();

        oneString = new LinkedList<>();
        oneString.add("hello");

        twoStrings = new LinkedList<>();
        twoStrings.add("hello");
        twoStrings.add("world");

        l = FileLineTokenizer.getInstance();
    }

    @Test
    public void TestEmptyReturnsEmpty() {
        TokenList results = l.splitFile(empty);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void TestOneStringReturnsString() {
        TokenList results = l.splitFile(oneString);

        TokenList expected = new TokenList(TokenType.LINE);
        expected.add(new LineToken("hello"));

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(results.size(), 1);
        assertEquals(results, expected);
    }

    @Test
    public void TestTwoStringsReturnsTwoStrings() {
        TokenList results = l.splitFile(twoStrings);

        TokenList expected = new TokenList(TokenType.LINE);
        expected.add(new LineToken("hello"));
        expected.add(new LineToken("world"));

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(results.size(), 2);
        assertEquals(results, expected);
    }
}
