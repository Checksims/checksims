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

package net.lldp.checksims.submission;

import net.lldp.checksims.token.Token;
import net.lldp.checksims.token.TokenList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static net.lldp.checksims.testutil.SubmissionUtils.charSubmissionFromString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Tests for Submissions
 */
public class SubmissionTest {
    private Submission a;
    private Submission aTwo;
    private Submission aInval;
    private Submission abc;

    @Before
    public void setUp() {
        a = charSubmissionFromString("a", "a");
        aTwo = charSubmissionFromString("a", "a");
        final TokenList aInvalList = TokenList.cloneTokenList(a.getContentAsTokens());
        aInvalList.get(0).setValid(false);
        aInval = new ConcreteSubmission("a", "a", aInvalList);
        abc = charSubmissionFromString("abc", "abc");
    }

    @Test
    public void TestSubmissionEquality() {
        assertEquals(a, aTwo);
    }

    @Test
    public void TestBasicSubmissionOperations() {
        Assert.assertEquals(a.getContentAsString(), a.getContentAsTokens().join(false));
        Assert.assertEquals(a.getNumTokens(), a.getContentAsTokens().size());
        Assert.assertEquals(a.getTokenType(), a.getContentAsTokens().type);
        assertEquals("a", a.getName());

        assertEquals(3, abc.getNumTokens());
    }

    @Test
    public void TestSubmissionEqualityIsValiditySensitive() {
        assertNotEquals(a, aInval);
    }

    @Test
    public void TestValidityIgnoringSubmissionEquality() {
        Submission aIgnoring = new ValidityIgnoringSubmission(a);

        assertEquals(aIgnoring, aInval);
        assertEquals(aIgnoring, a);
    }

    @Test
    public void TestValidityEnforcingSubmissionEquality() {
        Submission aEnforcing = new ValidityEnsuringSubmission(a);
        Submission aInvalEnforcing = new ValidityEnsuringSubmission(aInval);

        assertEquals(aEnforcing, a);
        assertNotEquals(aEnforcing, aInval);
        assertNotEquals(aInvalEnforcing, a);
        assertNotEquals(aInvalEnforcing, aInval);
    }
    @Test(expected=UnsupportedOperationException.class)
    public void testTokenListIsImmutable() {
        Submission s1 = charSubmissionFromString("s1", "testtest");
        Submission s2 = charSubmissionFromString("s2", "test");
        final TokenList aTokens = s1.getContentAsTokens();
        final TokenList bTokens = s2.getContentAsTokens();

        final Iterator<Token> aIt = aTokens.iterator();
        final Iterator<Token> bIt = bTokens.iterator();

        while(aIt.hasNext() && bIt.hasNext()) {
            final Token aTok = aIt.next();
            final Token bTok = bIt.next();
            if(aTok.equals(bTok)) {
                aTok.setValid(false);
                bTok.setValid(false);
            }
        }

        assertEquals(8, s1.getContentAsTokens().numValid());
        assertEquals(4, s2.getContentAsTokens().numValid());
    }
}
