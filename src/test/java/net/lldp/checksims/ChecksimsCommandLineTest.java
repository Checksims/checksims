/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright (c) 2014-2015 Nicholas DeMarinis, Matthew Heon, and Dolan Murvihill
 */

package net.lldp.checksims;

import net.lldp.checksims.algorithm.AlgorithmRegistry;
import net.lldp.checksims.algorithm.preprocessor.SubmissionPreprocessor;
import net.lldp.checksims.algorithm.similaritymatrix.output.MatrixPrinter;
import net.lldp.checksims.algorithm.similaritymatrix.output.MatrixPrinterRegistry;
import net.lldp.checksims.token.TokenType;
import net.lldp.checksims.util.output.OutputAsFilePrinter;
import net.lldp.checksims.util.output.OutputToStdoutPrinter;
import net.lldp.checksims.util.threading.ParallelAlgorithm;
import org.apache.commons.cli.AlreadySelectedException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Tests for the Checksims command line
 */
public class ChecksimsCommandLineTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    // TODO: Could make this take varargs String, removing the need to do new String[]{} when calling it
    public ChecksimsConfig parseToConfig(String[] args) throws Exception {
        CommandLine cli = ChecksimsCommandLine.parseOpts(args, false);
        ChecksimsConfig config = ChecksimsCommandLine.parseBaseFlags(cli);

        assertNotNull(config);

        return config;
    }

    @Test(expected = AlreadySelectedException.class)
    public void TestVerboseAndVeryVerboseConflict() throws Exception {
        parseToConfig(new String[] { "-v", "-vv" });
    }

    @Test(expected = UnrecognizedOptionException.class)
    public void TestInvalidOptionThrowsException() throws Exception {
        parseToConfig(new String[] { "-doesnotexist" });
    }

    @Test
    public void TestParseAlgorithmSmithWaterman() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "-a", "smithwaterman" });

        assertEquals("smithwaterman", config.getAlgorithm().getName());
    }

    @Test
    public void TestParseAlgorithmLineCompare() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "-a", "linecompare" });

        assertEquals("linecompare", config.getAlgorithm().getName());
    }

    @Test
    public void TestParseAlgorithmWithCaps() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "-a", "LiNeCOMPARE" });

        assertEquals("linecompare", config.getAlgorithm().getName());
    }

    @Test
    public void TestParseAlgorithmLongForm() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "--algorithm", "smithwaterman" });

        assertEquals("smithwaterman", config.getAlgorithm().getName());
    }

    @Test
    public void TestParseAlgorithmDefault() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] {});

        assertEquals(AlgorithmRegistry.getInstance().getDefaultImplementationName(), config.getAlgorithm().getName());
    }

    @Test(expected = ChecksimsException.class)
    public void TestParseAlgorithmBadName() throws Exception {
        String[] argsInvalid = new String[] { "-a", "no_such_algorithm" };

        CommandLine cli = ChecksimsCommandLine.parseOpts(argsInvalid, false);
        ChecksimsCommandLine.parseBaseFlags(cli);
    }

    @Test(expected = MissingArgumentException.class)
    public void TestParseAlgorithmWithNoName() throws Exception {
        String[] argsInvalid = new String[] { "-a" };

        ChecksimsCommandLine.parseCLI(argsInvalid);
    }

    @Test
    public void TestParseTokenizationLine() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "-t", "line" });

        assertEquals(TokenType.LINE, config.getTokenization());
    }

    @Test
    public void TestParseTokenizationWhitespace() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "-t", "whitespace" });

        assertEquals(TokenType.WHITESPACE, config.getTokenization());
    }

    @Test
    public void TestParseTokenizationCharacter() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "-t", "character" });

        assertEquals(TokenType.CHARACTER, config.getTokenization());
    }

    @Test
    public void TestParseTokenizationCaps() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "-t", "ChAraCTEr" });

        assertEquals(TokenType.CHARACTER, config.getTokenization());
    }

    @Test
    public void TestParseTokenizationLongForm() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "--token", "character" });

        assertEquals(TokenType.CHARACTER, config.getTokenization());
    }

    @Test
    public void TestParseTokenizationDefault() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] {});

        assertEquals(AlgorithmRegistry.getInstance().getDefaultImplementation().getDefaultTokenType(), config.getTokenization());
    }

    @Test(expected = ChecksimsException.class)
    public void TestParseInvalidTokenization() throws Exception {
        String[] invalid = new String[] { "-t", "no_such_token" };

        CommandLine cli = ChecksimsCommandLine.parseOpts(invalid, false);
        ChecksimsCommandLine.parseBaseFlags(cli);
    }

    @Test(expected = MissingArgumentException.class)
    public void TestParseTokenizationMissingArg() throws Exception {
        String[] invalid = new String[] { "-t" };

        ChecksimsCommandLine.parseCLI(invalid);
    }

    @Test
    public void TestParseOutputToFileOne() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "-f", "filename" });

        assertTrue(config.getOutputMethod() instanceof OutputAsFilePrinter);
        OutputAsFilePrinter printer = (OutputAsFilePrinter)config.getOutputMethod();
        assertEquals("filename", printer.getFile().getName());
    }

    @Test
    public void TestParseOutputToFileTwo() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "-f", "anotherfile" });

        assertTrue(config.getOutputMethod() instanceof OutputAsFilePrinter);
        OutputAsFilePrinter printer = (OutputAsFilePrinter)config.getOutputMethod();
        assertEquals("anotherfile", printer.getFile().getName());
    }

    @Test
    public void TestParseOutputToStdout() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] {});

        assertTrue(config.getOutputMethod() instanceof OutputToStdoutPrinter);
    }

    @Test
    public void TestParseOutputToFileLongForm() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "--file", "verbose" });

        assertTrue(config.getOutputMethod() instanceof OutputAsFilePrinter);
        OutputAsFilePrinter printer = (OutputAsFilePrinter)config.getOutputMethod();
        assertEquals("verbose", printer.getFile().getName());
    }

    @Test(expected = MissingArgumentException.class)
    public void TestOutputToFileNoArg() throws Exception {
        String[] invalid = new String[] { "-f" };

        ChecksimsCommandLine.parseCLI(invalid);
    }

    @Test
    public void TestParseNumThreadsOne() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "-j", "1" });

        assertEquals(1, config.getNumThreads());
    }

    @Test
    public void TestParseNumThreadsTwo() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "-j", "2" });

        assertEquals(2, config.getNumThreads());
    }

    @Test
    public void TestParseNumThreadsEight() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "-j", "8" });

        assertEquals(8, config.getNumThreads());
    }

    @Test
    public void TestParseNumThreadsSixteen() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "-j", "16" });

        assertEquals(16, config.getNumThreads());
    }

    @Test
    public void TestParseNumThreadsVerbose() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "--jobs", "4" });

        assertEquals(4, config.getNumThreads());
    }

    @Test
    public void TestParseNumThreadsDefault() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] {});

        assertEquals(ParallelAlgorithm.getThreadCount(), config.getNumThreads());
    }

    @Test
    public void TestInvalidParseNumberJobs() throws Exception {
        expectedEx.expect(NumberFormatException.class);

        String[] invalidNumber = new String[] { "-j", "notanumber" };

        CommandLine cli = ChecksimsCommandLine.parseOpts(invalidNumber, false);
        ChecksimsCommandLine.parseBaseFlags(cli);
    }

    @Test
    public void TestExceptionOnParseFloatingPointJobs() throws Exception {
        expectedEx.expect(NumberFormatException.class);

        String[] invalidNumber = new String[] { "-j", "17.6" };

        CommandLine cli = ChecksimsCommandLine.parseOpts(invalidNumber, false);
        ChecksimsCommandLine.parseBaseFlags(cli);
    }

    @Test(expected = ChecksimsException.class)
    public void TestParseNumThreadsZero() throws Exception {
        String[] invalidNumber = new String[] { "-j", "0" };

        CommandLine cli = ChecksimsCommandLine.parseOpts(invalidNumber, false);
        ChecksimsCommandLine.parseBaseFlags(cli);
    }

    @Test(expected = ChecksimsException.class)
    public void TestParseNumThreadsNegative() throws Exception {
        String[] invalidNumber = new String[] { "-j", "-2" };

        CommandLine cli = ChecksimsCommandLine.parseOpts(invalidNumber, false);
        ChecksimsCommandLine.parseBaseFlags(cli);
    }

    @Test(expected = MissingArgumentException.class)
    public void TestJobsMissingArg() throws Exception {
        String[] invalid = new String[] { "-j" };

        ChecksimsCommandLine.parseCLI(invalid);
    }

    @Test
    public void TestParsePreprocessorsOnePreprocessor() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "-p", "lowercase" });

        assertEquals(1, config.getPreprocessors().size());
        assertEquals("lowercase", config.getPreprocessors().get(0).getName());
    }

    @Test
    public void TestParsePreprocessorsDuplicatedPreprocessor() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "-p", "lowercase,lowercase" });

        assertEquals(1, config.getPreprocessors().size());
        assertEquals("lowercase", config.getPreprocessors().get(0).getName());
    }

    @Test
    public void TestParsePreprocessorsTwoPreprocessors() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "-p", "lowercase,deduplicate" });

        assertEquals(2, config.getPreprocessors().size());
        List<String> names = config.getPreprocessors().stream().map(SubmissionPreprocessor::getName).collect(Collectors.toList());
        assertTrue(names.contains("lowercase"));
        assertTrue(names.contains("deduplicate"));
    }

    @Test
    public void TestParsePreprocessorsCaps() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "-p", "LoWERcasE" });

        assertEquals(1, config.getPreprocessors().size());
        assertEquals("lowercase", config.getPreprocessors().get(0).getName());
    }

    @Test
    public void TestParsePreprocessorsLongForm() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "--preprocess", "lowercase" });

        assertEquals(1, config.getPreprocessors().size());
        assertEquals("lowercase", config.getPreprocessors().get(0).getName());
    }

    @Test
    public void TestParsePreprocessorsDefault() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] {});

        assertTrue(config.getPreprocessors().isEmpty());
    }

    @Test(expected = ChecksimsException.class)
    public void TestParseInvalidPreprocessor() throws Exception {
        String[] invalid = new String[] { "-p", "does_not_exist" };

        CommandLine cli = ChecksimsCommandLine.parseOpts(invalid, false);
        ChecksimsCommandLine.parseBaseFlags(cli);
    }

    @Test(expected = ChecksimsException.class)
    public void TestParseOneInvalidPreprocessorOutOfMultiple() throws Exception {
        String[] invalid = new String[] { "-p", "lowercase,does_not_exist" };

        CommandLine cli = ChecksimsCommandLine.parseOpts(invalid, false);
        ChecksimsCommandLine.parseBaseFlags(cli);
    }

    @Test(expected = MissingArgumentException.class)
    public void TestParsePreprocessorsMissingArg() throws Exception {
        String[] invalid = new String[] { "-p" };

        CommandLine cli = ChecksimsCommandLine.parseOpts(invalid, false);
        ChecksimsCommandLine.parseBaseFlags(cli);
    }

    @Test
    public void TestParseOutputStrategyCSV() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "-o", "csv" });

        assertEquals(1, config.getOutputPrinters().size());
        assertEquals("csv", config.getOutputPrinters().get(0).getName());
    }

    @Test
    public void TestParseOutputStrategyHTML() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "-o", "html" });

        assertEquals(1, config.getOutputPrinters().size());
        assertEquals("html", config.getOutputPrinters().get(0).getName());
    }


    @Test
    public void TestParseOutputStrategyThreshold() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "-o", "threshold" });

        assertEquals(1, config.getOutputPrinters().size());
        assertEquals("threshold", config.getOutputPrinters().get(0).getName());
    }

    @Test
    public void TestParseOutputStrategyDuplicated() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "-o", "threshold,threshold" });

        assertEquals(1, config.getOutputPrinters().size());
        assertEquals("threshold", config.getOutputPrinters().get(0).getName());
    }

    @Test
    public void TestParseOutputStrategyTwoStrategies() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "-o", "csv,html" });

        assertEquals(2, config.getOutputPrinters().size());
        List<String> names = config.getOutputPrinters().stream().map(MatrixPrinter::getName).collect(Collectors.toList());
        assertTrue(names.contains("csv"));
        assertTrue(names.contains("html"));
    }

    @Test
    public void TestParseOutputStrategyThreeStrategies() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "-o", "csv,html,threshold" });

        assertEquals(3, config.getOutputPrinters().size());
        List<String> names = config.getOutputPrinters().stream().map(MatrixPrinter::getName).collect(Collectors.toList());
        assertTrue(names.contains("csv"));
        assertTrue(names.contains("html"));
        assertTrue(names.contains("threshold"));
    }

    @Test
    public void TestParseOutputStrategyCaps() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "-o", "HTML" });

        assertEquals(1, config.getOutputPrinters().size());
        assertEquals("html", config.getOutputPrinters().get(0).getName());
    }

    @Test
    public void TestParseOutputStrategyVerbose() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] { "--output", "threshold" });

        assertEquals(1, config.getOutputPrinters().size());
        assertEquals("threshold", config.getOutputPrinters().get(0).getName());
    }

    @Test
    public void TestParseOutputStrategyDefault() throws Exception {
        ChecksimsConfig config = parseToConfig(new String[] {});

        assertEquals(1, config.getOutputPrinters().size());
        assertEquals(MatrixPrinterRegistry.getInstance().getDefaultImplementationName(), config.getOutputPrinters().get(0).getName());
    }

    @Test(expected = ChecksimsException.class)
    public void TestInvalidOutputStrategyThrowsException() throws Exception {
        String[] invalid = new String[] { "-o", "does_not_exist" };

        CommandLine cli = ChecksimsCommandLine.parseOpts(invalid, false);
        ChecksimsCommandLine.parseBaseFlags(cli);
    }

    @Test(expected = ChecksimsException.class)
    public void TestOneInvalidOutputStrategyAmongSeveralThrowsException() throws Exception {
        String[] invalid = new String[] { "-o", "csv,does_not_exist" };

        CommandLine cli = ChecksimsCommandLine.parseOpts(invalid, false);
        ChecksimsCommandLine.parseBaseFlags(cli);
    }

    @Test(expected = MissingArgumentException.class)
    public void TestOutputStrategyMissingArg() throws Exception {
        String[] invalid = new String[] { "-o" };

        ChecksimsCommandLine.parseOpts(invalid, false);
    }
}
