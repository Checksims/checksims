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
 * Copyright (c) 2014-2015 Matthew Heon and Dolan Murvihill
 */

package edu.wpi.checksims;

import edu.wpi.checksims.algorithm.AlgorithmRegistry;
import edu.wpi.checksims.algorithm.commoncode.CommonCodeHandler;
import edu.wpi.checksims.algorithm.commoncode.CommonCodeLineRemovalHandler;
import edu.wpi.checksims.algorithm.commoncode.CommonCodePassthroughHandler;
import edu.wpi.checksims.algorithm.preprocessor.PreprocessorRegistry;
import edu.wpi.checksims.algorithm.preprocessor.SubmissionPreprocessor;
import edu.wpi.checksims.algorithm.similaritymatrix.output.MatrixPrinter;
import edu.wpi.checksims.algorithm.similaritymatrix.output.MatrixPrinterRegistry;
import edu.wpi.checksims.submission.EmptySubmissionException;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.token.TokenType;
import edu.wpi.checksims.token.tokenizer.Tokenizer;
import edu.wpi.checksims.util.output.OutputAsFilePrinter;
import edu.wpi.checksims.util.output.OutputPrinter;
import org.apache.commons.cli.*;
import org.apache.commons.collections4.list.SetUniqueList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.SimpleLogger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Parses Checksims' command-line options
 */
public final class ChecksimsCommandLine {
    private static Logger logs;

    static Logger startLogger(int level) {
        if(level == 1) {
            // Set verbose logging level
            System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");
        } else if(level == 2) {
            // Set very verbose logging level
            System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
        } else if(level == 0) {
            System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO");
        } else {
            throw new RuntimeException("Unrecognized verbosity level passed to startLogger!");
        }

        System.setProperty(SimpleLogger.SHOW_LOG_NAME_KEY, "false");
        System.setProperty(SimpleLogger.SHOW_THREAD_NAME_KEY, "false");
        System.setProperty(SimpleLogger.LEVEL_IN_BRACKETS_KEY, "true");

        return LoggerFactory.getLogger(ChecksimsCommandLine.class);
    }

    static Options getOpts() {
        Options opts = new Options();

        Option alg = new Option("a", "algorithm", true, "algorithm to use");
        Option token = new Option("t", "token", true, "tokenization type to use");
        Option out = new Option("o", "output", true, "output format");
        Option file = new Option("f", "file", true, "file to output to");
        Option preprocess = new Option("p", "preprocess", true, "preprocessors to apply");
        Option jobs = new Option("j", "jobs", true, "number of threads to use");
        Option verbose = new Option("v", "verbose", false, "specify verbose output");
        Option doubleVerbose = new Option("vv", "veryverbose", false,
                "specify very verbose output. supercedes -v if both specified.");
        Option help = new Option("h", "help", false, "show usage information");
        Option common = new Option("c", "common", true, "remove common code contained in given directory");
        Option recursive = new Option("r", "recursive", false,
                "recursively traverse subdirectories to generate submissions");
        Option version = new Option("version", false, "print version of Checksims");

        opts.addOption(alg);
        opts.addOption(token);
        opts.addOption(out);
        opts.addOption(file);
        opts.addOption(preprocess);
        opts.addOption(jobs);
        opts.addOption(verbose);
        opts.addOption(doubleVerbose);
        opts.addOption(help);
        opts.addOption(common);
        opts.addOption(recursive);
        opts.addOption(version);

        return opts;
    }

    // Parse a given set of CLI arguments
    static CommandLine parseOpts(String[] args) throws ParseException {
        checkNotNull(args);

        Parser parser = new GnuParser();

        // Parse the CLI args
        return parser.parse(getOpts(), args);
    }

    /**
     * Print help message
     */
    static void printHelp() {
        HelpFormatter f = new HelpFormatter();
        PrintWriter systemErr = new PrintWriter(System.err, true);

        f.printHelp(systemErr, 80, "checksims [args] glob directory [directory2 ...]",
                "checksims: check similarity of student submissions", getOpts(), 2, 4, "");

        System.err.println("\nSupported Similarity Detection Algorithms:");
        AlgorithmRegistry.getInstance().getSupportedImplementationNames().stream().
                forEach((name) -> System.err.print(name + ", "));
        System.err.println("\nDefault algorithm is " + AlgorithmRegistry.getInstance().getDefaultImplementationName());

        System.err.println("\nSupported Output Strategies:");
        MatrixPrinterRegistry.getInstance().getSupportedImplementationNames().stream().
                forEach((name) -> System.err.print(name + ", "));
        System.err.println("\nDefault strategy is "
                + MatrixPrinterRegistry.getInstance().getDefaultImplementationName());

        System.err.println("\nAvailable Preprocessors:");
        PreprocessorRegistry.getInstance().getSupportedImplementationNames().stream().
                forEach((name) -> System.err.print(name + ", "));
        System.err.println();

        System.err.println("\nChecksims Version " + ChecksimsRunner.getChecksimsVersion() + "\n\n");

        System.exit(0);
    }

    /**
     * Parse basic CLI flags and produce a ChecksimsConfig
     *
     * @param cli Parsed command line
     * @return Config derived from parsed CLI
     * @throws ChecksimsException Thrown on invalid user input or internal error
     */
    static ChecksimsConfig parseBaseFlags(CommandLine cli) throws ChecksimsException {
        checkNotNull(cli);

        // If we don't have a logger, set one up
        if(logs == null) {
            logs = LoggerFactory.getLogger(ChecksimsCommandLine.class);
        }

        // Create a base config to work from
        ChecksimsConfig config = new ChecksimsConfig();

        // Parse plagiarism detection algorithm
        if(cli.hasOption("a")) {
            config = config.setAlgorithm(
                    AlgorithmRegistry.getInstance().getImplementationInstance(cli.getOptionValue("a")));
            config = config.setTokenization(config.getAlgorithm().getDefaultTokenType());
        }

        // Parse tokenization
        if(cli.hasOption("t")) {
            config = config.setTokenization(TokenType.fromString(cli.getOptionValue("t")));
        }

        // Parse file output value
        boolean outputToFile = cli.hasOption("f");
        if(outputToFile) {
            File outputFile = new File(cli.getOptionValue("f"));
            OutputPrinter filePrinter = new OutputAsFilePrinter(outputFile);
            config = config.setOutputMethod(filePrinter);
            logs.info("Saving output to file " + outputFile.getName());
        }

        // Parse number of threads to use
        if(cli.hasOption("j")) {
            int numThreads = Integer.parseInt(cli.getOptionValue("j"));

            if(numThreads < 1) {
                throw new ChecksimsException("Thread count must be positive!");
            }

            config = config.setNumThreads(numThreads);
        }

        // Parse preprocessors
        // Ensure no duplicates
        if(cli.hasOption("p")) {
            List<SubmissionPreprocessor> preprocessors = SetUniqueList.setUniqueList(new ArrayList<>());
            String[] splitPreprocessors = cli.getOptionValue("p").split(",");
            for (String s : splitPreprocessors) {
                SubmissionPreprocessor p = PreprocessorRegistry.getInstance().getImplementationInstance(s);
                preprocessors.add(p);
            }
            config = config.setPreprocessors(preprocessors);
        }

        // Parse output strategies
        // Ensure no duplicates
        if(cli.hasOption("o")) {
            String[] desiredStrategies = cli.getOptionValue("o").split(",");
            Set<String> deduplicatedStrategies = new HashSet<>(Arrays.asList(desiredStrategies));

            if(deduplicatedStrategies.isEmpty()) {
                throw new ChecksimsException("Error: did not obtain a valid output strategy!");
            }

            // Convert to MatrixPrinters
            List<MatrixPrinter> printers = new ArrayList<>();
            for(String name : deduplicatedStrategies) {
                printers.add(MatrixPrinterRegistry.getInstance().getImplementationInstance(name));
            }

            config = config.setOutputPrinters(printers);
        }

        return config;
    }

    /**
     * Parse common code removal settings
     *
     * If the -c flag is not present, a CommonCodePassthroughHandler will be returned
     *
     * TODO add unit tests
     *
     * @param cli Parsed command line options
     * @param glob Glob matcher to use when building common code submission
     * @param tokenizer Tokenizer to use when building common code submission
     * @param recursive Whether to recursively traverse common code directory
     * @return Handler for common code
     * @throws ChecksimsException Thrown if no files matching the glob pattern are found in the common code directory
     * @throws IOException Thrown on error creating common code submission
     */
    static CommonCodeHandler parseCommonCodeSetting(CommandLine cli, String glob, Tokenizer tokenizer,
                                                    boolean recursive) throws ChecksimsException, IOException {
        checkNotNull(cli);
        checkNotNull(glob);

        // Parse common code detection
        boolean removeCommonCode = cli.hasOption("c");
        if(removeCommonCode) {
            File commonCodeDir = new File(cli.getOptionValue("c"));
            Submission commonCode = Submission.submissionFromDir(commonCodeDir, glob, tokenizer, recursive);

            try {
                return new CommonCodeLineRemovalHandler(commonCode);
            } catch(EmptySubmissionException e) {
                // The common code submission was empty
                // Inform the user we're not actually removing common code because of this
                logs.warn(e.getMessage());
                return CommonCodePassthroughHandler.getInstance();
            }
        }

        return CommonCodePassthroughHandler.getInstance();
    }

    /**
     * Build the collection of submissions Checksims will be run on
     *
     * TODO add unit tests
     *
     * @param cli Parsed command line options
     * @param glob Glob matcher to use when building submissions
     * @param tokenizer Tokenizer to use when building submissions
     * @param recursive Whether to recursively traverse when building submissions
     * @return Collection of submissions which will be used to run Checksims
     * @throws IOException Thrown on issue reading files or traversing directories to build submissions
     */
    static Set<Submission> getSubmissions(CommandLine cli, String glob, Tokenizer tokenizer, boolean recursive)
            throws IOException, ChecksimsException {
        checkNotNull(cli);
        checkNotNull(glob);

        String[] unusedArgs = cli.getArgs();
        List<File> submissionDirs = new ArrayList<>();

        if(unusedArgs.length < 2) {
            throw new ChecksimsException("Expected at least 2 arguments: glob pattern and a submission directory!");
        }

        // The first element in args should be the glob matcher, so start at index 1
        for(int i = 1; i < unusedArgs.length; i++) {
            logs.debug("Adding directory " + unusedArgs[i]);
            submissionDirs.add(new File(unusedArgs[i]));
        }

        // Generate submissions to work on
        Set<Submission> submissions = new HashSet<>();
        for(File dir : submissionDirs) {
            submissions.addAll(Submission.submissionListFromDir(dir, glob, tokenizer, recursive));
        }

        if(submissions.isEmpty()) {
            throw new ChecksimsException("Did not obtain any submissions to operate on!");
        }

        return submissions;
    }

    /**
     * Parse CLI arguments into a ChecksimsConfig
     *
     * Also configures logger, and sets parallelism level in ParallelAlgorithm
     *
     * TODO add unit tests
     *
     * @param args CLI arguments to parse
     * @return Config created from CLI arguments
     * @throws ParseException Thrown on error parsing CLI arguments
     * @throws IOException Thrown on error building a submission from files
     */
    static ChecksimsConfig parseCLI(String[] args) throws ParseException, ChecksimsException, IOException {
        checkNotNull(args);

        CommandLine cli = parseOpts(args);

        // Print CLI Help
        if(cli.hasOption("h")) {
            printHelp();
        }

        // Print version
        if(cli.hasOption("version")) {
            System.err.println("Checksims version " + ChecksimsRunner.getChecksimsVersion());
            System.exit(0);
        }

        // Parse verbose setting
        if(cli.hasOption("vv")) {
            logs = startLogger(2);
        } else if(cli.hasOption("v")) {
            logs = startLogger(1);
        } else {
            logs = startLogger(0);
        }

        // Parse recursive flag
        boolean recursive = false;
        if(cli.hasOption("r")) {
            recursive = true;
            logs.trace("Recursively traversing subdirectories of student directories");
        }

        // Get unconsumed arguments
        String[] unusedArgs = cli.getArgs();

        if(unusedArgs.length < 2) {
            throw new ChecksimsException("Expecting at least two arguments: File match glob, and folder(s) to check");
        }

        // First non-flag argument is the glob matcher
        // All the rest are directories containing student submissions
        String glob = unusedArgs[0];

        // First, parse basic flags
        ChecksimsConfig config = parseBaseFlags(cli);

        // Set up a tokenizer to use
        Tokenizer tokenizer = Tokenizer.getTokenizer(config.getTokenization());

        // Next, parse common code settings
        CommonCodeHandler handler = parseCommonCodeSetting(cli, glob, tokenizer, recursive);
        config = config.setCommonCodeHandler(handler);

        // Next, build submissions
        Set<Submission> submissions = getSubmissions(cli, glob, tokenizer, recursive);
        config = config.setSubmissions(submissions);

        logs.trace("CLI parsing complete!");

        return config;
    }
}
