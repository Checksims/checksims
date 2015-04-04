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
import edu.wpi.checksims.algorithm.output.OutputRegistry;
import edu.wpi.checksims.algorithm.output.SimilarityMatrixPrinter;
import edu.wpi.checksims.algorithm.preprocessor.PreprocessorRegistry;
import edu.wpi.checksims.algorithm.preprocessor.SubmissionPreprocessor;
import edu.wpi.checksims.submission.EmptySubmissionException;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.token.TokenType;
import edu.wpi.checksims.token.tokenizer.FileTokenizer;
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
import java.util.LinkedList;
import java.util.List;

/**
 * Parses Checksims' command-line options
 */
public class ChecksimsCommandLine {
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

    /**
     * Parse CLI arguments into a ChecksimsConfig
     *
     * Also configs static logger, and sets parallelism level in ParallelAlgorithm
     *
     * TODO add unit tests
     *
     * @param args CLI arguments to parse
     * @return Config created from CLI arguments
     * @throws ParseException Thrown on error parsing CLI arguments
     */
    static ChecksimsConfig parseCLI(String[] args) throws ParseException, ChecksimsException {
        CommandLine cli = parseOpts(args);

        // Print CLI Help
        // TODO might want to make this a separate function?
        if(cli.hasOption("h")) {
            HelpFormatter f = new HelpFormatter();
            PrintWriter systemErr = new PrintWriter(System.err, true);

            f.printHelp(systemErr, 80, "checksims [args] glob directory [directory2 ...]", "checksims: check similarity of student submissions", getOpts(), 2, 4, "");

            System.err.println("\nSupported Similarity Detection Algorithms:");
            AlgorithmRegistry.getInstance().getSupportedImplementationNames().stream().forEach((name) -> System.err.print(name + ", "));
            System.err.println("\nDefault algorithm is " + AlgorithmRegistry.getInstance().getDefaultImplementationName());

            System.err.println("\nSupported Output Strategies:");
            OutputRegistry.getInstance().getSupportedImplementationNames().stream().forEach((name) -> System.err.print(name + ", "));
            System.err.println("\nDefault strategy is " + OutputRegistry.getInstance().getDefaultImplementationName());

            System.err.println("\nAvailable Preprocessors:");
            PreprocessorRegistry.getInstance().getSupportedImplementationNames().stream().forEach((name) -> System.err.print(name + ", "));
            System.err.println();

            System.exit(0);
        }

        // Parse verbose setting
        Logger logs;
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
            throw new ChecksimsException("Expecting at least two arguments: File match glob, and folder(s) to check");
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
        ChecksimsConfig config = new ChecksimsConfig();

        // Parse plagiarism detection algorithm
        if(cli.hasOption("a")) {
            config = config.setAlgorithm(AlgorithmRegistry.getInstance().getImplementationInstance(cli.getOptionValue("a")));
            config = config.setTokenization(config.getAlgorithm().getDefaultTokenType());
        }

        // Parse recursive flag
        boolean recursive = false;
        if(cli.hasOption("r")) {
            recursive = true;
            logs.trace("Recursively traversing subdirectories of student directories");
        }

        // Parse tokenization
        if(cli.hasOption("t")) {
            config = config.setTokenization(TokenType.fromString(cli.getOptionValue("t")));
        }
        FileTokenizer tokenizer = FileTokenizer.getTokenizer(config.getTokenization());

        // Parse common code detection
        boolean removeCommonCode = cli.hasOption("c");
        if(removeCommonCode) {
            File commonCodeDir = new File(cli.getOptionValue("c"));
            Submission commonCode;
            try {
                commonCode = Submission.submissionFromDir(commonCodeDir, glob, tokenizer, recursive);
            } catch(IOException e) {
                throw new ChecksimsException("Error obtaining common code", e);
            }
            try {
                CommonCodeHandler handler = new CommonCodeLineRemovalHandler(commonCode);
                config = config.setCommonCodeHandler(handler);
            } catch(EmptySubmissionException e) {
                // The common code submission was empty
                // Inform the user we're not actually removing common code because of this
                logs.warn(e.getMessage());
            }
        }

        // Parse file output value
        boolean outputToFile = cli.hasOption("f");
        if(outputToFile) {
            File outputFile = new File(cli.getOptionValue("f"));
            OutputPrinter filePrinter = new OutputAsFilePrinter(outputFile);
            config = config.setOutputMethod(filePrinter);
            logs.info("Saving output to file " + outputFile.getName());
        }

        if(cli.hasOption("j")) {
            config = config.setNumThreads(Integer.parseInt(cli.getOptionValue("j")));
        }

        // Parse preprocessors
        // Ensure no duplicates
        if(cli.hasOption("p")) {
            List<SubmissionPreprocessor> preprocessors = SetUniqueList.setUniqueList(new LinkedList<>());
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
            List<SimilarityMatrixPrinter> outputStrategies = SetUniqueList.setUniqueList(new LinkedList<>());
            String[] desiredStrategies = cli.getOptionValue("o").split(",");

            for(String s : desiredStrategies) {
                SimilarityMatrixPrinter p = OutputRegistry.getInstance().getImplementationInstance(s);
                outputStrategies.add(p);
            }

            config = config.setOutputPrinters(outputStrategies);
        }

        // Generate submissions to work on
        List<Submission> submissions = new LinkedList<>();
        for(File dir : submissionDirs) {
            try {
                submissions.addAll(Submission.submissionListFromDir(dir, glob, tokenizer, recursive));
            } catch(IOException e) {
                throw new ChecksimsException("Error creating submissions from directory", e);
            }
        }
        config = config.setSubmissions(submissions);

        logs.trace("CLI parsing complete!");

        return config;
    }
}
