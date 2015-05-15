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
import net.lldp.checksims.testutil.TokenUtils;
import org.junit.Before;
import org.junit.Test;

import static net.lldp.checksims.testutil.TokenUtils.makeTokenListLine;
import static org.junit.Assert.*;

/**
 * Test for FileLineTokenizer, which is itself very simple, and thus not extensively tested
 */
public class LineTokenizerTest {
    private LineTokenizer l;

    @Before
    public void setUp() {
        l = LineTokenizer.getInstance();
    }

    @Test
    public void TestEmptyNotNullReturnsEmpty() {
        TokenList results = l.splitFile("");

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void TestOneStringReturnsString() {
        TokenList results = l.splitFile("hello");

        TokenList expected = TokenUtils.makeTokenListLine("hello");

        assertNotNull(results);
        assertEquals(results, expected);
    }

    @Test
    public void TestMultiLineReturnsTwoStrings() {
        TokenList results = l.splitFile("hello\nworld\n");
        TokenList expected = TokenUtils.makeTokenListLine("hello", "world");

        assertNotNull(results);
        assertEquals(results, expected);
    }

    @Test
    public void TestMultiLineNoTrailingReturnsTwoStrings() {
        TokenList results = l.splitFile("hello\nworld");
        TokenList expected = TokenUtils.makeTokenListLine("hello", "world");

        assertNotNull(results);
        assertEquals(results, expected);
    }

    @Test
    public void TestThreeLineSplit() {
        TokenList results = l.splitFile("A\nB\nC\n");
        TokenList expected = TokenUtils.makeTokenListLine("A", "B", "C");

        assertNotNull(results);
        assertEquals(results, expected);
    }

    @Test
    public void TestAdjacentNewlines() {
        TokenList results = l.splitFile("\n\n");

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void TestAdjacentNewlinesWithContent() {
        TokenList results = l.splitFile("\n\nHello\n\n\nWorld\n\n\n\n");
        TokenList expected = TokenUtils.makeTokenListLine("Hello", "World");

        assertNotNull(results);
        assertNotNull(expected);
    }
}
