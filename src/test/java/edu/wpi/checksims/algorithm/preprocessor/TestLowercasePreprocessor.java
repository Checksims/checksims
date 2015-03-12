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
        emptyListCharacter = new ConcreteSubmission("Empty Character List", "", empty);

        TokenList oneElementChar = new TokenList(TokenType.CHARACTER);
        oneElementChar.add(new ConcreteToken('A', TokenType.CHARACTER));
        oneElementListCharacter = new ConcreteSubmission("One element character list", "A", oneElementChar);

        TokenList oneElementCharLower = new TokenList(TokenType.CHARACTER);
        oneElementCharLower.add(new ConcreteToken('a', TokenType.CHARACTER));
        oneElementListCharacterIsLowerCase = new ConcreteSubmission("One element lowercase character list", "a", oneElementCharLower);

        TokenList oneElementWhitespace = new TokenList(TokenType.WHITESPACE);
        oneElementWhitespace.add(new ConcreteToken("HELLO", TokenType.WHITESPACE));
        oneElementListWhitespace = new ConcreteSubmission("One element whitespace list", "HELLO", oneElementWhitespace);

        TokenList oneElementWhitespaceLower = new TokenList(TokenType.WHITESPACE);
        oneElementWhitespaceLower.add(new ConcreteToken("hello", TokenType.WHITESPACE));
        oneElementListWhitespaceIsLowerCase = new ConcreteSubmission("One element lowercase whitespace list", "hello", oneElementWhitespaceLower);

        TokenList oneElementLine = new TokenList(TokenType.LINE);
        oneElementLine.add(new ConcreteToken("HELLO WORLD", TokenType.LINE));
        oneElementListLine = new ConcreteSubmission("One element line list", "HELLO WORLD", oneElementLine);

        TokenList oneElementLineLower = new TokenList(TokenType.LINE);
        oneElementLineLower.add(new ConcreteToken("hello world", TokenType.LINE));
        oneElementListLineIsLowerCase = new ConcreteSubmission("One element lowercase line list", "hello world", oneElementLineLower);

        TokenList twoElementMixedChar = new TokenList(TokenType.CHARACTER);
        twoElementMixedChar.add(new ConcreteToken('H', TokenType.CHARACTER));
        twoElementMixedChar.add(new ConcreteToken('e', TokenType.CHARACTER));
        twoElementListCharacter = new ConcreteSubmission("Two element character list", "He", twoElementMixedChar);

        TokenList threeElementMixedChar = new TokenList(TokenType.CHARACTER);
        threeElementMixedChar.add(new ConcreteToken('H', TokenType.CHARACTER));
        threeElementMixedChar.add(new ConcreteToken('e', TokenType.CHARACTER));
        threeElementMixedChar.add(new ConcreteToken('L', TokenType.CHARACTER));
        threeElementListCharacter = new ConcreteSubmission("Three element character list", "HeL", threeElementMixedChar);

        instance = LowercasePreprocessor.getInstance();
    }

    @Test
    public void TestLowercaseEmptyReturnsEmpty() {
        Submission result = instance.process(emptyListCharacter);

        assertNotNull(result);
        assertTrue(result.getContentAsTokens().isEmpty());
        assertEquals(result, emptyListCharacter);
    }

    @Test
    public void TestOneElementCharacterLowercase() {
        Submission result = instance.process(oneElementListCharacter);

        Submission expected = new ConcreteSubmission(oneElementListCharacter.getName(), oneElementListCharacter.getContentAsString(), oneElementListCharacterIsLowerCase.getContentAsTokens());

        assertNotNull(result);
        assertFalse(result.getContentAsTokens().isEmpty());
        assertEquals(result.getContentAsTokens().size(), 1);
        assertEquals(result.getContentAsTokens(), expected.getContentAsTokens());
        assertEquals(result, expected);
    }

    @Test
    public void TestOneElementCharacterLowercaseIdentity() {
        Submission result = instance.process(oneElementListCharacterIsLowerCase);

        assertNotNull(result);
        assertFalse(result.getContentAsTokens().isEmpty());
        assertEquals(result.getContentAsTokens().size(), 1);
        assertEquals(result.getContentAsTokens(), oneElementListCharacterIsLowerCase.getContentAsTokens());
        assertEquals(result, oneElementListCharacterIsLowerCase);
    }

    @Test
    public void TestOneElementWhitespaceLowercase() {
        Submission result = instance.process(oneElementListWhitespace);

        Submission expected = new ConcreteSubmission(oneElementListWhitespace.getName(), oneElementListWhitespace.getContentAsString(), oneElementListWhitespaceIsLowerCase.getContentAsTokens());

        assertNotNull(result);
        assertFalse(result.getContentAsTokens().isEmpty());
        assertEquals(result.getContentAsTokens().size(), 1);
        assertEquals(result.getContentAsTokens(), expected.getContentAsTokens());
        assertEquals(result, expected);
    }

    @Test
    public void TestOneElementWhitespaceLowercaseIdentity() {
        Submission result = instance.process(oneElementListWhitespaceIsLowerCase);

        assertNotNull(result);
        assertFalse(result.getContentAsTokens().isEmpty());
        assertEquals(result.getContentAsTokens().size(), 1);
        assertEquals(result.getContentAsTokens(), oneElementListWhitespaceIsLowerCase.getContentAsTokens());
        assertEquals(result, oneElementListWhitespaceIsLowerCase);
    }

    @Test
    public void TestOneElementLineLowercase() {
        Submission result = instance.process(oneElementListLine);

        Submission expected = new ConcreteSubmission(oneElementListLine.getName(), oneElementListLine.getContentAsString(), oneElementListLineIsLowerCase.getContentAsTokens());

        assertNotNull(result);
        assertFalse(result.getContentAsTokens().isEmpty());
        assertEquals(result.getContentAsTokens().size(), 1);
        assertEquals(result.getContentAsTokens(), expected.getContentAsTokens());
        assertEquals(result, expected);
    }

    @Test
    public void TestOneElementLineLowercaseIdentity() {
        Submission result = instance.process(oneElementListLineIsLowerCase);

        assertNotNull(result);
        assertFalse(result.getContentAsTokens().isEmpty());
        assertEquals(result.getContentAsTokens().size(), 1);
        assertEquals(result.getContentAsTokens(), oneElementListLineIsLowerCase.getContentAsTokens());
        assertEquals(result, oneElementListLineIsLowerCase);
    }

    @Test
    public void EnsureOrderingRemainsIdenticalTwoElements() {
        Submission result = instance.process(twoElementListCharacter);

        TokenList expectedList = new TokenList(TokenType.CHARACTER);
        expectedList.add(new ConcreteToken('h', TokenType.CHARACTER));
        expectedList.add(new ConcreteToken('e', TokenType.CHARACTER));
        Submission expected = new ConcreteSubmission(twoElementListCharacter.getName(), twoElementListCharacter.getContentAsString(), expectedList);

        assertNotNull(result);
        assertFalse(result.getContentAsTokens().isEmpty());
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
        Submission expected = new ConcreteSubmission(threeElementListCharacter.getName(), threeElementListCharacter.getContentAsString(), expectedList);

        assertNotNull(result);
        assertFalse(result.getContentAsTokens().isEmpty());
        assertEquals(result.getNumTokens(), 3);
        assertEquals(result, expected);
    }
}
