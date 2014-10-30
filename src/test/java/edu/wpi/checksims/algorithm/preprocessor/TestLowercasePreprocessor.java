package edu.wpi.checksims.algorithm.preprocessor;

import edu.wpi.checksims.Submission;
import edu.wpi.checksims.util.token.*;
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
        emptyListCharacter = new Submission("Empty Character List", empty);

        TokenList oneElementChar = new TokenList(TokenType.CHARACTER);
        oneElementChar.add(new CharacterToken('A'));
        oneElementListCharacter = new Submission("One element character list", oneElementChar);

        TokenList oneElementCharLower = new TokenList(TokenType.CHARACTER);
        oneElementCharLower.add(new CharacterToken('a'));
        oneElementListCharacterIsLowerCase = new Submission("One element lowercase character list", oneElementCharLower);

        TokenList oneElementWhitespace = new TokenList(TokenType.WHITESPACE);
        oneElementWhitespace.add(new WhitespaceToken("HELLO"));
        oneElementListWhitespace = new Submission("One element whitespace list", oneElementWhitespace);

        TokenList oneElementWhitespaceLower = new TokenList(TokenType.WHITESPACE);
        oneElementWhitespaceLower.add(new WhitespaceToken("hello"));
        oneElementListWhitespaceIsLowerCase = new Submission("One element lowercase whitespace list", oneElementWhitespaceLower);

        TokenList oneElementLine = new TokenList(TokenType.LINE);
        oneElementLine.add(new LineToken("HELLO WORLD"));
        oneElementListLine = new Submission("One element line list", oneElementLine);

        TokenList oneElementLineLower = new TokenList(TokenType.LINE);
        oneElementLineLower.add(new LineToken("hello world"));
        oneElementListLineIsLowerCase = new Submission("One element lowercase line list", oneElementLineLower);

        TokenList twoElementMixedChar = new TokenList(TokenType.CHARACTER);
        twoElementMixedChar.add(new CharacterToken('H'));
        twoElementMixedChar.add(new CharacterToken('e'));
        twoElementListCharacter = new Submission("Two element character list", twoElementMixedChar);

        TokenList threeElementMixedChar = new TokenList(TokenType.CHARACTER);
        threeElementMixedChar.add(new CharacterToken('H'));
        threeElementMixedChar.add(new CharacterToken('e'));
        threeElementMixedChar.add(new CharacterToken('L'));
        threeElementListCharacter = new Submission("Three element character list", threeElementMixedChar);

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

        Submission expected = new Submission(oneElementListCharacter.getName(), oneElementListCharacterIsLowerCase.getTokenList());

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

        Submission expected = new Submission(oneElementListWhitespace.getName(), oneElementListWhitespaceIsLowerCase.getTokenList());

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

        Submission expected = new Submission(oneElementListLine.getName(), oneElementListLineIsLowerCase.getTokenList());

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
        expectedList.add(new CharacterToken('h'));
        expectedList.add(new CharacterToken('e'));
        Submission expected = new Submission(twoElementListCharacter.getName(), expectedList);

        assertNotNull(result);
        assertFalse(result.getTokenList().isEmpty());
        assertEquals(result.getNumTokens(), 2);
        assertEquals(result, expected);
    }

    @Test
    public void EnsureOrderingRemainsIdenticalThreeElements() {
        Submission result = instance.process(threeElementListCharacter);

        TokenList expectedList = new TokenList(TokenType.CHARACTER);
        expectedList.add(new CharacterToken('h'));
        expectedList.add(new CharacterToken('e'));
        expectedList.add(new CharacterToken('l'));
        Submission expected = new Submission(threeElementListCharacter.getName(), expectedList);

        assertNotNull(result);
        assertFalse(result.getTokenList().isEmpty());
        assertEquals(result.getNumTokens(), 3);
        assertEquals(result, expected);
    }
}
