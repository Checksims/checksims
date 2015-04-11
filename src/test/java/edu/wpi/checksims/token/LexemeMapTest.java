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

package edu.wpi.checksims.token;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for mapping lexemes to tokens
 */
public class LexemeMapTest {
    @Before
    public void setUp() {
        LexemeMap.resetMappings();
    }

    @Test
    public void TestCanAddAndRetrieveOneToken() {
        int lexeme = LexemeMap.getLexemeForToken("hello");
        Object token = LexemeMap.getTokenForLexeme(lexeme);

        assertNotNull(token);
        assertEquals("hello", token);
    }

    @Test
    public void TestCanRepeatedlyRetrieveOneToken() {
        int lexeme = LexemeMap.getLexemeForToken("hello");
        Object token = LexemeMap.getTokenForLexeme(lexeme);
        Object token2 = LexemeMap.getTokenForLexeme(lexeme);
        Object token3 = LexemeMap.getTokenForLexeme(lexeme);

        assertNotNull(token);
        assertNotNull(token2);
        assertNotNull(token3);
        assertEquals("hello", token);
        assertEquals("hello", token2);
        assertEquals("hello", token3);
    }

    @Test
    public void TestAddOneTokenTwiceGivesSameLexeme() {
        int lexeme = LexemeMap.getLexemeForToken("hello");
        int lexeme2 = LexemeMap.getLexemeForToken("hello");

        assertEquals(lexeme, lexeme2);
    }

    @Test
    public void TestAddTwoTokensGivesDifferentLexemes() {
        int lexeme = LexemeMap.getLexemeForToken("hello");
        int lexeme2 = LexemeMap.getLexemeForToken("world");

        assertNotEquals(lexeme, lexeme2);
    }

    @Test
    public void TestAddAndRetrieveTwoTokens() {
        int lexeme = LexemeMap.getLexemeForToken("hello");
        int lexeme2 = LexemeMap.getLexemeForToken("world");

        Object token1 = LexemeMap.getTokenForLexeme(lexeme);
        Object token2 = LexemeMap.getTokenForLexeme(lexeme2);

        assertNotNull(token1);
        assertNotNull(token2);
        assertEquals("hello", token1);
        assertEquals("world", token2);
    }

    @Test
    public void TestRetrieveAfterAdd() {
        int lexeme = LexemeMap.getLexemeForToken("hello");

        Object token1 = LexemeMap.getTokenForLexeme(lexeme);

        LexemeMap.getLexemeForToken("world");

        Object token2 = LexemeMap.getTokenForLexeme(lexeme);

        assertNotNull(token1);
        assertNotNull(token2);
        assertEquals("hello", token1);
        assertEquals("hello", token2);
    }
}
