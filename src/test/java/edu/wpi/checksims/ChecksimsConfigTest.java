package edu.wpi.checksims;

import edu.wpi.checksims.submission.ConcreteSubmission;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.token.TokenType;
import edu.wpi.checksims.token.tokenizer.FileTokenizer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Tests of validity checking on ChecksimsConfig
 *
 * TODO tests for all the getters/setters
 */
public class ChecksimsConfigTest {
    private static List<Submission> testSubmissionList;

    @BeforeClass
    public static void setUp() {
        FileTokenizer testTokenizer = FileTokenizer.getTokenizer(TokenType.CHARACTER);
        Submission test = new ConcreteSubmission("Test", "test", testTokenizer.splitFile("test"));

        testSubmissionList = Arrays.asList(test);
    }

    @Test(expected = ChecksimsException.class)
    public void TestConfigWithNoSubmissionsThrowsException() throws ChecksimsException {
        ChecksimsConfig config = new ChecksimsConfig();
        config.isReady();
    }

    @Test(expected = ChecksimsException.class)
    public void TestConfigWithSubmissionsButZeroThreads() throws ChecksimsException {
        ChecksimsConfig config = new ChecksimsConfig();
        config = config.setNumThreads(0);
        config = config.setSubmissions(testSubmissionList);

        config.isReady();
    }

    @Test(expected = ChecksimsException.class)
    public void TestConfigWithSubmissionsButNegativeThreads() throws ChecksimsException {
        ChecksimsConfig config = new ChecksimsConfig();
        config = config.setNumThreads(-1);
        config = config.setSubmissions(testSubmissionList);

        config.isReady();
    }

    @Test(expected = ChecksimsException.class)
    public void TestConfigWithNoOutputStrategiesThrowsException() throws ChecksimsException {
        ChecksimsConfig config = new ChecksimsConfig();
        config = config.setOutputPrinters(new LinkedList<>());
        config = config.setSubmissions(testSubmissionList);

        config.isReady();
    }

    @Test
    public void TestConfigCanBeCorrectlyParsed() throws ChecksimsException {
        ChecksimsConfig config = new ChecksimsConfig();
        config = config.setSubmissions(testSubmissionList);

        config.isReady();
    }
}
