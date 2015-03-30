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

package edu.wpi.checksims.util.threading;

import edu.wpi.checksims.algorithm.AlgorithmResults;
import edu.wpi.checksims.algorithm.SimilarityDetector;
import edu.wpi.checksims.submission.ConcreteSubmission;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.submission.ValidityIgnoringSubmission;
import edu.wpi.checksims.token.TokenList;
import edu.wpi.checksims.token.TokenType;
import edu.wpi.checksims.token.tokenizer.FileTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.concurrent.Callable;

/**
 * Basic unit of thread execution for Common Code Removal
 */
public class CommonCodeRemovalWorker implements Callable<Submission> {
    private final SimilarityDetector algorithm;
    private final Submission common;
    private final Submission removeFrom;

    private static Logger logs = LoggerFactory.getLogger(CommonCodeRemovalWorker.class);

    public CommonCodeRemovalWorker(SimilarityDetector algorithm, Submission common, Submission removeFrom) {
        this.algorithm = algorithm;
        this.common = common;
        this.removeFrom = removeFrom;
    }

    /**
     * Perform common code removal on given submission
     *
     * @return Submission with common code removed
     * @throws Exception Unused - all exceptions will be RuntimeException or similar
     */
    @Override
    public Submission call() throws Exception {
        logs.debug("Performing common code removal on submission " + removeFrom.getName());

        TokenType type = algorithm.getDefaultTokenType();
        FileTokenizer tokenizer = FileTokenizer.getTokenizer(type);

        // Re-tokenize input and common code using given token type
        TokenList redoneIn = tokenizer.splitFile(removeFrom.getContentAsString());
        TokenList redoneCommon = tokenizer.splitFile(common.getContentAsString());
        
        // Create new submissions with retokenized input
        Submission computeIn = new ConcreteSubmission(removeFrom.getName(), removeFrom.getContentAsString(), redoneIn);
        Submission computeCommon = new ConcreteSubmission(common.getName(), common.getContentAsString(), redoneCommon);

        // Use the new submissions to compute this
        AlgorithmResults results = algorithm.detectSimilarity(computeIn, computeCommon);

        // The results contains two TokenLists, representing the final state of the submissions after similarity detection
        // All common code should be marked invalid for the input submission's final list
        TokenList listWithCommonInvalid;
        float percentMatched;
        int identTokens;
        if(new ValidityIgnoringSubmission(results.a).equals(removeFrom)) {
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
        TokenType oldType = removeFrom.getTokenType();
        FileTokenizer oldTokenizer = FileTokenizer.getTokenizer(oldType);
        TokenList finalListGoodTokenization = oldTokenizer.splitFile(newBody);

        DecimalFormat d = new DecimalFormat("###.00");
        logs.trace("Submission " + removeFrom.getName() + " contained " + d.format(100 * percentMatched) + "% common code");
        logs.trace("Removed " + identTokens + " common tokens (of " + removeFrom.getNumTokens() + " total)");

        return new ConcreteSubmission(removeFrom.getName(), newBody, finalListGoodTokenization);
    }

    @Override
    public String toString() {
        return "Common Code Removal Worker for submission \"" + removeFrom.getName() + "\"";
    }
}
