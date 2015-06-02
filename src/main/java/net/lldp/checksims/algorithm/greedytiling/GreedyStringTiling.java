package net.lldp.checksims.algorithm.greedytiling;

import net.lldp.checksims.algorithm.AlgorithmResults;
import net.lldp.checksims.algorithm.InternalAlgorithmError;
import net.lldp.checksims.algorithm.SimilarityDetector;
import net.lldp.checksims.submission.Submission;
import net.lldp.checksims.token.Token;
import net.lldp.checksims.token.TokenList;
import net.lldp.checksims.token.TokenType;
import net.lldp.checksims.token.TokenTypeMismatchException;

import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;

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
    public AlgorithmResults detectSimilarity(final Submission a, final Submission b)
            throws TokenTypeMismatchException, InternalAlgorithmError {
        checkNotNull(a);
        checkNotNull(b);
        // Test for token type mismatch
        if(!a.getTokenType().equals(b.getTokenType())) {
            throw new TokenTypeMismatchException("Token list type mismatch: submission " + a.getName() + " has type " +
                    a.getTokenType().toString() + ", while submission " + b.getName() + " has type "
                    + b.getTokenType().toString());
        }
        final TokenList aTokens = a.getContentAsTokens();
        final TokenList bTokens = b.getContentAsTokens();

        final Iterator<Token> aIt = aTokens.iterator();
        final Iterator<Token> bIt = bTokens.iterator();

        while(aIt.hasNext() && bIt.hasNext()) {
            final Token aTok = aIt.next();
            final Token bTok = bIt.next();
            if(aTok.equals(bTok)) {
                aTok.setValid(false);
                bTok.setValid(false);
            } else {
                aTok.setValid(true);
                bTok.setValid(true);
            }
        }

        return new AlgorithmResults(a, b, aTokens, bTokens);
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
