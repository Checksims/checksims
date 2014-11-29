package edu.wpi.checksims.algorithm.preprocessor;

import edu.wpi.checksims.submission.ConcreteSubmission;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.token.*;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for the LowercasePreprocessor
 */
public class TestLowercasePreprocessor {
    private static Submission emptyListCharacter;
    private static Submission oneElementListCharacter;
    private static Submission oneElementListCharacterIsLowerCase;
    private static Submission oneElementListWhitespace;
    private static Submission oneElementListWhitespaceIsLowerCase;
    private static Submission oneElementListLine;
    private static Submission oneElementListLineIsLowerCase;
    private static Submission twoElementListCharacter;
    private static Submission threeElementListCharacter;
    private static LowercasePreprocessor instance;

    @BeforeClass
    public static void setUp() {
        TokenList empty = new TokenList(TokenType.CHARACTER);
        emptyListCharacter = new ConcreteSubmission("Empty Character List", empty);

        TokenList oneElementChar = new TokenList(TokenType.CHARACTER);
        oneElementChar.add(new ConcreteToken('A', TokenType.CHARACTER));
        oneElementListCharacter = new ConcreteSubmission("One element character list", oneElementChar);

        TokenList oneElementCharLower = new TokenList(TokenType.CHARACTER);
        oneElementCharLower.add(new ConcreteToken('a', TokenType.CHARACTER));
        oneElementListCharacterIsLowerCase = new ConcreteSubmission("One element lowercase character list", oneElementCharLower);

        TokenList oneElementWhitespace = new TokenList(TokenType.WHITESPACE);
        oneElementWhitespace.add(new ConcreteToken("HELLO", TokenType.WHITESPACE));
        oneElementListWhitespace = new ConcreteSubmission("One element whitespace list", oneElementWhitespace);

        TokenList oneElementWhitespaceLower = new TokenList(TokenType.WHITESPACE);
        oneElementWhitespaceLower.add(new ConcreteToken("hello", TokenType.WHITESPACE));
        oneElementListWhitespaceIsLowerCase = new ConcreteSubmission("One element lowercase whitespace list", oneElementWhitespaceLower);

        TokenList oneElementLine = new TokenList(TokenType.LINE);
        oneElementLine.add(new ConcreteToken("HELLO WORLD", TokenType.LINE));
        oneElementListLine = new ConcreteSubmission("One element line list", oneElementLine);

        TokenList oneElementLineLower = new TokenList(TokenType.LINE);
        oneElementLineLower.add(new ConcreteToken("hello world", TokenType.LINE));
        oneElementListLineIsLowerCase = new ConcreteSubmission("One element lowercase line list", oneElementLineLower);

        TokenList twoElementMixedChar = new TokenList(TokenType.CHARACTER);
        twoElementMixedChar.add(new ConcreteToken('H', TokenType.CHARACTER));
        twoElementMixedChar.add(new ConcreteToken('e', TokenType.CHARACTER));
        twoElementListCharacter = new ConcreteSubmission("Two element character list", twoElementMixedChar);

        TokenList threeElementMixedChar = new TokenList(TokenType.CHARACTER);
        threeElementMixedChar.add(new ConcreteToken('H', TokenType.CHARACTER));
        threeElementMixedChar.add(new ConcreteToken('e', TokenType.CHARACTER));
        threeElementMixedChar.add(new ConcreteToken('L', TokenType.CHARACTER));
        threeElementListCharacter = new ConcreteSubmission("Three element character list", threeElementMixedChar);

        instance = LowercasePreprocessor.getInstance();
    }

    @Test
    public void TestLowercaseEmptyReturnsEmpty() {
        Submission result = instance.process(emptyListCharacter);

        assertNotNull(result);
        assertTrue(result.getTokenList().isEmpty());
        assertEquals(result, emptyListCharacter);
    }

    @Test
    public void TestOneElementCharacterLowercase() {
        Submission result = instance.process(oneElementListCharacter);

        Submission expected = new ConcreteSubmission(oneElementListCharacter.getName(), oneElementListCharacterIsLowerCase.getTokenList());

        assertNotNull(result);
        assertFalse(result.getTokenList().isEmpty());
        assertEquals(result.getTokenList().size(), 1);
        assertEquals(result.getTokenList(), expected.getTokenList());
        assertEquals(result, expected);
    }

    @Test
    public void TestOneElementCharacterLowercaseIdentity() {
        Submission result = instance.process(oneElementListCharacterIsLowerCase);

        assertNotNull(result);
        assertFalse(result.getTokenList().isEmpty());
        assertEquals(result.getTokenList().size(), 1);
        assertEquals(result.getTokenList(), oneElementListCharacterIsLowerCase.getTokenList());
        assertEquals(result, oneElementListCharacterIsLowerCase);
    }

    @Test
    public void TestOneElementWhitespaceLowercase() {
        Submission result = instance.process(oneElementListWhitespace);

        Submission expected = new ConcreteSubmission(oneElementListWhitespace.getName(), oneElementListWhitespaceIsLowerCase.getTokenList());

        assertNotNull(result);
        assertFalse(result.getTokenList().isEmpty());
        assertEquals(result.getTokenList().size(), 1);
        assertEquals(result.getTokenList(), expected.getTokenList());
        assertEquals(result, expected);
    }

    @Test
    public void TestOneElementWhitespaceLowercaseIdentity() {
        Submission result = instance.process(oneElementListWhitespaceIsLowerCase);

        assertNotNull(result);
        assertFalse(result.getTokenList().isEmpty());
        assertEquals(result.getTokenList().size(), 1);
        assertEquals(result.getTokenList(), oneElementListWhitespaceIsLowerCase.getTokenList());
        assertEquals(result, oneElementListWhitespaceIsLowerCase);
    }

    @Test
    public void TestOneElementLineLowercase() {
        Submission result = instance.process(oneElementListLine);

        Submission expected = new ConcreteSubmission(oneElementListLine.getName(), oneElementListLineIsLowerCase.getTokenList());

        assertNotNull(result);
        assertFalse(result.getTokenList().isEmpty());
        assertEquals(result.getTokenList().size(), 1);
        assertEquals(result.getTokenList(), expected.getTokenList());
        assertEquals(result, expected);
    }

    @Test
    public void TestOneElementLineLowercaseIdentity() {
        Submission result = instance.process(oneElementListLineIsLowerCase);

        assertNotNull(result);
        assertFalse(result.getTokenList().isEmpty());
        assertEquals(result.getTokenList().size(), 1);
        assertEquals(result.getTokenList(), oneElementListLineIsLowerCase.getTokenList());
        assertEquals(result, oneElementListLineIsLowerCase);
    }

    @Test
    public void EnsureOrderingRemainsIdenticalTwoElements() {
        Submission result = instance.process(twoElementListCharacter);

        TokenList expectedList = new TokenList(TokenType.CHARACTER);
        expectedList.add(new ConcreteToken('h', TokenType.CHARACTER));
        expectedList.add(new ConcreteToken('e', TokenType.CHARACTER));
        Submission expected = new ConcreteSubmission(twoElementListCharacter.getName(), expectedList);

        assertNotNull(result);
        assertFalse(result.getTokenList().isEmpty());
        assertEquals(result.getNumTokens(), 2);
        assertEquals(result, expected);
    }

    @Test
    public void EnsureOrderingRemainsIdenticalThreeElements() {
        Submission result = instance.process(threeElementListCharacter);

        TokenList expectedList = new TokenList(TokenType.CHARACTER);
        expectedList.add(new ConcreteToken('h', TokenType.CHARACTER));
        expectedList.add(new ConcreteToken('e', TokenType.CHARACTER));
        expectedList.add(new ConcreteToken('l', TokenType.CHARACTER));
        Submission expected = new ConcreteSubmission(threeElementListCharacter.getName(), expectedList);

        assertNotNull(result);
        assertFalse(result.getTokenList().isEmpty());
        assertEquals(result.getNumTokens(), 3);
        assertEquals(result, expected);
    }
}
