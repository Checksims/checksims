package edu.wpi.checksims.token.tokenizer;

import edu.wpi.checksims.token.TokenList;
import edu.wpi.checksims.token.TokenType;
import edu.wpi.checksims.token.WhitespaceToken;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test that we can split files by whitespace
 */
public class FileWhitespaceTokenizerTest {
    private static List<String> empty;
    private static List<String> oneWord;
    private static List<String> twoWords;
    private static List<String> wordsSpaceSeparated;
    private static List<String> wordsTabSeparated;
    private static List<String> multipleLines;
    private static FileWhitespaceTokenizer s;

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

        s = FileWhitespaceTokenizer.getInstance();
    }

    @Test
    public void testEmptyReturnsEmpty() {
        TokenList tokens = s.splitFile(empty);

        assertNotNull(tokens);
        assertTrue(tokens.isEmpty());
    }

    @Test
    public void testOneWordReturnsWordToken() {
        TokenList tokens = s.splitFile(oneWord);

        TokenList expected = new TokenList(TokenType.WHITESPACE);
        expected.add(new WhitespaceToken("hello"));

        assertNotNull(tokens);
        assertFalse(tokens.isEmpty());
        assertEquals(tokens.size(), 1);
        assertEquals(tokens, expected);
    }

    @Test
    public void testTwoWordsReturnsTwoWordTokens() {
        TokenList tokens = s.splitFile(twoWords);

        TokenList expected = new TokenList(TokenType.WHITESPACE);
        expected.add(new WhitespaceToken("hello"));
        expected.add(new WhitespaceToken("world"));

        assertNotNull(tokens);
        assertFalse(tokens.isEmpty());
        assertEquals(tokens.size(), 2);
        assertEquals(tokens, expected);
    }

    @Test
    public void testWordsSpaceSeparatedParsedCorrectly() {
        TokenList tokens = s.splitFile(wordsSpaceSeparated);

        TokenList expected = new TokenList(TokenType.WHITESPACE);
        expected.add(new WhitespaceToken("hello"));
        expected.add(new WhitespaceToken("world"));
        expected.add(new WhitespaceToken("this"));
        expected.add(new WhitespaceToken("is"));
        expected.add(new WhitespaceToken("a"));
        expected.add(new WhitespaceToken("test"));

        assertNotNull(tokens);
        assertFalse(tokens.isEmpty());
        assertEquals(tokens.size(), 6);
        assertEquals(tokens, expected);
    }

    @Test
    public void testWordsTabSeparatedParsedCorrectly() {
        TokenList tokens = s.splitFile(wordsTabSeparated);

        TokenList expected = new TokenList(TokenType.WHITESPACE);
        expected.add(new WhitespaceToken("hello"));
        expected.add(new WhitespaceToken("world"));
        expected.add(new WhitespaceToken("this"));
        expected.add(new WhitespaceToken("is"));
        expected.add(new WhitespaceToken("a"));
        expected.add(new WhitespaceToken("test"));

        assertNotNull(tokens);
        assertFalse(tokens.isEmpty());
        assertEquals(tokens.size(), 6);
        assertEquals(tokens, expected);
    }

    @Test
    public void testMultipleLinesParsedCorrectly() {
        TokenList tokens = s.splitFile(multipleLines);

        TokenList expected = new TokenList(TokenType.WHITESPACE);
        expected.add(new WhitespaceToken("hello"));
        expected.add(new WhitespaceToken("world"));
        expected.add(new WhitespaceToken("this"));
        expected.add(new WhitespaceToken("is"));
        expected.add(new WhitespaceToken("a"));
        expected.add(new WhitespaceToken("test"));

        assertNotNull(tokens);
        assertFalse(tokens.isEmpty());
        assertEquals(tokens.size(), 6);
        assertEquals(tokens, expected);
    }
}
