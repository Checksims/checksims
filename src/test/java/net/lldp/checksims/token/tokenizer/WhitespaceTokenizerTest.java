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

package net.lldp.checksims.token.tokenizer;

import net.lldp.checksims.token.TokenList;
import org.junit.Before;
import org.junit.Test;

import static net.lldp.checksims.testutil.TokenUtils.makeTokenListWhitespace;
import static org.junit.Assert.*;

/**
 * Test that we can split files by whitespace
 */
public class WhitespaceTokenizerTest {
    private WhitespaceTokenizer s;

    @Before
    public void setUp() {
        s = WhitespaceTokenizer.getInstance();
    }

    @Test
    public void testEmptyReturnsEmpty() {
        TokenList tokens = s.splitString("");

        assertNotNull(tokens);
        assertTrue(tokens.isEmpty());
    }

    @Test
    public void testOneWordReturnsWordToken() {
        TokenList tokens = s.splitString("hello");
        TokenList expected = makeTokenListWhitespace("hello");

        assertNotNull(tokens);
        assertEquals(tokens, expected);
    }

    @Test
    public void testTwoWordsReturnsTwoWordTokens() {
        TokenList tokens = s.splitString("hello world");
        TokenList expected = makeTokenListWhitespace("hello", "world");

        assertNotNull(tokens);
        assertEquals(tokens, expected);
    }

    @Test
    public void testWordsSpaceSeparatedParsedCorrectly() {
        TokenList tokens = s.splitString("    hello     world       this is   a test     ");
        TokenList expected = makeTokenListWhitespace("hello", "world", "this", "is", "a", "test");

        assertNotNull(tokens);
        assertEquals(tokens, expected);
    }

    @Test
    public void testWordsTabSeparatedParsedCorrectly() {
        TokenList tokens = s.splitString("hello\tworld\t\tthis \t \t is a test\t");
        TokenList expected = makeTokenListWhitespace("hello", "world", "this", "is", "a", "test");

        assertNotNull(tokens);
        assertEquals(tokens, expected);
    }
}
