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

package net.lldp.checksims.token;

import net.lldp.checksims.testutil.TokenUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Basic tests on Tokens
 *
 * TODO tests on Whitespace and Line tokens
 * TODO add tests for Clone
 */
public class TokenTest {
    private Token aValid;
    private Token aInvalid;
    private Token bValid;
    private Token bInvalid;
    private Token aValidTwo;
    private Token aValidityIgnoring;
    private Token aValidityEnsuring;
    private Token aValidityEnsuringValid;
    private Token aImmutable;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() {
        aValid = TokenUtils.makeCharToken('a');
        aInvalid = new ConcreteToken('a', TokenType.CHARACTER, false);
        bValid = TokenUtils.makeCharToken('b');
        bInvalid = new ConcreteToken('b', TokenType.CHARACTER, false);
        aValidTwo = TokenUtils.makeCharToken('a');
        aValidityIgnoring = new ValidityIgnoringToken(aInvalid);
        aValidityEnsuring = new ValidityEnsuringToken(aInvalid);
        aValidityEnsuringValid = new ValidityEnsuringToken(aValid);
        aImmutable = new ImmutableToken(aValid);
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
    public void TestImmutableTokenThrowsExceptionOnModify() {
        expectedEx.expect(UnsupportedOperationException.class);
        expectedEx.expectMessage("Cannot modify immutable token!");

        aImmutable.setValid(false);
    }

    @Test
    public void TestImmutableTokenDoesNotImpactEquality() {
        assertTrue(aImmutable.equals(aValid));
        assertTrue(aImmutable.equals(aValidTwo));
        assertFalse(aImmutable.equals(aInvalid));
        assertFalse(aImmutable.equals(bValid));
        assertFalse(aImmutable.equals(bInvalid));
    }
}
