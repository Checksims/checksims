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

import net.lldp.checksims.algorithm.AlgorithmResults;
import net.lldp.checksims.algorithm.InternalAlgorithmError;
import net.lldp.checksims.algorithm.SimilarityDetector;
import net.lldp.checksims.algorithm.linesimilarity.LineSimilarityChecker;
import net.lldp.checksims.submission.ConcreteSubmission;
import net.lldp.checksims.submission.Submission;
import net.lldp.checksims.submission.ValidityIgnoringSubmission;
import net.lldp.checksims.token.TokenList;
import net.lldp.checksims.token.TokenType;
import net.lldp.checksims.token.TokenTypeMismatchException;
import net.lldp.checksims.token.tokenizer.Tokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Common Code Removal via Line Comparison.
 */
public class CommonCodeLineRemovalPreprocessor implements SubmissionPreprocessor {
    private final Submission common;
    private static final SimilarityDetector algorithm = LineSimilarityChecker.getInstance();
    private static final Logger logs = LoggerFactory.getLogger(CommonCodeLineRemovalPreprocessor.class);

    /**
     * @return Dummy instance of CommonCodeLineRemovalPreprocessor with empty common code
     */
    public static CommonCodeLineRemovalPreprocessor getInstance() {
        return new CommonCodeLineRemovalPreprocessor(new ConcreteSubmission("Empty", "",
                new TokenList(TokenType.CHARACTER)));
    }

    /**
     * Create a Common Code Removal preprocessor using Line Compare.
     *
     * @param common Common code to remove
     */
    public CommonCodeLineRemovalPreprocessor(Submission common) {
        checkNotNull(common);

        this.common = common;
    }

    /**
     * Perform common code removal using Line Comparison.
     *
     * @param removeFrom Submission to remove common code from
     * @return Input submission with common code removed
     * @throws InternalAlgorithmError Thrown on error removing common code
     */
    @Override
    public Submission process(Submission removeFrom) throws InternalAlgorithmError {
        logs.debug("Performing common code removal on submission " + removeFrom.getName());

        TokenType type = algorithm.getDefaultTokenType();
        Tokenizer tokenizer = Tokenizer.getTokenizer(type);

        // Re-tokenize input and common code using given token type
        TokenList redoneIn = tokenizer.splitFile(removeFrom.getContentAsString());
        TokenList redoneCommon = tokenizer.splitFile(common.getContentAsString());

        // Create new submissions with retokenized input
        Submission computeIn = new ConcreteSubmission(removeFrom.getName(), removeFrom.getContentAsString(), redoneIn);
        Submission computeCommon = new ConcreteSubmission(common.getName(), common.getContentAsString(), redoneCommon);

        // Use the new submissions to compute this
        AlgorithmResults results;

        // This exception should never happen, but if it does, just rethrow as InternalAlgorithmException
        try {
            results = algorithm.detectSimilarity(computeIn, computeCommon);
        } catch(TokenTypeMismatchException e) {
            throw new InternalAlgorithmError(e.getMessage());
        }

        // The results contains two TokenLists, representing the final state of the submissions after detection
        // All common code should be marked invalid for the input submission's final list
        TokenList listWithCommonInvalid;
        double percentMatched;
        int identTokens;
        if(new ValidityIgnoringSubmission(results.a).equals(computeIn)) {
            listWithCommonInvalid = results.finalListA;
            percentMatched = results.percentMatchedA();
            identTokens = results.identicalTokensA;
        } else if(new ValidityIgnoringSubmission(results.b).equals(computeIn)) {
            listWithCommonInvalid = results.finalListB;
            percentMatched = results.percentMatchedB();
            identTokens = results.identicalTokensB;
        } else {
            throw new RuntimeException("Unreachable code!");
        }

        // Recreate the string body of the submission from this new list
        String newBody = listWithCommonInvalid.join(true);

        // Retokenize the new body with the original tokenization
        TokenType oldType = removeFrom.getTokenType();
        Tokenizer oldTokenizer = Tokenizer.getTokenizer(oldType);
        TokenList finalListGoodTokenization = oldTokenizer.splitFile(newBody);

        DecimalFormat d = new DecimalFormat("###.00");
        logs.trace("Submission " + removeFrom.getName() + " contained " + d.format(100 * percentMatched)
                + "% common code");
        logs.trace("Removed " + identTokens + " common tokens (of " + removeFrom.getNumTokens() + " total)");

        return new ConcreteSubmission(removeFrom.getName(), newBody, finalListGoodTokenization);
    }

    /**
     * @return Name of the implementation as it will be seen in the registry
     */
    @Override
    public String getName() {
        return "commoncodeline";
    }

    @Override
    public String toString() {
        return "Common Code Line Removal preprocessor, removing common code submission " + common.getName();
    }

    @Override
    public int hashCode() {
        return getName().hashCode() ^ common.getName().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof CommonCodeLineRemovalPreprocessor)) {
            return false;
        }

        CommonCodeLineRemovalPreprocessor otherPreprocessor = (CommonCodeLineRemovalPreprocessor)other;

        return otherPreprocessor.common.equals(common);
    }
}
