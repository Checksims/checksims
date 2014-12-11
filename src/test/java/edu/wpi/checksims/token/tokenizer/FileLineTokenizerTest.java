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
 * Test for FileLineTokenizer, which is itself very simple, and thus not extensively tested
 */
public class FileLineTokenizerTest {
    private static List<String> empty;
    private static List<String> oneString;
    private static List<String> twoStrings;
    private static FileLineTokenizer l;

    @BeforeClass
    public static void setUp() {
        empty = new LinkedList<>();

        oneString = new LinkedList<>();
        oneString.add("hello");

        twoStrings = new LinkedList<>();
        twoStrings.add("hello");
        twoStrings.add("world");

        l = FileLineTokenizer.getInstance();
    }

    @Test
    public void TestEmptyReturnsEmpty() {
        TokenList results = l.splitFile(empty);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void TestOneStringReturnsString() {
        TokenList results = l.splitFile(oneString);

        TokenList expected = new TokenList(TokenType.LINE);
        expected.add(new ConcreteToken("hello", TokenType.LINE));

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(results.size(), 1);
        assertEquals(results, expected);
    }

    @Test
    public void TestTwoStringsReturnsTwoStrings() {
        TokenList results = l.splitFile(twoStrings);

        TokenList expected = new TokenList(TokenType.LINE);
        expected.add(new ConcreteToken("hello", TokenType.LINE));
        expected.add(new ConcreteToken("world", TokenType.LINE));

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(results.size(), 2);
        assertEquals(results, expected);
    }
}
