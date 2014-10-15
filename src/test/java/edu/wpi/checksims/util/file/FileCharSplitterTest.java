package edu.wpi.checksims.util.file;

import edu.wpi.checksims.util.Token;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for the FileCharSplitter
 */
public class FileCharSplitterTest {
    private static List<String> empty;
    private static List<String> oneWord;
    private static List<String> twoWords;
    private static List<String> withTabs;
    private static List<String> multipleStrings;
    private static FileCharSplitter c;

    @BeforeClass
    public static void setUp() {
        empty = new LinkedList<>();

        oneWord = new LinkedList<>();
        oneWord.add("hello");

        twoWords = new LinkedList<>();
        twoWords.add("hello world");

        withTabs = new LinkedList<>();
        withTabs.add("with\ttabs\t");

        multipleStrings = new LinkedList<>();
        multipleStrings.add("hello");
        multipleStrings.add("world");

        c = FileCharSplitter.getInstance();
    }

    @Test
    public void testEmptyReturnsEmpty() {
        List<Token<Character>> results = c.splitFile(empty);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testHelloReturnsChars() {
        List<Token<Character>> results = c.splitFile(oneWord);

        List<Token<Character>> expected = new LinkedList<>();
        expected.add(new Token<>('h'));
        expected.add(new Token<>('e'));
        expected.add(new Token<>('l'));
        expected.add(new Token<>('l'));
        expected.add(new Token<>('o'));

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(results.size(), 5);
        assertEquals(results, expected);
    }

    @Test
    public void testHelloWorldReturnsChars() {
        List<Token<Character>> results = c.splitFile(twoWords);

        List<Token<Character>> expected = new LinkedList<>();
        expected.add(new Token<>('h'));
        expected.add(new Token<>('e'));
        expected.add(new Token<>('l'));
        expected.add(new Token<>('l'));
        expected.add(new Token<>('o'));
        expected.add(new Token<>(' '));
        expected.add(new Token<>('w'));
        expected.add(new Token<>('o'));
        expected.add(new Token<>('r'));
        expected.add(new Token<>('l'));
        expected.add(new Token<>('d'));

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(results.size(), 11);
        assertEquals(results, expected);
    }

    @Test
    public void TestHandlesWhitespaceCorrectly() {
        List<Token<Character>> results = c.splitFile(withTabs);

        List<Token<Character>> expected = new LinkedList<>();
        expected.add(new Token<>('w'));
        expected.add(new Token<>('i'));
        expected.add(new Token<>('t'));
        expected.add(new Token<>('h'));
        expected.add(new Token<>('\t'));
        expected.add(new Token<>('t'));
        expected.add(new Token<>('a'));
        expected.add(new Token<>('b'));
        expected.add(new Token<>('s'));
        expected.add(new Token<>('\t'));

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(results.size(), 10);
        assertEquals(results, expected);
    }

    @Test
    public void testMultipleStringsCorrectlyParsed() {
        List<Token<Character>> results = c.splitFile(multipleStrings);

        List<Token<Character>> expected = new LinkedList<>();
        expected.add(new Token<>('h'));
        expected.add(new Token<>('e'));
        expected.add(new Token<>('l'));
        expected.add(new Token<>('l'));
        expected.add(new Token<>('o'));
        expected.add(new Token<>('w'));
        expected.add(new Token<>('o'));
        expected.add(new Token<>('r'));
        expected.add(new Token<>('l'));
        expected.add(new Token<>('d'));

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(results.size(), 10);
        assertEquals(results, expected);
    }
}
