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

package edu.wpi.checksims.algorithm.similaritymatrix.output;

import edu.wpi.checksims.algorithm.AlgorithmResults;
import edu.wpi.checksims.algorithm.InternalAlgorithmError;
import edu.wpi.checksims.algorithm.similaritymatrix.SimilarityMatrix;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.token.TokenList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static edu.wpi.checksims.testutil.SubmissionUtils.charSubmissionFromString;
import static edu.wpi.checksims.testutil.SubmissionUtils.setFromElements;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;

/**
 * Tests for MatrixToCSVPrinter
 */
public class MatrixToCSVPrinterTest {
    private MatrixToCSVPrinter instance;
    private SimilarityMatrix twoByTwo;
    private SimilarityMatrix twoByThree;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() throws InternalAlgorithmError {
        instance = MatrixToCSVPrinter.getInstance();

        Submission abcd = charSubmissionFromString("ABCD", "ABCD");
        Submission abcdefgh =charSubmissionFromString("ABCDEFGH", "ABCDEFGH");

        TokenList abcdInval = TokenList.cloneTokenList(abcd.getContentAsTokens());
        abcdInval.stream().forEach((token) -> token.setValid(false));
        TokenList abcdefghInval = TokenList.cloneTokenList(abcdefgh.getContentAsTokens());
        for(int i = 0; i < 4; i++) {
            abcdefghInval.get(i).setValid(false);
        }

        AlgorithmResults abcdToAbcdefgh = new AlgorithmResults(abcd, abcdefgh, 4, 4, abcdInval, abcdefghInval);

        twoByTwo = SimilarityMatrix.generateMatrix(setFromElements(abcd, abcdefgh), singleton(abcdToAbcdefgh));

        Submission abxy = charSubmissionFromString("ABXY", "ABXY");
        Submission xyz = charSubmissionFromString("XYZ", "XYZ");
        Submission www = charSubmissionFromString("WWW", "WWW");

        TokenList abxyInval = TokenList.cloneTokenList(abxy.getContentAsTokens());
        for(int i = 2; i < 4; i++) {
            abxyInval.get(i).setValid(false);
        }
        TokenList xyzInval = TokenList.cloneTokenList(xyz.getContentAsTokens());
        for(int i = 0; i < 2; i++) {
            xyzInval.get(i).setValid(false);
        }

        AlgorithmResults abxyToXyz = new AlgorithmResults(abxy, xyz, 2, 2, abxyInval, xyzInval);
        AlgorithmResults abxyToWww = new AlgorithmResults(abxy, www, 0, 0, abxy.getContentAsTokens(), www.getContentAsTokens());
        AlgorithmResults xyzToWww = new AlgorithmResults(xyz, www, 0, 0, xyz.getContentAsTokens(), www.getContentAsTokens());

        twoByThree = SimilarityMatrix.generateMatrix(setFromElements(abxy, xyz), setFromElements(www), setFromElements(abxyToXyz, abxyToWww, xyzToWww));
    }

    @Test
    public void TestNameIsCSV() {
        assertEquals("csv", instance.getName());
    }

    @Test
    public void TestPrinterNullThrowsException() throws Exception {
        expectedEx.expect(NullPointerException.class);

        instance.printMatrix(null);
    }

    @Test
    public void TestPrinterOnTwoByTwo() throws Exception {
        String expected = "NULL,ABCD,ABCDEFGH\nABCD,1.00,1.00\nABCDEFGH,0.50,1.00\n";

        assertEquals(expected, instance.printMatrix(twoByTwo));
    }

    @Test
    public void TestPrinterOnThreeByTwo() throws Exception {
        String expected = "NULL,ABXY,XYZ,WWW\nABXY,1.00,0.50,0.00\nXYZ,0.67,1.00,0.00\n";

        assertEquals(expected, instance.printMatrix(twoByThree));
    }
}
