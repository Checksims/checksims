package net.lldp.checksims.algorithm.greedytiling;

import net.lldp.checksims.algorithm.AlgorithmResults;
import net.lldp.checksims.algorithm.InternalAlgorithmError;
import net.lldp.checksims.algorithm.smithwaterman.SmithWaterman;
import net.lldp.checksims.submission.Submission;
import net.lldp.checksims.token.Token;
import net.lldp.checksims.token.TokenList;
import net.lldp.checksims.token.TokenTypeMismatchException;
import org.junit.Test;

import java.util.Iterator;

import static net.lldp.checksims.testutil.AlgorithmUtils.checkResultsIdenticalSubmissions;
import static net.lldp.checksims.testutil.AlgorithmUtils.checkResultsNoMatch;
import static net.lldp.checksims.testutil.SubmissionUtils.charSubmissionFromString;
import static net.lldp.checksims.testutil.SubmissionUtils.whitespaceSubmissionFromString;

/**
 * Tests for the Running Karp-Rabin Greedy String Tiling detector
 *
 * Created by Dolan Murvihill on 5/27/15.
 */
public class GreedyStringTilingTest {
    private final GreedyStringTiling instance = GreedyStringTiling.getInstance();
    private static final Submission test = charSubmissionFromString("test", "test");
    private static final Submission empty = charSubmissionFromString("empty", "");
    private static final Submission typeMismatch =
            whitespaceSubmissionFromString("type mismatch", "this is a whitespace thing");

    @Test(expected=NullPointerException.class)
    public void nullSubmissionA() throws InternalAlgorithmError, TokenTypeMismatchException {
        instance.detectSimilarity(null, test);
    }

    @Test(expected=NullPointerException.class)
    public void nullSubmissionB() throws InternalAlgorithmError, TokenTypeMismatchException {
        instance.detectSimilarity(test, null);
    }

    @Test(expected=TokenTypeMismatchException.class)
    public void testTokenTypeMismatch() throws InternalAlgorithmError, TokenTypeMismatchException {
        instance.detectSimilarity(empty, typeMismatch);
    }

    @Test
    public void emptySubmissions() throws InternalAlgorithmError, TokenTypeMismatchException {
        final AlgorithmResults results = instance.detectSimilarity(empty, empty);
        checkResultsIdenticalSubmissions(results, empty);
    }

    @Test
    public void emptySubmissionA() throws InternalAlgorithmError, TokenTypeMismatchException {
        final AlgorithmResults results = instance.detectSimilarity(empty, test);
        checkResultsNoMatch(results, empty, test);
    }

    @Test
    public void emptySubmissionB() throws InternalAlgorithmError, TokenTypeMismatchException {
        final AlgorithmResults results = instance.detectSimilarity(test, empty);
        checkResultsNoMatch(results, test, empty);
    }

    //@Test
    public void identicalSubmissions() throws InternalAlgorithmError, TokenTypeMismatchException {
        final AlgorithmResults results = instance.detectSimilarity(test, test);
        checkResultsIdenticalSubmissions(results, test);
    }

}
