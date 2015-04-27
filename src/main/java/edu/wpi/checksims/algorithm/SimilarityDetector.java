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

package edu.wpi.checksims.algorithm;

import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.token.TokenType;
import edu.wpi.checksims.token.TokenTypeMismatchException;
import edu.wpi.checksims.util.reflection.NamedInstantiable;

/**
 * Detect similarities between two submissions
 *
 * NOTE that, in addition to the methods listed here, all plagiarism detectors MUST support a no-arguments getInstance()
 * method, and be contained in edu.wpi.checksims.algorithm or a subpackage thereof.
 *
 * This is required as reflection is used to automatically detect and instantiate all similarity detection algorithms
 * present at runtime.
 */
public interface SimilarityDetector extends NamedInstantiable {
    /**
     * @return Default token type to be used for this similarity detector
     */
    TokenType getDefaultTokenType();

    /**
     * Apply a pairwise similarity detection algorithm
     *
     * Token list types of A and B must match
     *
     * @param a First submission to apply to
     * @param b Second submission to apply to
     * @return Similarity results of comparing submissions A and B
     * @throws TokenTypeMismatchException Thrown on comparing two submissions with different token types
     * @throws InternalAlgorithmError Thrown on error detecting similarities
     */
    AlgorithmResults detectSimilarity(Submission a, Submission b) throws TokenTypeMismatchException, InternalAlgorithmError;
}
