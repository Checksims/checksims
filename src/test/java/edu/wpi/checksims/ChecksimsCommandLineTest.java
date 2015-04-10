package edu.wpi.checksims;

import edu.wpi.checksims.algorithm.AlgorithmRegistry;
import edu.wpi.checksims.algorithm.output.OutputRegistry;
import edu.wpi.checksims.algorithm.output.SimilarityMatrixPrinter;
import edu.wpi.checksims.algorithm.preprocessor.SubmissionPreprocessor;
import edu.wpi.checksims.token.TokenType;
import edu.wpi.checksims.util.output.OutputAsFilePrinter;
import edu.wpi.checksims.util.output.OutputToStdoutPrinter;
import edu.wpi.checksims.util.threading.ParallelAlgorithm;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.MissingArgumentException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the Checksims command line
 */
public class ChecksimsCommandLineTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void TestParseAlgorithm() throws Exception {
        String[] argsSmithWaterman = new String[] { "-a", "smithwaterman" };
        String[] argsLineCompare = new String[] { "-a", "linecompare" };
        String[] argsCaps = new String[] { "-a", "LineCompare" };
        String[] longForm = new String[] { "--algorithm", "smithwaterman" };
        String[] noArgs = new String[] {};

        CommandLine cli1 = ChecksimsCommandLine.parseOpts(argsSmithWaterman);
        ChecksimsConfig config1 = ChecksimsCommandLine.parseBaseFlags(cli1);
        assertNotNull(config1);
        assertEquals("smithwaterman", config1.getAlgorithm().getName());

        CommandLine cli2 = ChecksimsCommandLine.parseOpts(argsLineCompare);
        ChecksimsConfig config2 = ChecksimsCommandLine.parseBaseFlags(cli2);
        assertNotNull(config2);
        assertEquals("linecompare", config2.getAlgorithm().getName());

        CommandLine cli3 = ChecksimsCommandLine.parseOpts(argsCaps);
        ChecksimsConfig config3 = ChecksimsCommandLine.parseBaseFlags(cli3);
        assertNotNull(config3);
        assertEquals("linecompare", config3.getAlgorithm().getName());

        CommandLine cli4 = ChecksimsCommandLine.parseOpts(longForm);
        ChecksimsConfig config4 = ChecksimsCommandLine.parseBaseFlags(cli4);
        assertNotNull(config4);
        assertEquals("smithwaterman", config4.getAlgorithm().getName());

        CommandLine cli5 = ChecksimsCommandLine.parseOpts(noArgs);
        ChecksimsConfig config5 = ChecksimsCommandLine.parseBaseFlags(cli5);
        assertNotNull(config5);
        assertEquals(AlgorithmRegistry.getInstance().getDefaultImplementationName(), config5.getAlgorithm().getName());
    }

    @Test(expected = ChecksimsException.class)
    public void TestParseAlgorithmBadName() throws Exception {
        String[] argsInvalid = new String[] { "-a", "no_such_algorithm" };

        CommandLine cli = ChecksimsCommandLine.parseOpts(argsInvalid);
        ChecksimsCommandLine.parseBaseFlags(cli);
    }

    @Test(expected = MissingArgumentException.class)
    public void TestParseAlgorithmWithNoName() throws Exception {
        String[] argsInvalid = new String[] { "-a" };

        ChecksimsCommandLine.parseCLI(argsInvalid);
    }

    @Test
    public void TestParseTokenization() throws Exception {
        String[] argsLine = new String[] { "-t", "line" };
        String[] argsWhitespace = new String[] { "-t", "whitespace" };
        String[] argsChar = new String[] { "-t", "character" };
        String[] argsCaps = new String[] { "-t", "ChAraCtEr" };
        String[] longForm = new String[] { "--token", "character" };
        String[] noArgs = new String[] {};

        CommandLine cli1 = ChecksimsCommandLine.parseOpts(argsLine);
        ChecksimsConfig config1 = ChecksimsCommandLine.parseBaseFlags(cli1);
        assertNotNull(config1);
        assertEquals(TokenType.LINE, config1.getTokenization());

        CommandLine cli2 = ChecksimsCommandLine.parseOpts(argsWhitespace);
        ChecksimsConfig config2 = ChecksimsCommandLine.parseBaseFlags(cli2);
        assertNotNull(config2);
        assertEquals(TokenType.WHITESPACE, config2.getTokenization());

        CommandLine cli3 = ChecksimsCommandLine.parseOpts(argsChar);
        ChecksimsConfig config3 = ChecksimsCommandLine.parseBaseFlags(cli3);
        assertNotNull(config3);
        assertEquals(TokenType.CHARACTER, config3.getTokenization());

        CommandLine cli4 = ChecksimsCommandLine.parseOpts(argsCaps);
        ChecksimsConfig config4 = ChecksimsCommandLine.parseBaseFlags(cli4);
        assertNotNull(config4);
        assertEquals(TokenType.CHARACTER, config4.getTokenization());

        CommandLine cli5 = ChecksimsCommandLine.parseOpts(longForm);
        ChecksimsConfig config5 = ChecksimsCommandLine.parseBaseFlags(cli5);
        assertNotNull(config5);
        assertEquals(TokenType.CHARACTER, config5.getTokenization());

        CommandLine cli6 = ChecksimsCommandLine.parseOpts(noArgs);
        ChecksimsConfig config6 = ChecksimsCommandLine.parseBaseFlags(cli6);
        assertNotNull(config6);
        assertEquals(AlgorithmRegistry.getInstance().getDefaultImplementation().getDefaultTokenType(), config6.getTokenization());
    }

    @Test(expected = ChecksimsException.class)
    public void TestParseInvalidTokenization() throws Exception {
        String[] invalid = new String[] { "-t", "no_such_token" };

        CommandLine cli = ChecksimsCommandLine.parseOpts(invalid);
        ChecksimsCommandLine.parseBaseFlags(cli);
    }

    @Test(expected = MissingArgumentException.class)
    public void TestParseTokenizationMissingArg() throws Exception {
        String[] invalid = new String[] { "-t" };

        ChecksimsCommandLine.parseCLI(invalid);
    }

    @Test
    public void TestParseOutputToFile() throws Exception {
        String[] outputToFile = new String[] { "-f", "filename" };
        String[] outputToAnotherFile = new String[] { "-f", "anotherfile" };
        String[] noArgs = new String[] {};
        String[] verbose = new String[] { "--file", "verbose" };

        CommandLine cli1 = ChecksimsCommandLine.parseOpts(outputToFile);
        ChecksimsConfig config1 = ChecksimsCommandLine.parseBaseFlags(cli1);
        assertNotNull(config1);
        assertTrue(config1.getOutputMethod() instanceof OutputAsFilePrinter);
        OutputAsFilePrinter printer = (OutputAsFilePrinter)config1.getOutputMethod();
        assertEquals("filename", printer.getFile().getName());

        CommandLine cli2 = ChecksimsCommandLine.parseOpts(outputToAnotherFile);
        ChecksimsConfig config2 = ChecksimsCommandLine.parseBaseFlags(cli2);
        assertNotNull(config2);
        assertTrue(config2.getOutputMethod() instanceof OutputAsFilePrinter);
        OutputAsFilePrinter printer2 = (OutputAsFilePrinter)config2.getOutputMethod();
        assertEquals("anotherfile", printer2.getFile().getName());

        CommandLine cli3 = ChecksimsCommandLine.parseOpts(noArgs);
        ChecksimsConfig config3 = ChecksimsCommandLine.parseBaseFlags(cli3);
        assertNotNull(config3);
        assertTrue(config3.getOutputMethod() instanceof OutputToStdoutPrinter);

        CommandLine cli4 = ChecksimsCommandLine.parseOpts(verbose);
        ChecksimsConfig config4 = ChecksimsCommandLine.parseBaseFlags(cli4);
        assertNotNull(config4);
        assertTrue(config4.getOutputMethod() instanceof OutputAsFilePrinter);
        OutputAsFilePrinter printer4 = (OutputAsFilePrinter)config4.getOutputMethod();
        assertEquals("verbose", printer4.getFile().getName());
    }

    @Test(expected = MissingArgumentException.class)
    public void TestOutputToFileNoArg() throws Exception {
        String[] invalid = new String[] { "-f" };

        ChecksimsCommandLine.parseCLI(invalid);
    }

    @Test
    public void TestParseNumThreads() throws Exception {
        String[] one = new String[] { "-j", "1" };
        String[] two = new String[] { "-j", "2" };
        String[] zero = new String[] { "-j", "8" };
        String[] twoDigit = new String[] { "-j", "16" };
        String[] fullArg = new String[] { "--jobs", "4" };
        String[] noArgs = new String[] {};

        CommandLine cli1 = ChecksimsCommandLine.parseOpts(one);
        ChecksimsConfig config1 = ChecksimsCommandLine.parseBaseFlags(cli1);
        assertNotNull(config1);
        assertEquals(1, config1.getNumThreads());

        CommandLine cli2 = ChecksimsCommandLine.parseOpts(two);
        ChecksimsConfig config2 = ChecksimsCommandLine.parseBaseFlags(cli2);
        assertNotNull(config2);
        assertEquals(2, config2.getNumThreads());

        CommandLine cli3 = ChecksimsCommandLine.parseOpts(zero);
        ChecksimsConfig config3 = ChecksimsCommandLine.parseBaseFlags(cli3);
        assertNotNull(config3);
        assertEquals(8, config3.getNumThreads());

        CommandLine cli4 = ChecksimsCommandLine.parseOpts(twoDigit);
        ChecksimsConfig config4 = ChecksimsCommandLine.parseBaseFlags(cli4);
        assertNotNull(config4);
        assertEquals(16, config4.getNumThreads());

        CommandLine cli5 = ChecksimsCommandLine.parseOpts(fullArg);
        ChecksimsConfig config5 = ChecksimsCommandLine.parseBaseFlags(cli5);
        assertNotNull(config5);
        assertEquals(4, config5.getNumThreads());

        CommandLine cli6 = ChecksimsCommandLine.parseOpts(noArgs);
        ChecksimsConfig config6 = ChecksimsCommandLine.parseBaseFlags(cli6);
        assertNotNull(config6);
        assertEquals(ParallelAlgorithm.getThreadCount(), config6.getNumThreads());
    }

    @Test
    public void TestInvalidParseNumberJobs() throws Exception {
        expectedEx.expect(NumberFormatException.class);

        String[] invalidNumber = new String[] { "-j", "notanumber" };

        CommandLine cli = ChecksimsCommandLine.parseOpts(invalidNumber);
        ChecksimsCommandLine.parseBaseFlags(cli);
    }

    @Test
    public void TestExceptionOnParseFloatingPointJobs() throws Exception {
        expectedEx.expect(NumberFormatException.class);

        String[] invalidNumber = new String[] { "-j", "17.6" };

        CommandLine cli = ChecksimsCommandLine.parseOpts(invalidNumber);
        ChecksimsCommandLine.parseBaseFlags(cli);
    }

    @Test(expected = MissingArgumentException.class)
    public void TestJobsMissingArg() throws Exception {
        String[] invalid = new String[] { "-j" };

        ChecksimsCommandLine.parseCLI(invalid);
    }

    @Test
    public void TestParsePreprocessors() throws Exception {
        String[] onePreprocessor = new String[] { "-p", "lowercase" };
        String[] duplicatedPreprocessor = new String[] { "-p", "lowercase,lowercase" };
        String[] caps = new String[] { "-p", "lOwErcASE" };
        String[] verbose = new String[] { "--preprocess", "lowercase" };
        String[] empty = new String[] {};
        String[] twoPreprocessors = new String[] { "-p", "lowercase,deduplicate" };

        CommandLine cli1 = ChecksimsCommandLine.parseOpts(onePreprocessor);
        ChecksimsConfig config1 = ChecksimsCommandLine.parseBaseFlags(cli1);
        assertNotNull(config1);
        assertEquals(config1.getPreprocessors().size(), 1);
        assertEquals("lowercase", config1.getPreprocessors().get(0).getName());

        CommandLine cli2 = ChecksimsCommandLine.parseOpts(duplicatedPreprocessor);
        ChecksimsConfig config2 = ChecksimsCommandLine.parseBaseFlags(cli2);
        assertNotNull(config2);
        assertEquals(config2.getPreprocessors().size(), 1);
        assertEquals("lowercase", config2.getPreprocessors().get(0).getName());

        CommandLine cli3 = ChecksimsCommandLine.parseOpts(caps);
        ChecksimsConfig config3 = ChecksimsCommandLine.parseBaseFlags(cli3);
        assertNotNull(config3);
        assertEquals(config3.getPreprocessors().size(), 1);
        assertEquals("lowercase", config3.getPreprocessors().get(0).getName());

        CommandLine cli4 = ChecksimsCommandLine.parseOpts(verbose);
        ChecksimsConfig config4 = ChecksimsCommandLine.parseBaseFlags(cli4);
        assertNotNull(config4);
        assertEquals(config4.getPreprocessors().size(), 1);
        assertEquals("lowercase", config4.getPreprocessors().get(0).getName());

        CommandLine cli5 = ChecksimsCommandLine.parseOpts(empty);
        ChecksimsConfig config5 = ChecksimsCommandLine.parseBaseFlags(cli5);
        assertNotNull(config5);
        assertTrue(config5.getPreprocessors().isEmpty());

        CommandLine cli6 = ChecksimsCommandLine.parseOpts(twoPreprocessors);
        ChecksimsConfig config6 = ChecksimsCommandLine.parseBaseFlags(cli6);
        assertNotNull(config6);
        assertEquals(2, config6.getPreprocessors().size());
        List<String> names = config6.getPreprocessors().stream().map(SubmissionPreprocessor::getName).collect(Collectors.toList());
        assertTrue(names.contains("lowercase"));
        assertTrue(names.contains("deduplicate"));
    }

    @Test(expected = ChecksimsException.class)
    public void TestParseInvalidPreprocessor() throws Exception {
        String[] invalid = new String[] { "-p", "does_not_exist" };

        CommandLine cli = ChecksimsCommandLine.parseOpts(invalid);
        ChecksimsCommandLine.parseBaseFlags(cli);
    }

    @Test(expected = ChecksimsException.class)
    public void TestParseOneInvalidPreprocessorOutOfMultiple() throws Exception {
        String[] invalid = new String[] { "-p", "lowercase,does_not_exist" };

        CommandLine cli = ChecksimsCommandLine.parseOpts(invalid);
        ChecksimsCommandLine.parseBaseFlags(cli);
    }

    @Test(expected = MissingArgumentException.class)
    public void TestParsePreprocessorsMissingArg() throws Exception {
        String[] invalid = new String[] { "-p" };

        CommandLine cli = ChecksimsCommandLine.parseOpts(invalid);
        ChecksimsCommandLine.parseBaseFlags(cli);
    }

    @Test
    public void TestParseOutputStrategies() throws Exception {
        String[] oneStrategy1 = new String[] { "-o", "csv" };
        String[] oneStrategy2 = new String[] { "-o", "html" };
        String[] twoStrategies = new String[] { "-o", "html,csv" };
        String[] threeStrategies = new String[] { "-o", "threshold,csv,html" };
        String[] duplicatedStrategies = new String[] { "-o", "html,html" };
        String[] caps = new String[] { "-o", "HTMl" };
        String[] verbose = new String[] { "--output", "csv" };
        String[] empty = new String[] {};

        CommandLine cli1 = ChecksimsCommandLine.parseOpts(oneStrategy1);
        ChecksimsConfig config1 = ChecksimsCommandLine.parseBaseFlags(cli1);
        assertNotNull(config1);
        assertEquals(config1.getOutputPrinters().size(), 1);
        assertEquals(config1.getOutputPrinters().get(0).getName(), "csv");

        CommandLine cli2 = ChecksimsCommandLine.parseOpts(oneStrategy2);
        ChecksimsConfig config2 = ChecksimsCommandLine.parseBaseFlags(cli2);
        assertNotNull(config2);
        assertEquals(config2.getOutputPrinters().size(), 1);
        assertEquals(config2.getOutputPrinters().get(0).getName(), "html");

        CommandLine cli3 = ChecksimsCommandLine.parseOpts(twoStrategies);
        ChecksimsConfig config3 = ChecksimsCommandLine.parseBaseFlags(cli3);
        assertNotNull(config3);
        assertEquals(config3.getOutputPrinters().size(), 2);
        List<String> names = config3.getOutputPrinters().stream().map(SimilarityMatrixPrinter::getName).collect(Collectors.toList());
        assertTrue(names.contains("html"));
        assertTrue(names.contains("csv"));

        CommandLine cli4 = ChecksimsCommandLine.parseOpts(threeStrategies);
        ChecksimsConfig config4 = ChecksimsCommandLine.parseBaseFlags(cli4);
        assertNotNull(config4);
        assertEquals(config4.getOutputPrinters().size(), 3);
        List<String> names2 = config4.getOutputPrinters().stream().map(SimilarityMatrixPrinter::getName).collect(Collectors.toList());
        assertTrue(names2.contains("html"));
        assertTrue(names2.contains("csv"));
        assertTrue(names2.contains("threshold"));

        CommandLine cli5 = ChecksimsCommandLine.parseOpts(duplicatedStrategies);
        ChecksimsConfig config5 = ChecksimsCommandLine.parseBaseFlags(cli5);
        assertNotNull(config5);
        assertEquals(config5.getOutputPrinters().size(), 1);
        assertEquals(config5.getOutputPrinters().get(0).getName(), "html");

        CommandLine cli6 = ChecksimsCommandLine.parseOpts(caps);
        ChecksimsConfig config6 = ChecksimsCommandLine.parseBaseFlags(cli6);
        assertNotNull(config6);
        assertEquals(config6.getOutputPrinters().size(), 1);
        assertEquals(config6.getOutputPrinters().get(0).getName(), "html");

        CommandLine cli7 = ChecksimsCommandLine.parseOpts(verbose);
        ChecksimsConfig config7 = ChecksimsCommandLine.parseBaseFlags(cli7);
        assertNotNull(config7);
        assertEquals(config7.getOutputPrinters().size(), 1);
        assertEquals(config7.getOutputPrinters().get(0).getName(), "csv");

        CommandLine cli8 = ChecksimsCommandLine.parseOpts(empty);
        ChecksimsConfig config8 = ChecksimsCommandLine.parseBaseFlags(cli8);
        assertNotNull(config8);
        assertEquals(config8.getOutputPrinters().size(), 1);
        assertEquals(config8.getOutputPrinters().get(0).getName(), OutputRegistry.getInstance().getDefaultImplementationName());
    }

    @Test(expected = ChecksimsException.class)
    public void TestInvalidOutputStrategyThrowsException() throws Exception {
        String[] invalid = new String[] { "-o", "does_not_exist" };

        CommandLine cli = ChecksimsCommandLine.parseOpts(invalid);
        ChecksimsCommandLine.parseBaseFlags(cli);
    }

    @Test(expected = ChecksimsException.class)
    public void TestOneInvalidOutputStrategyAmongSeveralThrowsException() throws Exception {
        String[] invalid = new String[] { "-o", "csv,does_not_exist" };

        CommandLine cli = ChecksimsCommandLine.parseOpts(invalid);
        ChecksimsCommandLine.parseBaseFlags(cli);
    }

    @Test(expected = MissingArgumentException.class)
    public void TestOutputStrategyMissingArg() throws Exception {
        String[] invalid = new String[] { "-o" };

        ChecksimsCommandLine.parseOpts(invalid);
    }
}
