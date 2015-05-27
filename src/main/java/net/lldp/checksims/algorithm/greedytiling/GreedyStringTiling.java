package net.lldp.checksims.algorithm.greedytiling;

import net.lldp.checksims.algorithm.AlgorithmResults;
import net.lldp.checksims.algorithm.InternalAlgorithmError;
import net.lldp.checksims.algorithm.SimilarityDetector;
import net.lldp.checksims.submission.Submission;
import net.lldp.checksims.token.TokenType;
import net.lldp.checksims.token.TokenTypeMismatchException;

/**
 * Implementation of Running Karp-Rabin Greedy String Tiling
 * Created by Dolan Murvihill on 5/27/15.
 */
public class GreedyStringTiling implements SimilarityDetector {
    private static GreedyStringTiling instance;

    public static GreedyStringTiling getInstance() {
        if(instance == null) {
            instance = new GreedyStringTiling();
        }
        return instance;
    }

    @Override
    public TokenType getDefaultTokenType() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public AlgorithmResults detectSimilarity(Submission a, Submission b)
            throws TokenTypeMismatchException, InternalAlgorithmError {
        throw new NullPointerException();
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
