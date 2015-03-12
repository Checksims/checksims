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

import static org.junit.Assert.*;

/**
 * Tests for the FileCharTokenizer
 */
public class FileCharTokenizerTest {
    private static final String empty = "";
    private static final String oneWord = "hello";
    private static final String twoWords = "hello world";
    private static final String withTabs = "with\ttabs\t";
    private static FileCharTokenizer c;

    @BeforeClass
    public static void setUp() {
        c = FileCharTokenizer.getInstance();
    }

    @Test
    public void testEmptyReturnsEmpty() {
        TokenList results = c.splitFile(empty);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testHelloReturnsChars() {
        TokenList results = c.splitFile(oneWord);

        TokenList expected = new TokenList(TokenType.CHARACTER);
        expected.add(new ConcreteToken('h', TokenType.CHARACTER));
        expected.add(new ConcreteToken('e', TokenType.CHARACTER));
        expected.add(new ConcreteToken('l', TokenType.CHARACTER));
        expected.add(new ConcreteToken('l', TokenType.CHARACTER));
        expected.add(new ConcreteToken('o', TokenType.CHARACTER));

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(results.size(), 5);
        assertEquals(results, expected);
    }

    @Test
    public void testHelloWorldReturnsChars() {
        TokenList results = c.splitFile(twoWords);

        TokenList expected = new TokenList(TokenType.CHARACTER);
        expected.add(new ConcreteToken('h', TokenType.CHARACTER));
        expected.add(new ConcreteToken('e', TokenType.CHARACTER));
        expected.add(new ConcreteToken('l', TokenType.CHARACTER));
        expected.add(new ConcreteToken('l', TokenType.CHARACTER));
        expected.add(new ConcreteToken('o', TokenType.CHARACTER));
        expected.add(new ConcreteToken(' ', TokenType.CHARACTER));
        expected.add(new ConcreteToken('w', TokenType.CHARACTER));
        expected.add(new ConcreteToken('o', TokenType.CHARACTER));
        expected.add(new ConcreteToken('r', TokenType.CHARACTER));
        expected.add(new ConcreteToken('l', TokenType.CHARACTER));
        expected.add(new ConcreteToken('d', TokenType.CHARACTER));

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(results.size(), 11);
        assertEquals(results, expected);
    }

    @Test
    public void TestHandlesWhitespaceCorrectly() {
        TokenList results = c.splitFile(withTabs);

        TokenList expected = new TokenList(TokenType.CHARACTER);
        expected.add(new ConcreteToken('w', TokenType.CHARACTER));
        expected.add(new ConcreteToken('i', TokenType.CHARACTER));
        expected.add(new ConcreteToken('t', TokenType.CHARACTER));
        expected.add(new ConcreteToken('h', TokenType.CHARACTER));
        expected.add(new ConcreteToken('\t', TokenType.CHARACTER));
        expected.add(new ConcreteToken('t', TokenType.CHARACTER));
        expected.add(new ConcreteToken('a', TokenType.CHARACTER));
        expected.add(new ConcreteToken('b', TokenType.CHARACTER));
        expected.add(new ConcreteToken('s', TokenType.CHARACTER));
        expected.add(new ConcreteToken('\t', TokenType.CHARACTER));

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(results.size(), 10);
        assertEquals(results, expected);
    }
}
