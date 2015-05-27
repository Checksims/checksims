package net.lldp.checksims.algorithm.greedytiling;

import net.lldp.checksims.algorithm.InternalAlgorithmError;
import net.lldp.checksims.token.TokenTypeMismatchException;
import org.junit.Test;

import static net.lldp.checksims.testutil.SubmissionUtils.charSubmissionFromString;

/**
 * Tests for the Running Karp-Rabin Greedy String Tiling detector
 *
 * Created by Dolan Murvihill on 5/27/15.
 */
public class GreedyStringTilingTest {
    private final GreedyStringTiling instance = GreedyStringTiling.getInstance();

    @Test(expected=NullPointerException.class)
    public void nullSubmissionA() throws InternalAlgorithmError, TokenTypeMismatchException {
        instance.detectSimilarity(null, charSubmissionFromString("test", "test"));
    }

    @Test(expected=NullPointerException.class)
    public void nullSubmissionB() throws InternalAlgorithmError, TokenTypeMismatchException {
        instance.detectSimilarity(charSubmissionFromString("test", "test"), null);
    }
}
