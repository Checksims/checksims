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
 * Copyright (c) 2014-2015 Matthew Heon and Dolan Murvihill
 */

package edu.wpi.checksims.token.tokenizer;

import edu.wpi.checksims.token.TokenList;
import org.junit.Before;
import org.junit.Test;

import static edu.wpi.checksims.testutil.TokenUtils.makeTokenListCharacter;
import static org.junit.Assert.*;

/**
 * Tests for the FileCharTokenizer
 */
public class FileCharTokenizerTest {
    private FileCharTokenizer c;

    @Before
    public void setUp() {
        c = FileCharTokenizer.getInstance();
    }

    @Test
    public void testEmptyReturnsEmpty() {
        TokenList results = c.splitFile("");

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testHelloReturnsChars() {
        TokenList results = c.splitFile("hello");
        TokenList expected = makeTokenListCharacter('h', 'e', 'l', 'l', 'o');

        assertNotNull(results);
        assertEquals(expected, results);
    }

    @Test
    public void testHelloWorldReturnsChars() {
        TokenList results = c.splitFile("hello world");
        TokenList expected = makeTokenListCharacter('h', 'e', 'l', 'l', 'o', ' ', 'w', 'o', 'r', 'l', 'd');

        assertNotNull(results);
        assertEquals(results, expected);
    }

    @Test
    public void TestHandlesWhitespaceCorrectly() {
        TokenList results = c.splitFile("with\ttabs\t");
        TokenList expected = makeTokenListCharacter('w', 'i', 't', 'h', '\t', 't', 'a', 'b', 's', '\t');

        assertNotNull(results);
        assertEquals(results, expected);
    }

    @Test
    public void TestHandlesNewlinesCorrectly() {
        TokenList results = c.splitFile("with\nnewlines\n");
        TokenList expected = makeTokenListCharacter('w', 'i', 't', 'h', '\n', 'n', 'e', 'w', 'l', 'i', 'n', 'e', 's', '\n');

        assertNotNull(results);
        assertEquals(results, expected);
    }
}
