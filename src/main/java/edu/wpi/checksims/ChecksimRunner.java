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

import com.google.common.collect.ImmutableList;
import edu.wpi.checksims.algorithm.AlgorithmRegistry;
import edu.wpi.checksims.algorithm.CommonCodeRemover;
import edu.wpi.checksims.algorithm.SimilarityDetector;
import edu.wpi.checksims.algorithm.output.OutputRegistry;
import edu.wpi.checksims.algorithm.output.SimilarityMatrix;
import edu.wpi.checksims.algorithm.output.SimilarityMatrixPrinter;
import edu.wpi.checksims.algorithm.preprocessor.PreprocessSubmissions;
import edu.wpi.checksims.algorithm.preprocessor.PreprocessorRegistry;
import edu.wpi.checksims.algorithm.preprocessor.SubmissionPreprocessor;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.token.TokenType;
import edu.wpi.checksims.token.tokenizer.FileTokenizer;
import edu.wpi.checksims.util.file.FileStringWriter;
import org.apache.commons.cli.*;
import org.apache.commons.collections4.list.SetUniqueList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.SimpleLogger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

/**
 * Entry point for Checksims
 */
public class ChecksimRunner {
    private static Logger logs;

    static Options getOpts() {
        Options opts = new Options();

        Option alg = new Option("a", "algorithm", true, "algorithm to use");
        Option token = new Option("t", "token", true, "tokenization type to use");
        Option out = new Option("o", "output", true, "output format");
        Option file = new Option("f", "file", true, "file to output to");
        Option preprocess = new Option("p", "preprocess", true, "preprocessors to apply");
        Option jobs = new Option("j", "jobs", true, "number of threads to use");
        Option verbose = new Option("v", "verbose", false, "specify verbose output");
        Option doubleVerbose = new Option("vv", "veryverbose", false, "specify very verbose output. supercedes -v if both specified.");
        Option help = new Option("h", "help", false, "show usage information");
        Option common = new Option("c", "common", true, "remove common code contained in given directory");
        Option recursive = new Option("r", "recursive", false, "recursively traverse subdirectories to generate submissions");

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

        return opts;
    }

    // Parse a given set of CLI arguments
    static CommandLine parseOpts(String[] args) throws ParseException {
        Parser parser = new GnuParser();

        // Parse the CLI args
        return parser.parse(getOpts(), args);
    }

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

        return LoggerFactory.getLogger(ChecksimRunner.class);
    }

    public static void main(String[] args) throws IOException {
        // TODO should split CLI handling into separate function and add unit tests

        CommandLine cli;
        try {
            cli = parseOpts(args);
        } catch(ParseException e) {
            throw new RuntimeException(e);
        }

        // Print CLI Help
        if(cli.hasOption("h")) {
            HelpFormatter f = new HelpFormatter();
            PrintWriter systemErr = new PrintWriter(System.err, true);

            f.printHelp(systemErr, 80, "checksims [args] glob directory [directory2 ...]", "checksims: check similarity of student submissions", getOpts(), 2, 4, "");

            System.err.println("\nSupported Similarity Detection Algorithms:");
            AlgorithmRegistry.getInstance().getSupportedAlgorithmNames().stream().forEach((name) -> System.err.print(name + ", "));
            System.err.println("\nDefault algorithm is " + AlgorithmRegistry.getInstance().getDefaultAlgorithmName());

            System.err.println("\nSupported Output Strategies:");
            OutputRegistry.getInstance().getAllOutputStrategyNames().stream().forEach((name) -> System.err.print(name + ", "));
            System.err.println("\nDefault strategy is " + OutputRegistry.getInstance().getDefaultStrategyName());

            System.err.println("\nAvailable Preprocessors:");
            PreprocessorRegistry.getInstance().getPreprocessorNames().stream().forEach((name) -> System.err.print(name + ", "));
            System.err.println();

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

        // Get unconsumed arguments
        String[] unusedArgs = cli.getArgs();

        if(unusedArgs.length < 2) {
            throw new RuntimeException("Expecting at least two arguments: File match glob, and folder(s) to check");
        }

        // First non-flag argument is the glob matcher
        // All the rest are directories containing student submissions
        String glob = unusedArgs[0];
        List<File> submissionDirs = new LinkedList<>();

        for(int i = 1; i < unusedArgs.length; i++) {
            logs.debug("Adding directory " + unusedArgs[i]);
            submissionDirs.add(new File(unusedArgs[i]));
        }

        // Create a base config to work from
        ChecksimConfig config = new ChecksimConfig();

        // Parse plagiarism detection algorithm
        if(cli.hasOption("a")) {
            try {
                config = config.setAlgorithm(AlgorithmRegistry.getInstance().getAlgorithmInstance(cli.getOptionValue("a")));
            } catch(ChecksimException e) {
                logs.error("Error obtaining algorithm!");
                throw new RuntimeException(e);
            }
        }

        // Parse recursive flag
        boolean recursive = false;
        if(cli.hasOption("r")) {
            recursive = true;
            logs.trace("Recursively traversing subdirectories of student directories");
        }

        // Parse tokenization
        TokenType tokenization;
        if(cli.hasOption("t")) {
            try {
                config = config.setTokenization(TokenType.fromString(cli.getOptionValue("t")));
            } catch(ChecksimException e) {
                logs.error("Error obtaining tokenization!");
                throw new RuntimeException(e);
            }
        }
        FileTokenizer tokenizer = FileTokenizer.getTokenizer(config.getTokenization());

        // Parse common code detection
        boolean removeCommonCode = cli.hasOption("c");
        if(removeCommonCode) {
            File commonCodeDir = new File(cli.getOptionValue("c"));
            Submission commonCode = Submission.submissionFromDir(commonCodeDir, glob, tokenizer, recursive);
            config = config.setCommonCodeRemoval(true, commonCode);
            // TODO may be desirable for this to be configurable
            try {
                config = config.setCommonCodeRemovalAlgorithm(AlgorithmRegistry.getInstance().getAlgorithmInstance("linecompare"));
            } catch(ChecksimException e) {
                logs.error("Cannot obtain instance of linecompare algorithm!");
                throw new RuntimeException(e);
            }
        }

        // Parse file output value
        boolean outputToFile = cli.hasOption("f");
        if(outputToFile) {
            File outputFile = new File(cli.getOptionValue("f"));
            config.setOutputToFile(true, outputFile);
            logs.info("Saving output to file " + outputFile.getName());
        }

        if(cli.hasOption("j")) {
            int threads = Integer.parseInt(cli.getOptionValue("j"));

            if (threads < 1) {
                logs.error("Invalid job count specified!");
                throw new RuntimeException("Must specify positive number of threads - got " + threads);
            }

            System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "" + threads);
        }

        // Parse preprocessors
        // Ensure no duplicates
        if(cli.hasOption("p")) {
            List<SubmissionPreprocessor> preprocessors = SetUniqueList.setUniqueList(new LinkedList<>());
            String[] splitPreprocessors = cli.getOptionValue("p").split(",");
            try {
                for (String s : splitPreprocessors) {
                    SubmissionPreprocessor p = PreprocessorRegistry.getInstance().getPreprocessor(s);
                    preprocessors.add(p);
                }
            } catch(ChecksimException e) {
                logs.error("Error obtaining preprocessors!");
                throw new RuntimeException(e);
            }
            config = config.setPreprocessors(preprocessors);
        }

        // Parse output strategies
        // Ensure no duplicates
        if(cli.hasOption("o")) {
            List<SimilarityMatrixPrinter> outputStrategies = SetUniqueList.setUniqueList(new LinkedList<>());
            String[] desiredStrategies = cli.getOptionValue("o").split(",");

            try {
                for(String s : desiredStrategies) {
                    SimilarityMatrixPrinter p = OutputRegistry.getInstance().getOutputStrategy(s);
                    outputStrategies.add(p);
                }
            } catch(ChecksimException e) {
                logs.error("Error obtaining output strategies!");
                throw new RuntimeException(e);
            }
            config = config.setOutputPrinters(outputStrategies);
        }

        // Generate submissions to work on
        List<Submission> submissions = new LinkedList<>();
        for(File dir : submissionDirs) {
            try {
                submissions.addAll(Submission.submissionListFromDir(dir, glob, tokenizer, recursive));
            } catch(IOException e) {
                logs.error("Error creating submissions from directory!");
                throw new RuntimeException(e);
            }
        }
        config.setSubmissions(submissions);

        runChecksims(config);

        System.exit(0);
    }

    public static void runChecksims(ChecksimConfig config) {
        // Check to see that the config is usable and user-specified CLI opts are good
        try {
            config.isReady();
        } catch(ChecksimException e) {
            logs.error("Error: invalid run configuration specified!");
            throw new RuntimeException(e);
        }

        ImmutableList<Submission> submissions = config.getSubmissions();

        logs.info("Got " + submissions.size() + " submissions to test.");

        if(submissions.size() == 0) {
            logs.error("No student submissions were found! Nothing to do!");
            System.exit(0);
        }

        // If we are performing common code detection...
        if(config.doRemoveCommonCode()) {
            // Perform common code removal before preprocessor application
            submissions = ImmutableList.copyOf(CommonCodeRemover.removeCommonCodeFromSubmissionsInList(submissions, config.getCommonCode(), config.getCommonCodeRemovalAlgorithm()));
        }

        // Apply all preprocessors
        for(SubmissionPreprocessor p : config.getPreprocessors()) {
            submissions = ImmutableList.copyOf(PreprocessSubmissions.process(p::process, submissions));
        }

        // Apply algorithm to submission
        SimilarityMatrix results = SimilarityMatrix.generate(submissions, config.getAlgorithm());

        for(SimilarityMatrixPrinter p : config.getOutputPrinters()) {
            String output = p.printMatrix(results);

            logs.info("Generating " + p.getName() + " output");

            if (config.doOutputToFile()) {
                try {
                    FileStringWriter.writeStringToFile(new File(config.getOutputFile().getAbsolutePath() + "." + p.getName()), output);
                } catch (IOException e) {
                    logs.error("Error printing output to file!");
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("\n\n");
                System.out.println(output);
            }
        }
    }
}
