package edu.wpi.checksims.token;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Basic tests on Tokens
 */
public class TokenTest {
    @Test
    public void TestCharacterTokenLowercase() {
        ConcreteToken upper = new ConcreteToken('H', TokenType.CHARACTER);
        ConcreteToken lower = new ConcreteToken('h', TokenType.CHARACTER);

        assertEquals(lower.lowerCase(), lower);
        assertEquals(upper.lowerCase(), lower);
    }

    @Test
    public void TestWhitespaceTokenLowercase() {
        ConcreteToken upper = new ConcreteToken("HELLO", TokenType.WHITESPACE);
        ConcreteToken lower = new ConcreteToken("hello", TokenType.WHITESPACE);

        assertEquals(lower.lowerCase(), lower);
        assertEquals(upper.lowerCase(), lower);
    }

    @Test
    public void TestLineTokenLowercase() {
        ConcreteToken upper = new ConcreteToken("Hello World", TokenType.LINE);
        ConcreteToken lower = new ConcreteToken("hello world", TokenType.LINE);

        assertEquals(lower.lowerCase(), lower);
        assertEquals(upper.lowerCase(), lower);
    }
}
