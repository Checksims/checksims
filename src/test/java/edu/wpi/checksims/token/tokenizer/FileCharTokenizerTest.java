package edu.wpi.checksims.token.tokenizer;

import edu.wpi.checksims.token.CharacterToken;
import edu.wpi.checksims.token.TokenList;
import edu.wpi.checksims.token.TokenType;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for the FileCharTokenizer
 */
public class FileCharTokenizerTest {
    private static List<String> empty;
    private static List<String> oneWord;
    private static List<String> twoWords;
    private static List<String> withTabs;
    private static List<String> multipleStrings;
    private static FileCharTokenizer c;

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

        c = FileCharTokenizer.getInstance();
    }

    @Test
    public void testEmptyReturnsEmpty() {
        TokenList results = c.splitFile(empty);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testHelloReturnsChars() {
        TokenList results = c.splitFile(oneWord);

        TokenList expected = new TokenList(TokenType.CHARACTER);
        expected.add(new CharacterToken('h'));
        expected.add(new CharacterToken('e'));
        expected.add(new CharacterToken('l'));
        expected.add(new CharacterToken('l'));
        expected.add(new CharacterToken('o'));

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(results.size(), 5);
        assertEquals(results, expected);
    }

    @Test
    public void testHelloWorldReturnsChars() {
        TokenList results = c.splitFile(twoWords);

        TokenList expected = new TokenList(TokenType.CHARACTER);
        expected.add(new CharacterToken('h'));
        expected.add(new CharacterToken('e'));
        expected.add(new CharacterToken('l'));
        expected.add(new CharacterToken('l'));
        expected.add(new CharacterToken('o'));
        expected.add(new CharacterToken(' '));
        expected.add(new CharacterToken('w'));
        expected.add(new CharacterToken('o'));
        expected.add(new CharacterToken('r'));
        expected.add(new CharacterToken('l'));
        expected.add(new CharacterToken('d'));

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(results.size(), 11);
        assertEquals(results, expected);
    }

    @Test
    public void TestHandlesWhitespaceCorrectly() {
        TokenList results = c.splitFile(withTabs);

        TokenList expected = new TokenList(TokenType.CHARACTER);
        expected.add(new CharacterToken('w'));
        expected.add(new CharacterToken('i'));
        expected.add(new CharacterToken('t'));
        expected.add(new CharacterToken('h'));
        expected.add(new CharacterToken('\t'));
        expected.add(new CharacterToken('t'));
        expected.add(new CharacterToken('a'));
        expected.add(new CharacterToken('b'));
        expected.add(new CharacterToken('s'));
        expected.add(new CharacterToken('\t'));

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(results.size(), 10);
        assertEquals(results, expected);
    }

    @Test
    public void testMultipleStringsCorrectlyParsed() {
        TokenList results = c.splitFile(multipleStrings);

        TokenList expected = new TokenList(TokenType.CHARACTER);
        expected.add(new CharacterToken('h'));
        expected.add(new CharacterToken('e'));
        expected.add(new CharacterToken('l'));
        expected.add(new CharacterToken('l'));
        expected.add(new CharacterToken('o'));
        expected.add(new CharacterToken('w'));
        expected.add(new CharacterToken('o'));
        expected.add(new CharacterToken('r'));
        expected.add(new CharacterToken('l'));
        expected.add(new CharacterToken('d'));

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(results.size(), 10);
        assertEquals(results, expected);
    }
}
