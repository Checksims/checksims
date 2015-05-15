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

package net.lldp.checksims.testutil;

import net.lldp.checksims.algorithm.preprocessor.SubmissionPreprocessor;
import net.lldp.checksims.submission.Submission;

import static org.junit.Assert.assertEquals;

/**
 * Convenience functions for testing preprocessors
 */
public class PreprocessorUtils {
    private PreprocessorUtils() {}

    /**
     * Preprocessor a submission, and verify its contents are what are expected
     *
     * @param toTest Submission to preprocess
     * @param expected Expected contents
     * @param preprocessor Preprocessor to apply
     */
    public static void checkPreprocessSubmission(Submission toTest, String expected, SubmissionPreprocessor preprocessor) throws Exception {
        Submission expectedSub;
        switch(toTest.getTokenType()) {
            case CHARACTER:
                expectedSub = SubmissionUtils.charSubmissionFromString(toTest.getName(), expected);
                break;
            case WHITESPACE:
                expectedSub = SubmissionUtils.whitespaceSubmissionFromString(toTest.getName(), expected);
                break;
            case LINE:
                expectedSub = SubmissionUtils.lineSubmissionFromString(toTest.getName(), expected);
                break;
            default:
                throw new RuntimeException("Unrecognized tokenization!");
        }

        Submission result = preprocessor.process(toTest);
        assertEquals(expectedSub, result);
    }

    /**
     * Preprocess a submission, and verify its contents have not changed
     *
     * @param toTest Submission to preprocess
     * @param preprocessor Preprocessor to apply
     */
    public static void checkPreprocessSubmissionIdentity(Submission toTest, SubmissionPreprocessor preprocessor) throws Exception {
        Submission result = preprocessor.process(toTest);
        assertEquals(toTest, result);
    }
}
