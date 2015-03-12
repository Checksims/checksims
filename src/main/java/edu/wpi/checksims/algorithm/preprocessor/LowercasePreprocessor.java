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

package edu.wpi.checksims.algorithm.preprocessor;

import edu.wpi.checksims.submission.ConcreteSubmission;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.token.Token;
import edu.wpi.checksims.token.TokenList;

import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Lowercases tokens to prevent case from interfering with comparisons
 */
public class LowercasePreprocessor implements SubmissionPreprocessor {
    private static LowercasePreprocessor instance;

    private LowercasePreprocessor() {}

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
        Supplier<TokenList> tokenListSupplier = () -> new TokenList(submission.getContentAsTokens().type);

        return new ConcreteSubmission(submission.getName(), submission.getContentAsString(), submission.getContentAsTokens().stream().map(Token::lowerCase).collect(Collectors.toCollection(tokenListSupplier)));
    }
}
