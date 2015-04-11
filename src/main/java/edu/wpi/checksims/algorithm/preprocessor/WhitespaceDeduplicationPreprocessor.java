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

package edu.wpi.checksims.algorithm.preprocessor;

import edu.wpi.checksims.submission.ConcreteSubmission;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.token.TokenList;
import edu.wpi.checksims.token.tokenizer.Tokenizer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Remove duplicated whitespace characters
 */
public class WhitespaceDeduplicationPreprocessor implements SubmissionPreprocessor {
    private static WhitespaceDeduplicationPreprocessor instance;

    private WhitespaceDeduplicationPreprocessor() {}

    /**
     * @return Singleton instance of WhitespaceDeduplicationPreprocessor
     */
    public static WhitespaceDeduplicationPreprocessor getInstance() {
        if(instance == null) {
            instance = new WhitespaceDeduplicationPreprocessor();
        }

        return instance;
    }

    /**
     * Deduplicate whitespace in a submission
     *
     * @param submission Submission to transform
     * @return Input submission with whitespace deduplicated
     */
    @Override
    public Submission process(Submission submission) {
        checkNotNull(submission);

        String tabsAndSpacesDedup = submission.getContentAsString().replaceAll("[ \t]+", " ");
        String unixNewlineDedup = tabsAndSpacesDedup.replaceAll("\n+", "\n");
        String windowsNewlineDedup = unixNewlineDedup.replaceAll("(\r\n)+", "\r\n");

        Tokenizer tokenizer = Tokenizer.getTokenizer(submission.getTokenType());

        TokenList finalList = tokenizer.splitFile(windowsNewlineDedup);

        return new ConcreteSubmission(submission.getName(), windowsNewlineDedup, finalList);
    }

    /**
     * @return Name of the implementation as it will be seen in the registry
     */
    @Override
    public String getName() {
        return "deduplicate";
    }

    @Override
    public String toString() {
        return "Singleton instance of WhitespaceDeduplicationPreprocessor";
    }
}
