package edu.wpi.checksims;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.LinkedList;

/**
 * Tests of validity checking on ChecksimsConfig
 *
 * TODO tests for all the getters/setters
 */
public class ChecksimsConfigTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void TestSetAlgorithmToNull() {
        expectedEx.expect(NullPointerException.class);

        ChecksimsConfig config = new ChecksimsConfig();
        config.setAlgorithm(null);
    }

    @Test
    public void TestSetTokenizationNull() {
        expectedEx.expect(NullPointerException.class);

        ChecksimsConfig config = new ChecksimsConfig();
        config.setTokenization(null);
    }

    @Test
    public void TestSetPreprocessorsNull() {
        expectedEx.expect(NullPointerException.class);

        ChecksimsConfig config = new ChecksimsConfig();
        config.setPreprocessors(null);
    }

    @Test
    public void TestSetSubmissionsNull() {
        expectedEx.expect(NullPointerException.class);

        ChecksimsConfig config = new ChecksimsConfig();
        config.setSubmissions(null);
    }

    @Test
    public void TestSetSubmissionsEmpty() {
        expectedEx.expect(IllegalArgumentException.class);

        ChecksimsConfig config = new ChecksimsConfig();
        config.setSubmissions(new LinkedList<>());
    }

    @Test
    public void TestSetCommonCodeHandlerNull() {
        expectedEx.expect(NullPointerException.class);

        ChecksimsConfig config = new ChecksimsConfig();
        config.setCommonCodeHandler(null);
    }

    @Test
    public void TestSetOutputPrintersNull() {
        expectedEx.expect(NullPointerException.class);

        ChecksimsConfig config = new ChecksimsConfig();
        config.setOutputPrinters(null);
    }

    @Test
    public void TestSetOutputPrintersEmpty() {
        expectedEx.expect(IllegalArgumentException.class);

        ChecksimsConfig config = new ChecksimsConfig();
        config.setOutputPrinters(new LinkedList<>());
    }

    @Test
    public void TestSetOutputMethodNull() {
        expectedEx.expect(NullPointerException.class);

        ChecksimsConfig config = new ChecksimsConfig();
        config.setOutputMethod(null);
    }

    @Test
    public void TestSetThreadsNegative() {
        expectedEx.expect(IllegalArgumentException.class);

        ChecksimsConfig config = new ChecksimsConfig();
        config.setNumThreads(-1);
    }

    @Test
    public void TestSetThreadsZero() {
        expectedEx.expect(IllegalArgumentException.class);

        ChecksimsConfig config = new ChecksimsConfig();
        config.setNumThreads(0);
    }
}
