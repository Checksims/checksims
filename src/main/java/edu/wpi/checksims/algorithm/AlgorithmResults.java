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
import edu.wpi.checksims.token.TokenList;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Results for a pairwise comparison algorithm
 */
public final class AlgorithmResults {
    // TODO consider making these private and adding getters
    public final Submission a;
    public final Submission b;
    public final int identicalTokensA;
    public final int identicalTokensB;
    public final TokenList finalListA;
    public final TokenList finalListB;
    private final double percentMatchedA;
    private final double percentMatchedB;

    /**
     * Construct results for a pairwise similarity detection algorith,
     *
     * @param a First submission compared
     * @param b Second submission compared
     * @param finalListA Token list from submission A, with matched tokens set invalid
     * @param finalListB Token list from submission B, with matched tokens set invalid
     */
    public AlgorithmResults(Submission a, Submission b, TokenList finalListA, TokenList finalListB) {
        checkNotNull(a);
        checkNotNull(b);
        checkNotNull(finalListA);
        checkNotNull(finalListB);
        checkArgument(a.getNumTokens() == finalListA.size(),
                "Token size mismatch when creating algorithm results for submission \"" + a.getName()
                        + "\" --- expected " + a.getNumTokens() + ", got " + finalListA.size());
        checkArgument(b.getNumTokens() == finalListB.size(),
                "Token size mismatch when creating algorithm results for submission \"" + b.getName()
                        + "\" --- expected " + b.getNumTokens() + ", got " + finalListB.size());

        this.a = a;
        this.b = b;
        this.finalListA = TokenList.immutableCopy(finalListA);
        this.finalListB = TokenList.immutableCopy(finalListB);

        this.identicalTokensA = (int)finalListA.stream().filter((token) -> !token.isValid()).count();
        this.identicalTokensB = (int)finalListB.stream().filter((token) -> !token.isValid()).count();

        if(a.getNumTokens() == 0) {
            percentMatchedA = 0.0;
        } else {
            percentMatchedA = ((double)identicalTokensA) / (double)a.getNumTokens();
        }

        if(b.getNumTokens() == 0) {
            percentMatchedB = 0.0;
        } else {
            percentMatchedB = ((double)identicalTokensB) / (double)b.getNumTokens();
        }
    }

    /**
     * @return Percentage similarity of submission A to submission B. Represented as a double from 0.0 to 1.0 inclusive
     */
    public double percentMatchedA() {
        return percentMatchedA;
    }

    /**
     * @return Percentage similarity of submission B to submission A. Represented as a double from 0.0 to 1.0 inclusive
     */
    public double percentMatchedB() {
        return percentMatchedB;
    }

    @Override
    public String toString() {
        return "Similarity results for submissions named " + a.getName() + " and " + b.getName();
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof AlgorithmResults)) {
            return false;
        }

        AlgorithmResults otherResults = (AlgorithmResults)other;

        return this.a.equals(otherResults.a)
                && this.b.equals(otherResults.b)
                && this.finalListA.equals(otherResults.finalListA)
                && this.finalListB.equals(otherResults.finalListB)
                && this.identicalTokensA == otherResults.identicalTokensA
                && this.identicalTokensB == otherResults.identicalTokensB;
    }

    @Override
    public int hashCode() {
        return a.hashCode() ^ b.hashCode();
    }
}
