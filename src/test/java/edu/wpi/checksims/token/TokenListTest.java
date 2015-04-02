/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright (c) 2014 Matthew Heon and Dolan Murvihill
 */

package edu.wpi.checksims.token;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 * Tests for the TokenList class
 */
public class TokenListTest {
    private TokenList emptyCharacter;
    private TokenList oneElementWhitespace;
    private TokenList twoElementsWhitespace;
    private TokenList oneElementLine;
    private TokenList twoElementsLine;
    private TokenList oneElementCharacter;
    private TokenList twoElementsCharacter;
    private TokenList threeElementsCharacter;
    private TokenList twoElementsOneInvalidCharacter;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() {
        ConcreteToken a = new ConcreteToken('a', TokenType.CHARACTER);
        ConcreteToken b = new ConcreteToken('b', TokenType.CHARACTER);
        ConcreteToken c = new ConcreteToken('c', TokenType.CHARACTER);
        ConcreteToken w = new ConcreteToken("whitespace", TokenType.WHITESPACE);
        ConcreteToken x = new ConcreteToken("space", TokenType.WHITESPACE);
        ConcreteToken l = new ConcreteToken("line line line", TokenType.LINE);
        ConcreteToken m = new ConcreteToken("another line", TokenType.LINE);
        ConcreteToken inval = new ConcreteToken('i', TokenType.CHARACTER, false);

        emptyCharacter = new TokenList(TokenType.CHARACTER);

        oneElementCharacter = new TokenList(TokenType.CHARACTER);
        oneElementCharacter.add(a);

        oneElementWhitespace = new TokenList(TokenType.WHITESPACE);
        oneElementWhitespace.add(w);

        twoElementsWhitespace = new TokenList(TokenType.WHITESPACE);
        twoElementsWhitespace.add(w);
        twoElementsWhitespace.add(x);

        oneElementLine = new TokenList(TokenType.LINE);
        oneElementLine.add(l);

        twoElementsLine = new TokenList(TokenType.LINE);
        twoElementsLine.add(l);
        twoElementsLine.add(m);

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
        emptyCharacter.add(new ConcreteToken('a', TokenType.CHARACTER));

        assertNotNull(emptyCharacter);
        assertFalse(emptyCharacter.isEmpty());
        assertEquals(1, emptyCharacter.size());
        assertEquals(emptyCharacter, oneElementCharacter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void TestAddInvalidTypeToEmpty() {
        emptyCharacter.add(new ConcreteToken("hello", TokenType.WHITESPACE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void TestAddOtherInvalidTypeToEmpty() {
        emptyCharacter.add(new ConcreteToken("hello world", TokenType.LINE));
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

    @Test
    public void TestImmutableCopyOnEmptyIsStillEmpty() {
        TokenList immutableClone = TokenList.immutableCopy(emptyCharacter);

        assertNotNull(immutableClone);
        assertTrue(immutableClone.isEmpty());
        assertEquals(immutableClone.type, emptyCharacter.type);
    }

    @Test
    public void TestImmutableCopyDoesNotModify() {
        TokenList immutableClone = TokenList.immutableCopy(oneElementCharacter);

        assertNotNull(immutableClone);
        assertFalse(immutableClone.isEmpty());
        assertEquals(immutableClone.type, oneElementCharacter.type);
        assertEquals(immutableClone.size(), oneElementCharacter.size());
        assertEquals(immutableClone, oneElementCharacter);
    }

    @Test
    public void TestImmutableCopyOnMultiElement() {
        TokenList immutableClone = TokenList.immutableCopy(threeElementsCharacter);

        assertNotNull(immutableClone);
        assertFalse(immutableClone.isEmpty());
        assertEquals(immutableClone.type, threeElementsCharacter.type);
        assertEquals(immutableClone.size(), threeElementsCharacter.size());
        assertEquals(immutableClone, threeElementsCharacter);
    }

    @Test
    public void TestImmutableCopyPreservesValidity() {
        TokenList immutableClone = TokenList.immutableCopy(twoElementsOneInvalidCharacter);

        assertNotNull(immutableClone);
        assertFalse(immutableClone.isEmpty());
        assertEquals(immutableClone.type, twoElementsOneInvalidCharacter.type);
        assertEquals(immutableClone.size(), twoElementsOneInvalidCharacter.size());
        assertFalse(immutableClone.get(1).isValid());
        assertEquals(immutableClone, twoElementsOneInvalidCharacter);
    }

    @Test
    public void TestImmutableCopyPreventsModification() {
        expectedEx.expect(UnsupportedOperationException.class);

        TokenList immutableClone = TokenList.immutableCopy(emptyCharacter);

        immutableClone.add(new ConcreteToken('E', TokenType.CHARACTER));
    }

    @Test
    public void TestJoinEmptyList() {
        String joined = emptyCharacter.join(false);

        assertNotNull(joined);
        assertTrue(joined.isEmpty());
    }

    @Test
    public void TestJoinSingleCharacter() {
        String joined = oneElementCharacter.join(false);

        assertNotNull(joined);
        assertFalse(joined.isEmpty());
        assertEquals(joined, "a");
    }

    @Test
    public void TestJoinMultiElementCharacter() {
        String joinedTwo = twoElementsCharacter.join(false);

        assertNotNull(joinedTwo);
        assertFalse(joinedTwo.isEmpty());
        assertEquals(joinedTwo, "ab");

        String joinedThree = threeElementsCharacter.join(false);

        assertNotNull(joinedThree);
        assertFalse(joinedThree.isEmpty());
        assertEquals(joinedThree, "abc");
    }

    @Test
    public void TestJoinSameOnAllValid() {
        String joinedTrue = threeElementsCharacter.join(true);
        String joinedFalse = threeElementsCharacter.join(false);

        assertEquals(joinedFalse, joinedTrue);
    }

    @Test
    public void TestJoinIgnoresInvalidIfAsked() {
        String joined = twoElementsOneInvalidCharacter.join(true);

        assertNotNull(joined);
        assertFalse(joined.isEmpty());
        assertEquals(joined, "a");
    }

    @Test
    public void TestJoinInvalidDifferent() {
        String joinedIgnore = twoElementsOneInvalidCharacter.join(false);
        String joinedNoIgnore = twoElementsOneInvalidCharacter.join(true);

        assertNotEquals(joinedIgnore, joinedNoIgnore);
        assertEquals(joinedIgnore, "ai");
        assertEquals(joinedNoIgnore, "a");
    }

    @Test
    public void TestJoinWhitespaceDoesNotLeaveTrailingSpace() {
        String joined = oneElementWhitespace.join(false);

        assertNotNull(joined);
        assertFalse(joined.isEmpty());
        assertEquals(joined, "whitespace");
    }

    @Test
    public void TestJoinMultiEltWhitespace() {
        String joined = twoElementsWhitespace.join(false);

        assertNotNull(joined);
        assertFalse(joined.isEmpty());
        assertEquals(joined, "whitespace space");
    }

    @Test
    public void TestJoinLineAddsNewline() {
        String joined = oneElementLine.join(false);

        assertNotNull(joined);
        assertFalse(joined.isEmpty());
        assertEquals(joined, "line line line\n");
    }

    @Test
    public void TestJoinMultiEltLine() {
        String joined = twoElementsLine.join(false);

        assertNotNull(joined);
        assertFalse(joined.isEmpty());
        assertEquals(joined, "line line line\nanother line\n");
    }
}
