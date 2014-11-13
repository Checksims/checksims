package edu.wpi.checksims.util.token;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for the TokenList class
 */
public class TokenListTest {
    private TokenList emptyCharacter;
    private TokenList oneElementWhitespace;
    private TokenList oneElementLine;
    private TokenList oneElementCharacter;
    private TokenList twoElementsCharacter;
    private TokenList threeElementsCharacter;
    private TokenList twoElementsOneInvalidCharacter;

    @Before
    public void setUp() {
        CharacterToken a = new CharacterToken('a');
        CharacterToken b = new CharacterToken('b');
        CharacterToken c = new CharacterToken('c');
        WhitespaceToken w = new WhitespaceToken("whitespace");
        LineToken l = new LineToken("line line line");
        CharacterToken inval = new CharacterToken('i', false);

        emptyCharacter = new TokenList(TokenType.CHARACTER);

        oneElementCharacter = new TokenList(TokenType.CHARACTER);
        oneElementCharacter.add(a);

        oneElementWhitespace = new TokenList(TokenType.WHITESPACE);
        oneElementWhitespace.add(w);

        oneElementLine = new TokenList(TokenType.LINE);
        oneElementLine.add(l);

        twoElementsCharacter = new TokenList(TokenType.CHARACTER);
        twoElementsCharacter.add(a);
        twoElementsCharacter.add(b);

        threeElementsCharacter = new TokenList(TokenType.CHARACTER);
        threeElementsCharacter.add(a);
        threeElementsCharacter.add(b);
        threeElementsCharacter.add(c);

        twoElementsOneInvalidCharacter = new TokenList(TokenType.CHARACTER);
        twoElementsOneInvalidCharacter.add(a);
        twoElementsOneInvalidCharacter.add(inval);
    }

    @Test
    public void TestAddValidTypeToEmpty() {
        emptyCharacter.add(new CharacterToken('a'));

        assertNotNull(emptyCharacter);
        assertFalse(emptyCharacter.isEmpty());
        assertEquals(1, emptyCharacter.size());
        assertEquals(emptyCharacter, oneElementCharacter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void TestAddInvalidTypeToEmpty() {
        emptyCharacter.add(new WhitespaceToken("hello"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void TestAddOtherInvalidTypeToEmpty() {
        emptyCharacter.add(new LineToken("hello world"));
    }

    @Test
    public void TestDifferentTypeEmptyListsAreNotEqual() {
        TokenList emptyWhitespace = new TokenList(TokenType.WHITESPACE);

        assertNotEquals(emptyWhitespace, emptyCharacter);
    }

    @Test
    public void TestCloneEmpty() {
        TokenList cloned = TokenList.cloneTokenList(emptyCharacter);

        assertNotNull(cloned);
        assertTrue(cloned.isEmpty());
        assertEquals(cloned.type, emptyCharacter.type);
        assertEquals(cloned, emptyCharacter);
    }

    @Test
    public void TestCloneOneElement() {
        TokenList clonedChar = TokenList.cloneTokenList(oneElementCharacter);
        TokenList clonedWhitespace = TokenList.cloneTokenList(oneElementWhitespace);
        TokenList clonedLine = TokenList.cloneTokenList(oneElementLine);

        assertNotNull(clonedChar);
        assertFalse(clonedChar.isEmpty());
        assertEquals(clonedChar.type, TokenType.CHARACTER);
        assertEquals(clonedChar, oneElementCharacter);

        assertNotNull(clonedWhitespace);
        assertFalse(clonedWhitespace.isEmpty());
        assertEquals(clonedWhitespace.type, TokenType.WHITESPACE);
        assertEquals(clonedWhitespace, oneElementWhitespace);

        assertNotNull(clonedLine);
        assertFalse(clonedLine.isEmpty());
        assertEquals(clonedLine.type, TokenType.LINE);
        assertEquals(clonedLine, oneElementLine);
    }

    @Test
    public void TestCloneOrderTwoAndThreeElements() {
        TokenList clonedTwoElt = TokenList.cloneTokenList(twoElementsCharacter);
        TokenList clonedThreeElt = TokenList.cloneTokenList(threeElementsCharacter);

        assertNotNull(clonedTwoElt);
        assertFalse(clonedTwoElt.isEmpty());
        assertEquals(clonedTwoElt.size(), 2);
        assertEquals(clonedTwoElt, twoElementsCharacter);

        assertNotNull(clonedThreeElt);
        assertFalse(clonedThreeElt.isEmpty());
        assertEquals(clonedThreeElt.size(), 3);
        assertEquals(clonedThreeElt, threeElementsCharacter);
    }

    @Test
    public void TestClonePreservesInvalid() {
        TokenList clonedInvalid = TokenList.cloneTokenList(twoElementsOneInvalidCharacter);

        assertNotNull(clonedInvalid);
        assertFalse(clonedInvalid.isEmpty());
        assertEquals(clonedInvalid.size(), 2);
        assertFalse(clonedInvalid.get(1).isValid());

        // Can't verify the entire lists are equal, given that Token equals() is false if one tokenization is invalid
        assertEquals(clonedInvalid.get(0), twoElementsOneInvalidCharacter.get(0));
        assertEquals(clonedInvalid.get(1).getType(), twoElementsOneInvalidCharacter.get(1).getType());
        assertEquals(clonedInvalid.get(1).getToken(), twoElementsOneInvalidCharacter.get(1).getToken());
    }

    @Test
    public void TestCloneIsDeep() {
        TokenList clone = TokenList.cloneTokenList(oneElementCharacter);

        assertNotNull(clone);
        assertFalse(clone.isEmpty());
        assertEquals(clone.size(), 1);
        assertEquals(clone, oneElementCharacter);

        clone.get(0).setValid(!clone.get(0).isValid());

        assertFalse(clone.equals(oneElementCharacter));
    }
}
