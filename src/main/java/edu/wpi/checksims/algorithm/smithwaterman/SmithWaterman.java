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
import edu.wpi.checksims.token.TokenType;
import edu.wpi.checksims.token.TokenTypeMismatchException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Performs the actual Smith-Waterman algorithm
 */
public class SmithWaterman implements SimilarityDetector {
    private static SmithWaterman instance;

    private SmithWaterman() {}

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
     * Apply a pairwise similarity detection algorithm
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

        return new AlgorithmResults(a, b, 0, 0, a.getContentAsTokens(), b.getContentAsTokens());
    }
}
