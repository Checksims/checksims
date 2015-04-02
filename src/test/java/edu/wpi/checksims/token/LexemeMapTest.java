package edu.wpi.checksims.token;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for mapping lexemes to tokens
 */
public class LexemeMapTest {
    @Before
    public void setUp() {
        LexemeMap.resetMappings();
    }

    @Test
    public void TestCanAddAndRetrieveOneToken() {
        int lexeme = LexemeMap.getLexemeForToken("hello");
        Object token = LexemeMap.getTokenForLexeme(lexeme);

        assertNotNull(token);
        assertEquals(token, "hello");
    }

    @Test
    public void TestCanRepeatedlyRetrieveOneToken() {
        int lexeme = LexemeMap.getLexemeForToken("hello");
        Object token = LexemeMap.getTokenForLexeme(lexeme);
        Object token2 = LexemeMap.getTokenForLexeme(lexeme);
        Object token3 = LexemeMap.getTokenForLexeme(lexeme);

        assertNotNull(token);
        assertNotNull(token2);
        assertNotNull(token3);
        assertEquals(token, "hello");
        assertEquals(token2, token);
        assertEquals(token3, token2);
    }

    @Test
    public void TestAddOneTokenTwiceGivesSameLexeme() {
        int lexeme = LexemeMap.getLexemeForToken("hello");
        int lexeme2 = LexemeMap.getLexemeForToken("hello");

        assertEquals(lexeme, lexeme2);
    }

    @Test
    public void TestAddTwoTokensGivesDifferentLexemes() {
        int lexeme = LexemeMap.getLexemeForToken("hello");
        int lexeme2 = LexemeMap.getLexemeForToken("world");

        assertNotEquals(lexeme, lexeme2);
    }

    @Test
    public void TestAddAndRetrieveTwoTokens() {
        int lexeme = LexemeMap.getLexemeForToken("hello");
        int lexeme2 = LexemeMap.getLexemeForToken("world");

        Object token1 = LexemeMap.getTokenForLexeme(lexeme);
        Object token2 = LexemeMap.getTokenForLexeme(lexeme2);

        assertNotNull(token1);
        assertNotNull(token2);
        assertEquals(token1, "hello");
        assertEquals(token2, "world");
    }

    @Test
    public void TestRetrieveAfterAdd() {
        int lexeme = LexemeMap.getLexemeForToken("hello");

        Object token1 = LexemeMap.getTokenForLexeme(lexeme);

        LexemeMap.getLexemeForToken("world");

        Object token2 = LexemeMap.getTokenForLexeme(lexeme);

        assertNotNull(token1);
        assertNotNull(token2);
        assertEquals(token1, "hello");
        assertEquals(token1, token2);
    }
}
