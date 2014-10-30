package edu.wpi.checksims.algorithm;

import edu.wpi.checksims.Submission;

/**
 * Results for a pairwise comparison algorithm
 */
public final class AlgorithmResults {
    public final Submission a;
    public final Submission b;
    public final int identicalTokensA;
    public final int identicalTokensB;

    public AlgorithmResults(Submission a, Submission b, int identicalTokensA, int identicalTokensB) {
        this.a = a;
        this.b = b;
        this.identicalTokensA = identicalTokensA;
        this.identicalTokensB = identicalTokensB;
    }

    public float percentMatchedA() {
        return ((float)identicalTokensA) / a.getNumTokens();
    }

    public float percentMatchedB() {
        return ((float)identicalTokensB) / b.getNumTokens();
    }

    @Override
    public String toString() {
        return "Plagiarism comparison of submissions named " + a.getName() + " and " + b.getName();
    }
}
