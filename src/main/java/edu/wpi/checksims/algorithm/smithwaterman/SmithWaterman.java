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

package edu.wpi.checksims.algorithm.smithwaterman;

import edu.wpi.checksims.algorithm.AlgorithmResults;
import edu.wpi.checksims.algorithm.InternalAlgorithmError;
import edu.wpi.checksims.algorithm.SimilarityDetector;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.token.TokenList;
import edu.wpi.checksims.token.TokenType;
import edu.wpi.checksims.token.TokenTypeMismatchException;
import org.apache.commons.lang3.tuple.Pair;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of the Smith-Waterman algorithm
 */
public class SmithWaterman implements SimilarityDetector {
    private static SmithWaterman instance;

    private SmithWaterman() {}

    /**
     * @return Singleton instance of the Smith-Waterman algorithm
     */
    public static SmithWaterman getInstance() {
        if(instance == null) {
            instance = new SmithWaterman();
        }

        return instance;
    }

    /**
     * @return Name of this implementation
     */
    @Override
    public String getName() {
        return "smithwaterman";
    }

    /**
     * @return Default token type to be used for this similarity detector
     */
    @Override
    public TokenType getDefaultTokenType() {
        return TokenType.WHITESPACE;
    }

    /**
     * Apply the Smith-Waterman algorithm to determine the similarity between two submissions
     *
     * Token list types of A and B must match
     *
     * @param a First submission to apply to
     * @param b Second submission to apply to
     * @return Similarity results of comparing submissions A and B
     * @throws TokenTypeMismatchException Thrown on comparing submissions with mismatched token types
     * @throws InternalAlgorithmError Thrown on internal error
     */
    @Override
    public AlgorithmResults detectSimilarity(Submission a, Submission b) throws TokenTypeMismatchException, InternalAlgorithmError {
        checkNotNull(a);
        checkNotNull(b);

        // Test for token type mismatch
        if(!a.getTokenType().equals(b.getTokenType())) {
            throw new TokenTypeMismatchException("Token list type mismatch: submission " + a.getName() + " has type " +
                    a.getTokenType().toString() + ", while submission " + b.getName() + " has type " + b.getTokenType().toString());
        }

        // Handle a 0-token submission (no similarity)
        if(a.getNumTokens() == 0 || b.getNumTokens() == 0) {
            return new AlgorithmResults(a, b, 0, 0, a.getContentAsTokens(), b.getContentAsTokens());
        } else if(a.equals(b)) {
            // Handle identical submissions
            TokenList aInval = TokenList.cloneTokenList(a.getContentAsTokens());
            aInval.stream().forEach((token) -> token.setValid(false));
            return new AlgorithmResults(a, b, a.getNumTokens(), b.getNumTokens(), aInval, aInval);
        }

        // Alright, easy cases taken care of. Generate an instance to perform the actual algorithm
        SmithWatermanAlgorithm algorithm = new SmithWatermanAlgorithm(a.getContentAsTokens(), b.getContentAsTokens());

        Pair<TokenList, TokenList> endLists = algorithm.computeSmithWatermanAlignment();

        // Generate matched tokens for each - filter out valid tokens, and count the invalid tokens
        int matchedTokensFirst = (int)endLists.getLeft().stream().filter((token) -> !token.isValid()).count();
        int matchedTokensSecond = (int)endLists.getRight().stream().filter((token) -> !token.isValid()).count();

        return new AlgorithmResults(a, b, matchedTokensFirst, matchedTokensSecond, endLists.getLeft(), endLists.getRight());
    }

    @Override
    public String toString() {
        return "Singleton instance of Smith-Waterman Algorithm";
    }
}
