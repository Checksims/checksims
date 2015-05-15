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

package net.lldp.checksims.algorithm.similaritymatrix.output;

import net.lldp.checksims.algorithm.AlgorithmResults;
import net.lldp.checksims.algorithm.InternalAlgorithmError;
import net.lldp.checksims.algorithm.similaritymatrix.SimilarityMatrix;
import net.lldp.checksims.submission.Submission;
import net.lldp.checksims.token.TokenList;
import net.lldp.checksims.testutil.SubmissionUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;

/**
 * Tests for MatrixThresholdPrinter
 */
public class MatrixThresholdPrinterTest {
    private MatrixThresholdPrinter instance;
    private SimilarityMatrix noSignificant;
    private SimilarityMatrix oneSignificant;
    private SimilarityMatrix oneHalfSignificant;
    private SimilarityMatrix twoSignificant;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() throws InternalAlgorithmError {
        Submission abcd = SubmissionUtils.charSubmissionFromString("ABCD", "ABCD");
        Submission xyz = SubmissionUtils.charSubmissionFromString("XYZ", "XYZ");
        Submission abcde = SubmissionUtils.charSubmissionFromString("ABCDE", "ABCDE");
        Submission a = SubmissionUtils.charSubmissionFromString("A", "A");

        instance = MatrixThresholdPrinter.getInstance();

        AlgorithmResults abcdToXyz = new AlgorithmResults(abcd, xyz, abcd.getContentAsTokens(), xyz.getContentAsTokens());

        noSignificant = SimilarityMatrix.generateMatrix(SubmissionUtils.setFromElements(abcd, xyz), singleton(abcdToXyz));

        TokenList abcdInval = TokenList.cloneTokenList(abcd.getContentAsTokens());
        abcdInval.stream().forEach((token) -> token.setValid(false));
        TokenList abcdeInval = TokenList.cloneTokenList(abcde.getContentAsTokens());
        for(int i = 0; i < 4; i++) {
            abcdeInval.get(i).setValid(false);
        }

        AlgorithmResults abcdToAbcde = new AlgorithmResults(abcd, abcde, abcdInval, abcdeInval);

        oneSignificant = SimilarityMatrix.generateMatrix(SubmissionUtils.setFromElements(abcd, abcde), singleton(abcdToAbcde));

        TokenList abcdInval2 = TokenList.cloneTokenList(abcd.getContentAsTokens());
        abcdInval2.get(0).setValid(false);
        TokenList aInval = TokenList.cloneTokenList(a.getContentAsTokens());
        aInval.get(0).setValid(false);

        AlgorithmResults abcdToA = new AlgorithmResults(abcd, a, abcdInval2, aInval);

        oneHalfSignificant = SimilarityMatrix.generateMatrix(SubmissionUtils.setFromElements(abcd, a), singleton(abcdToA));

        Submission efgh = SubmissionUtils.charSubmissionFromString("EFGH", "EFGH");
        Submission fghijk = SubmissionUtils.charSubmissionFromString("FGHIJK", "FGHIJK");
        Submission e = SubmissionUtils.charSubmissionFromString("E", "E");

        TokenList efghInval1 = TokenList.cloneTokenList(efgh.getContentAsTokens());
        for(int i = 1; i < 4; i++) {
            efghInval1.get(i).setValid(false);
        }
        TokenList fghijkInval = TokenList.cloneTokenList(fghijk.getContentAsTokens());
        for(int i = 0; i < 3; i++) {
            fghijkInval.get(i).setValid(false);
        }

        AlgorithmResults efghToF = new AlgorithmResults(efgh, fghijk, efghInval1, fghijkInval);

        TokenList efghInval2 = TokenList.cloneTokenList(efgh.getContentAsTokens());
        efghInval2.get(0).setValid(false);
        TokenList eInval = TokenList.cloneTokenList(e.getContentAsTokens());
        eInval.get(0).setValid(false);

        AlgorithmResults efghToE = new AlgorithmResults(efgh, e, efghInval2, eInval);

        AlgorithmResults fToE = new AlgorithmResults(fghijk, e, fghijk.getContentAsTokens(), e.getContentAsTokens());

        twoSignificant = SimilarityMatrix.generateMatrix(SubmissionUtils.setFromElements(efgh, fghijk), singleton(e), SubmissionUtils.setFromElements(efghToF, efghToE, fToE));
    }

    @Test
    public void TestPrintNull() throws Exception {
        expectedEx.expect(NullPointerException.class);

        instance.printMatrix(null);
    }

    @Test
    public void TestNameIsThreshold() {
        assertEquals("threshold", instance.getName());
    }

    @Test
    public void TestPrintNoSignificant() throws Exception {
        String expected = "No significant matches found.\n";

        assertEquals(expected, instance.printMatrix(noSignificant));
    }

    @Test
    public void TestPrintOneSignificant() throws Exception {
        String expected = "Found match of 100% (inverse match 80%) between submissions \"ABCD\" and \"ABCDE\"\n";

        assertEquals(expected, instance.printMatrix(oneSignificant));
    }

    @Test
    public void TestPrintOneHalfSignificant() throws Exception {
        String expected = "Found match of 100% (inverse match 25%) between submissions \"A\" and \"ABCD\"\n";

        assertEquals(expected, instance.printMatrix(oneHalfSignificant));
    }

    @Test
    public void TestPrintTwoSignificant() throws Exception {
        String expected = "Found match of 100% (inverse match 25%) between submissions \"E\" and \"EFGH\"\n";
        expected += "Found match of 75% (inverse match 50%) between submissions \"EFGH\" and \"FGHIJK\"\n";

        assertEquals(expected, instance.printMatrix(twoSignificant));
    }
}
