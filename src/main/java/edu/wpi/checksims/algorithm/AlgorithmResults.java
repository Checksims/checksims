package edu.wpi.checksims.algorithm;

import com.google.common.collect.ImmutableList;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.token.TokenList;

/**
 * Results for a pairwise comparison algorithm
 */
public final class AlgorithmResults {
    public final Submission a;
    public final Submission b;
    public final int identicalTokensA;
    public final int identicalTokensB;
    public final TokenList finalListA;
    public final TokenList finalListB;

    public AlgorithmResults(Submission a, Submission b, int identicalTokensA, int identicalTokensB, TokenList finalListA, TokenList finalListB) {
        this.a = a;
        this.b = b;
        this.identicalTokensA = identicalTokensA;
        this.identicalTokensB = identicalTokensB;
        this.finalListA = TokenList.immutableCopy(finalListA);
        this.finalListB = TokenList.immutableCopy(finalListB);
    }

    // TODO may be desirable to ensure that identicalTokensA and identicalTokensB are never over the number of tokens in the submission?

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
        return "Plagiarism comparison of submissions named " + a.getName() + " and " + b.getName();
    }
}
