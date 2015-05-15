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

package net.lldp.checksims.algorithm.preprocessor;

import net.lldp.checksims.submission.ConcreteSubmission;
import net.lldp.checksims.submission.Submission;
import net.lldp.checksims.token.TokenList;
import net.lldp.checksims.token.tokenizer.Tokenizer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Lowercases tokens to prevent case from interfering with comparisons.
 */
public final class LowercasePreprocessor implements SubmissionPreprocessor {
    private static LowercasePreprocessor instance;

    private LowercasePreprocessor() {}

    /**
     * @return Singleton instance of LowercasePreprocessor
     */
    public static LowercasePreprocessor getInstance() {
        if(instance == null) {
            instance = new LowercasePreprocessor();
        }

        return instance;
    }

    @Override
    public String getName() {
        return "lowercase";
    }

    @Override
    public Submission process(Submission submission) {
        checkNotNull(submission);

        Tokenizer tokenizer = Tokenizer.getTokenizer(submission.getTokenType());

        // Lowercase the content of the submission, then retokenize
        String contentLower = submission.getContentAsString().toLowerCase();
        TokenList tokenizedLower = tokenizer.splitFile(contentLower);

        return new ConcreteSubmission(submission.getName(), contentLower, tokenizedLower);
    }
}
