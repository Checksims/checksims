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

package edu.wpi.checksims.algorithm;

import edu.wpi.checksims.ChecksimException;
import edu.wpi.checksims.submission.ConcreteSubmission;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.submission.ValidityIgnoringSubmission;
import edu.wpi.checksims.token.TokenList;
import edu.wpi.checksims.token.TokenType;
import edu.wpi.checksims.token.tokenizer.FileTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Remove common code from submissions
 */
public class CommonCodeRemover {
    private static final Logger logs = LoggerFactory.getLogger(CommonCodeRemover.class);

    private CommonCodeRemover() {}

    public static List<Submission> removeCommonCodeFromSubmissionsInList(List<Submission> removeFrom, Submission common, SimilarityDetector algorithm) {
        if(removeFrom.isEmpty()) {
            logs.debug("No submissions to perform common code removal on!");
            return removeFrom;
        }

        AtomicInteger submissionsProcessed = new AtomicInteger(0);
        long startTime = System.currentTimeMillis();

        List<Submission> toReturn = removeFrom.stream().parallel().map((submission) -> {
            try {
                logs.info("Removing common code from submission " + submissionsProcessed.incrementAndGet() + "/" + removeFrom.size());

                return removeCommonCodeFromSubmission(submission, common, algorithm);
            } catch(ChecksimException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        logs.info("Common code removal took " + elapsedTime + "ms");


        return toReturn;
    }

    public static Submission removeCommonCodeFromSubmission(Submission in, Submission common, SimilarityDetector algorithm) throws ChecksimException {
        logs.debug("Performing common code removal on submission " + in.getName());

        TokenType type = algorithm.getDefaultTokenType();
        FileTokenizer tokenizer = FileTokenizer.getTokenizer(type);

        // Re-tokenize input and common code using given token type
        TokenList redoneIn = tokenizer.splitFile(in.getContentAsString());
        TokenList redoneCommon = tokenizer.splitFile(common.getContentAsString());

        // Create new submissions with retokenized input
        Submission computeIn = new ConcreteSubmission(in.getName(), in.getContentAsString(), redoneIn);
        Submission computeCommon = new ConcreteSubmission(common.getName(), common.getContentAsString(), redoneCommon);

        // Use the new submissions to compute this
        AlgorithmResults results = algorithm.detectSimilarity(computeIn, computeCommon);

        // The results contains two TokenLists, representing the final state of the submissions after similarity detection
        // All common code should be marked invalid for the input submission's final list
        TokenList listWithCommonInvalid;
        float percentMatched;
        int identTokens;
        if(new ValidityIgnoringSubmission(results.a).equals(in)) {
            listWithCommonInvalid = results.finalListA;
            percentMatched = results.percentMatchedA();
            identTokens = results.identicalTokensA;
        } else {
            listWithCommonInvalid = results.finalListB;
            percentMatched = results.percentMatchedB();
            identTokens = results.identicalTokensB;
        }

        // Recreate the string body of the submission from this new list
        String newBody = listWithCommonInvalid.join(true);

        // Retokenize the new body with the original tokenization
        TokenType oldType = in.getTokenType();
        FileTokenizer oldTokenizer = FileTokenizer.getTokenizer(oldType);
        TokenList finalListGoodTokenization = oldTokenizer.splitFile(newBody);

        DecimalFormat d = new DecimalFormat("###.00");
        logs.trace("Submission " + in.getName() + " contained " + d.format(100 * percentMatched) + "% common code");
        logs.trace("Removed " + identTokens + " common tokens (of " + in.getNumTokens() + " total)");

        return new ConcreteSubmission(in.getName(), newBody, finalListGoodTokenization);
    }
}
