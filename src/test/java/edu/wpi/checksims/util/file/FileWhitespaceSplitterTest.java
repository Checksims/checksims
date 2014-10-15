package edu.wpi.checksims.util.file;

import edu.wpi.checksims.util.Token;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test that we can split files by whitespace
 */
public class FileWhitespaceSplitterTest {
    private static List<String> empty;
    private static List<String> oneWord;
    private static List<String> twoWords;
    private static List<String> wordsSpaceSeparated;
    private static List<String> wordsTabSeparated;
    private static List<String> multipleLines;
    private static FileWhitespaceSplitter s;

    @BeforeClass
    public static void setUp() {
        empty = new LinkedList<>();

        oneWord = new LinkedList<>();
        oneWord.add("hello");

        twoWords = new LinkedList<>();
        twoWords.add("hello world");

        wordsSpaceSeparated = new LinkedList<>();
        wordsSpaceSeparated.add("    hello     world       this is   a test     ");

        wordsTabSeparated = new LinkedList<>();
        wordsTabSeparated.add("hello\tworld\t\tthis \t \t is a test\t");

        multipleLines = new LinkedList<>();
        multipleLines.add("hello world");
        multipleLines.add("this is a test");

        s = FileWhitespaceSplitter.getInstance();
    }

    @Test
    public void testEmptyReturnsEmpty() {
        List<Token<String>> tokens = s.splitFile(empty);

        assertNotNull(tokens);
        assertTrue(tokens.isEmpty());
    }

    @Test
    public void testOneWordReturnsWordToken() {
        List<Token<String>> tokens = s.splitFile(oneWord);

        List<Token<String>> expected = new LinkedList<>();
        expected.add(new Token<>("hello"));

        assertNotNull(tokens);
        assertFalse(tokens.isEmpty());
        assertEquals(tokens.size(), 1);
        assertEquals(tokens, expected);
    }

    @Test
    public void testTwoWordsReturnsTwoWordTokens() {
        List<Token<String>> tokens = s.splitFile(twoWords);

        List<Token<String>> expected = new LinkedList<>();
        expected.add(new Token<>("hello"));
        expected.add(new Token<>("world"));

        assertNotNull(tokens);
        assertFalse(tokens.isEmpty());
        assertEquals(tokens.size(), 2);
        assertEquals(tokens, expected);
    }

    @Test
    public void testWordsSpaceSeparatedParsedCorrectly() {
        List<Token<String>> tokens = s.splitFile(wordsSpaceSeparated);

        List<Token<String>> expected = new LinkedList<>();
        expected.add(new Token<>("hello"));
        expected.add(new Token<>("world"));
        expected.add(new Token<>("this"));
        expected.add(new Token<>("is"));
        expected.add(new Token<>("a"));
        expected.add(new Token<>("test"));

        assertNotNull(tokens);
        assertFalse(tokens.isEmpty());
        assertEquals(tokens.size(), 6);
        assertEquals(tokens, expected);
    }

    @Test
    public void testWordsTabSeparatedParsedCorrectly() {
        List<Token<String>> tokens = s.splitFile(wordsTabSeparated);

        List<Token<String>> expected = new LinkedList<>();
        expected.add(new Token<>("hello"));
        expected.add(new Token<>("world"));
        expected.add(new Token<>("this"));
        expected.add(new Token<>("is"));
        expected.add(new Token<>("a"));
        expected.add(new Token<>("test"));

        assertNotNull(tokens);
        assertFalse(tokens.isEmpty());
        assertEquals(tokens.size(), 6);
        assertEquals(tokens, expected);
    }

    @Test
    public void testMultipleLinesParsedCorrectly() {
        List<Token<String>> tokens = s.splitFile(multipleLines);

        List<Token<String>> expected = new LinkedList<>();
        expected.add(new Token<>("hello"));
        expected.add(new Token<>("world"));
        expected.add(new Token<>("this"));
        expected.add(new Token<>("is"));
        expected.add(new Token<>("a"));
        expected.add(new Token<>("test"));

        assertNotNull(tokens);
        assertFalse(tokens.isEmpty());
        assertEquals(tokens.size(), 6);
        assertEquals(tokens, expected);
    }
}
