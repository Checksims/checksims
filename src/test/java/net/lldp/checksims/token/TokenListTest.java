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
 * Copyright (c) 2014-2015 Nicholas DeMarinis, Matthew Heon, and Dolan Murvihill
 */

package net.lldp.checksims.token;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static net.lldp.checksims.testutil.TokenUtils.*;
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
        Token a = makeCharToken('a');
        Token b = makeCharToken('b');
        Token c = makeCharToken('c');
        Token w = makeWhitespaceToken("whitespace");
        Token x = makeWhitespaceToken("space");
        Token l = makeLineToken("line line line");
        Token m = makeLineToken("another line");
        Token inval = new ConcreteToken('i', TokenType.CHARACTER, false);

        emptyCharacter = new TokenList(TokenType.CHARACTER);
        oneElementWhitespace = makeTokenListWhitespace(w);
        twoElementsWhitespace = makeTokenListWhitespace(w, x);
        oneElementLine = makeTokenListLine(l);
        twoElementsLine = makeTokenListLine(l, m);
        oneElementCharacter = makeTokenListCharacter(a);
        twoElementsCharacter = makeTokenListCharacter(a, b);
        threeElementsCharacter = makeTokenListCharacter(a, b, c);
        twoElementsOneInvalidCharacter = makeTokenListCharacter(a, inval);
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
        assertEquals(emptyCharacter, cloned);
    }

    @Test
    public void TestCloneOneElementCharacter() {
        TokenList cloned = TokenList.cloneTokenList(oneElementCharacter);

        assertNotNull(cloned);
        assertEquals(oneElementCharacter, cloned);
    }

    @Test
    public void TestCloneOneElementWhitespace() {
        TokenList cloned = TokenList.cloneTokenList(oneElementWhitespace);

        assertNotNull(cloned);
        assertEquals(oneElementWhitespace, cloned);
    }

    @Test
    public void TestCloneOneElementLine() {
        TokenList cloned = TokenList.cloneTokenList(oneElementLine);

        assertNotNull(cloned);
        assertEquals(oneElementLine, cloned);
    }

    @Test
    public void TestCloneTwoElements() {
        TokenList cloned = TokenList.cloneTokenList(twoElementsCharacter);

        assertNotNull(cloned);
        assertEquals(twoElementsCharacter, cloned);
    }

    @Test
    public void TestCloneThreeElements() {
        TokenList cloned = TokenList.cloneTokenList(threeElementsCharacter);

        assertNotNull(cloned);
        assertEquals(threeElementsCharacter, cloned);
    }

    @Test
    public void TestClonePreservesInvalid() {
        TokenList clonedInvalid = TokenList.cloneTokenList(twoElementsOneInvalidCharacter);

        assertNotNull(clonedInvalid);
        assertEquals(twoElementsOneInvalidCharacter, clonedInvalid);
    }

    @Test
    public void TestCloneIsDeep() {
        TokenList clone = TokenList.cloneTokenList(oneElementCharacter);

        assertNotNull(clone);
        assertEquals(oneElementCharacter, clone);

        clone.get(0).setValid(!clone.get(0).isValid());

        assertNotEquals(oneElementCharacter, clone);
    }

    @Test
    public void TestImmutableCopyOnEmptyIsStillEmpty() {
        TokenList immutableClone = TokenList.immutableCopy(emptyCharacter);

        assertNotNull(immutableClone);
        assertEquals(emptyCharacter, immutableClone);
    }

    @Test
    public void TestImmutableCopyDoesNotModify() {
        TokenList immutableClone = TokenList.immutableCopy(oneElementCharacter);

        assertNotNull(immutableClone);
        assertEquals(oneElementCharacter, immutableClone);
    }

    @Test
    public void TestImmutableCopyOnMultiElement() {
        TokenList immutableClone = TokenList.immutableCopy(threeElementsCharacter);

        assertNotNull(immutableClone);
        assertEquals(threeElementsCharacter, immutableClone);
    }

    @Test
    public void TestImmutableCopyPreservesValidity() {
        TokenList immutableClone = TokenList.immutableCopy(twoElementsOneInvalidCharacter);

        assertNotNull(immutableClone);
        assertEquals(twoElementsOneInvalidCharacter, immutableClone);
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
        assertEquals("a", joined);
    }

    @Test
    public void TestJoinTwoElementCharacter() {
        String joined = twoElementsCharacter.join(false);

        assertNotNull(joined);
        assertEquals("ab", joined);
    }

    @Test
    public void TestJoinThreeElementCharacter() {
        String joined = threeElementsCharacter.join(false);

        assertNotNull(joined);
        assertEquals("abc", joined);
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
        assertEquals("a", joined);
    }

    @Test
    public void TestJoinInvalidDifferent() {
        String joinedIgnore = twoElementsOneInvalidCharacter.join(false);
        String joinedNoIgnore = twoElementsOneInvalidCharacter.join(true);

        assertNotEquals(joinedIgnore, joinedNoIgnore);
        assertEquals("ai", joinedIgnore);
        assertEquals("a", joinedNoIgnore);
    }

    @Test
    public void TestJoinWhitespaceDoesNotLeaveTrailingSpace() {
        String joined = oneElementWhitespace.join(false);

        assertNotNull(joined);
        assertEquals("whitespace", joined);
    }

    @Test
    public void TestJoinMultiEltWhitespace() {
        String joined = twoElementsWhitespace.join(false);

        assertNotNull(joined);
        assertEquals("whitespace space", joined);
    }

    @Test
    public void TestJoinLineAddsNewline() {
        String joined = oneElementLine.join(false);

        assertNotNull(joined);
        assertEquals("line line line\n", joined);
    }

    @Test
    public void TestJoinMultiEltLine() {
        String joined = twoElementsLine.join(false);

        assertNotNull(joined);
        assertEquals("line line line\nanother line\n", joined);
    }

    @Test
    public void testNumValid() {
        assertEquals(3, threeElementsCharacter.numValid());
    }

    @Test
    public void testNumValidAgain() {
        assertEquals(2, twoElementsWhitespace.numValid());
    }

    @Test
    public void testNumValidAfterChange() {
        threeElementsCharacter.get(1).setValid(false);
        assertEquals(2, threeElementsCharacter.numValid());
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testImmutableCopyContainsImmutableTokens() {
        final TokenList t = TokenList.immutableCopy(threeElementsCharacter);
        t.get(1).setValid(false);
    }
}
