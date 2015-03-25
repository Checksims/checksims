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

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Basic tests on Tokens
 */
public class TokenTest {
    private static Token aValid;
    private static Token aInvalid;
    private static Token bValid;
    private static Token bInvalid;
    private static Token aValidTwo;
    private static Token aValidityIgnoring;
    private static Token aValidityEnsuring;
    private static Token aValidityEnsuringValid;

    @BeforeClass
    public static void setUp() {
        aValid = new ConcreteToken('a', TokenType.CHARACTER, true);
        aInvalid = new ConcreteToken('a', TokenType.CHARACTER, false);
        bValid = new ConcreteToken('b', TokenType.CHARACTER, true);
        bInvalid = new ConcreteToken('b', TokenType.CHARACTER, false);
        aValidTwo = new ConcreteToken('a', TokenType.CHARACTER, true);
        aValidityIgnoring = new ValidityIgnoringToken(aInvalid);
        aValidityEnsuring = new ValidityEnsuringToken(aInvalid);
        aValidityEnsuringValid = new ValidityEnsuringToken(aValid);
    }

    @Test
    public void TestNormalEqualityIsTransitive() {
        assertTrue(aValid.equals(aValidTwo));
        assertTrue(aValidTwo.equals(aValid));
    }

    @Test
    public void TestNormalEqualityRespectsTokenContent() {
        assertFalse(aValid.equals(bValid));
        assertFalse(aInvalid.equals(bInvalid));
    }

    @Test
    public void TestNormalTokenEqualityRespectsValidity() {
        assertFalse(aValid.equals(aInvalid));
        assertFalse(bInvalid.equals(bValid));
    }

    @Test
    public void TestValidityIgnoringEqualitySameContent() {
        assertTrue(aValidityIgnoring.equals(aValid));
        assertTrue(aValidityIgnoring.equals(aInvalid));
    }

    @Test
    public void TestValidityIgnoringEqualityDifferentContent() {
        assertFalse(aValidityIgnoring.equals(bValid));
        assertFalse(aValidityIgnoring.equals(bInvalid));
    }

    @Test
    public void TestValidityEnsuringEqualitySameContent() {
        assertTrue(aValidityEnsuringValid.equals(aValid));
        assertFalse(aValidityEnsuringValid.equals(aInvalid));
        assertFalse(aValidityEnsuring.equals(aInvalid));
        assertFalse(aValidityEnsuring.equals(aValid));
    }

    @Test
    public void TestValidityEnsuringEqualityDifferentContent() {
        assertFalse(aValidityEnsuring.equals(bValid));
        assertFalse(aValidityEnsuring.equals(bInvalid));
        assertFalse(aValidityEnsuringValid.equals(bValid));
        assertFalse(aValidityEnsuringValid.equals(bInvalid));
    }

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
