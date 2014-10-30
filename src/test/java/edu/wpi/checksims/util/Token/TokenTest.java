package edu.wpi.checksims.util.Token;

import edu.wpi.checksims.util.token.CharacterToken;
import edu.wpi.checksims.util.token.LineToken;
import edu.wpi.checksims.util.token.WhitespaceToken;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Basic tests on Tokens
 */
public class TokenTest {
    @Test
    public void TestCharacterTokenLowercase() {
        CharacterToken upper = new CharacterToken('H');
        CharacterToken lower = new CharacterToken('h');

        assertEquals(lower.lowerCase(), lower);
        assertEquals(upper.lowerCase(), lower);
    }

    @Test
    public void TestWhitespaceTokenLowercase() {
        WhitespaceToken upper = new WhitespaceToken("HELLO");
        WhitespaceToken lower = new WhitespaceToken("hello");

        assertEquals(lower.lowerCase(), lower);
        assertEquals(upper.lowerCase(), lower);
    }

    @Test
    public void TestLineTokenLowercase() {
        LineToken upper = new LineToken("Hello World");
        LineToken lower = new LineToken("hello world");

        assertEquals(lower.lowerCase(), lower);
        assertEquals(upper.lowerCase(), lower);
    }
}
