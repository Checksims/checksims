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

package edu.wpi.checksims.token.tokenizer;

import edu.wpi.checksims.token.ConcreteToken;
import edu.wpi.checksims.token.TokenList;
import edu.wpi.checksims.token.TokenType;
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
        expected.add(new ConcreteToken("hello", TokenType.WHITESPACE));

        assertNotNull(tokens);
        assertFalse(tokens.isEmpty());
        assertEquals(tokens.size(), 1);
        assertEquals(tokens, expected);
    }

    @Test
    public void testTwoWordsReturnsTwoWordTokens() {
        TokenList tokens = s.splitFile(twoWords);

        TokenList expected = new TokenList(TokenType.WHITESPACE);
        expected.add(new ConcreteToken("hello", TokenType.WHITESPACE));
        expected.add(new ConcreteToken("world", TokenType.WHITESPACE));

        assertNotNull(tokens);
        assertFalse(tokens.isEmpty());
        assertEquals(tokens.size(), 2);
        assertEquals(tokens, expected);
    }

    @Test
    public void testWordsSpaceSeparatedParsedCorrectly() {
        TokenList tokens = s.splitFile(wordsSpaceSeparated);

        TokenList expected = new TokenList(TokenType.WHITESPACE);
        expected.add(new ConcreteToken("hello", TokenType.WHITESPACE));
        expected.add(new ConcreteToken("world", TokenType.WHITESPACE));
        expected.add(new ConcreteToken("this", TokenType.WHITESPACE));
        expected.add(new ConcreteToken("is", TokenType.WHITESPACE));
        expected.add(new ConcreteToken("a", TokenType.WHITESPACE));
        expected.add(new ConcreteToken("test", TokenType.WHITESPACE));

        assertNotNull(tokens);
        assertFalse(tokens.isEmpty());
        assertEquals(tokens.size(), 6);
        assertEquals(tokens, expected);
    }

    @Test
    public void testWordsTabSeparatedParsedCorrectly() {
        TokenList tokens = s.splitFile(wordsTabSeparated);

        TokenList expected = new TokenList(TokenType.WHITESPACE);
        expected.add(new ConcreteToken("hello", TokenType.WHITESPACE));
        expected.add(new ConcreteToken("world", TokenType.WHITESPACE));
        expected.add(new ConcreteToken("this", TokenType.WHITESPACE));
        expected.add(new ConcreteToken("is", TokenType.WHITESPACE));
        expected.add(new ConcreteToken("a", TokenType.WHITESPACE));
        expected.add(new ConcreteToken("test", TokenType.WHITESPACE));

        assertNotNull(tokens);
        assertFalse(tokens.isEmpty());
        assertEquals(tokens.size(), 6);
        assertEquals(tokens, expected);
    }

    @Test
    public void testMultipleLinesParsedCorrectly() {
        TokenList tokens = s.splitFile(multipleLines);

        TokenList expected = new TokenList(TokenType.WHITESPACE);
        expected.add(new ConcreteToken("hello", TokenType.WHITESPACE));
        expected.add(new ConcreteToken("world", TokenType.WHITESPACE));
        expected.add(new ConcreteToken("this", TokenType.WHITESPACE));
        expected.add(new ConcreteToken("is", TokenType.WHITESPACE));
        expected.add(new ConcreteToken("a", TokenType.WHITESPACE));
        expected.add(new ConcreteToken("test", TokenType.WHITESPACE));

        assertNotNull(tokens);
        assertFalse(tokens.isEmpty());
        assertEquals(tokens.size(), 6);
        assertEquals(tokens, expected);
    }
}
