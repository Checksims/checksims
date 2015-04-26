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
 *
 * TODO cleanup of this class
 */
public final class AlgorithmResults {
    public final Submission a;
    public final Submission b;
    public final int identicalTokensA;
    public final int identicalTokensB;
    public final TokenList finalListA;
    public final TokenList finalListB;

    // TODO consider refactoring to remove identicalTokensA and identicalTokensB - just compute at runtime?
    public AlgorithmResults(Submission a, Submission b, int identicalTokensA, int identicalTokensB, TokenList finalListA, TokenList finalListB) {
        checkNotNull(a);
        checkNotNull(b);
        checkNotNull(finalListA);
        checkNotNull(finalListB);

        int invalACount = (int)finalListA.stream().filter((token) -> !token.isValid()).count();
        int invalBCount = (int)finalListB.stream().filter((token) -> !token.isValid()).count();

        // Verify that identicalTokens matches the number of invalid tokens in the list
        checkArgument(invalACount == identicalTokensA, "Insane AlgorithmResults detected - " + invalACount +
                " invalid tokens found, but " + identicalTokensA + " reported for submission " + a.getName());
        checkArgument(invalBCount == identicalTokensB, "Insane AlgorithmResults detected - " + invalBCount +
                " invalid tokens found, but " + identicalTokensB + " reported for submission " + b.getName());

        this.a = a;
        this.b = b;
        this.identicalTokensA = identicalTokensA;
        this.identicalTokensB = identicalTokensB;
        this.finalListA = TokenList.immutableCopy(finalListA);
        this.finalListB = TokenList.immutableCopy(finalListB);
    }

    public float percentMatchedA() {
        if(a.getNumTokens() == 0) {
            return 0.0f;
        }

        return ((float)identicalTokensA) / a.getNumTokens();
    }

    public float percentMatchedB() {
        if(a.getNumTokens() == 0) {
            return 0.0f;
        }

        return ((float)identicalTokensB) / b.getNumTokens();
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

        return this.a.equals(otherResults.a) && this.b.equals(otherResults.b) && this.finalListA.equals(otherResults.finalListA)
                && this.finalListB.equals(otherResults.finalListB) && this.identicalTokensA == otherResults.identicalTokensA
                && this.identicalTokensB == otherResults.identicalTokensB;
    }

    @Override
    public int hashCode() {
        return a.hashCode() ^ b.hashCode();
    }
}
