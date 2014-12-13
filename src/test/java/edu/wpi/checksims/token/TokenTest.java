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

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Basic tests on Tokens
 */
public class TokenTest {
    @Test
    public void TestCharacterTokenLowercase() {
        ConcreteToken upper = new ConcreteToken('H', TokenType.CHARACTER);
        ConcreteToken lower = new ConcreteToken('h', TokenType.CHARACTER);

        assertEquals(lower.lowerCase(), lower);
        assertEquals(upper.lowerCase(), lower);
    }

    @Test
    public void TestWhitespaceTokenLowercase() {
        ConcreteToken upper = new ConcreteToken("HELLO", TokenType.WHITESPACE);
        ConcreteToken lower = new ConcreteToken("hello", TokenType.WHITESPACE);

        assertEquals(lower.lowerCase(), lower);
        assertEquals(upper.lowerCase(), lower);
    }

    @Test
    public void TestLineTokenLowercase() {
        ConcreteToken upper = new ConcreteToken("Hello World", TokenType.LINE);
        ConcreteToken lower = new ConcreteToken("hello world", TokenType.LINE);

        assertEquals(lower.lowerCase(), lower);
        assertEquals(upper.lowerCase(), lower);
    }
}
