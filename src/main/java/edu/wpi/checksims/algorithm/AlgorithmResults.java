package edu.wpi.checksims.algorithm;

import edu.wpi.checksims.Submission;

/**
 * Results for a pairwise comparison algorithm
 */
public class AlgorithmResults<T extends Comparable<T>> {
    public final Submission<T> a;
    public final Submission<T> b;
    public final int identicalTokensA;
    public final int identicalTokensB;

    public AlgorithmResults(Submission<T> a, Submission<T> b, int identicalTokensA, int identicalTokensB) {
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
