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
import net.lldp.checksims.algorithm.commoncode.CommonCodeHandler;
import net.lldp.checksims.algorithm.commoncode.CommonCodeLineRemovalHandler;
import net.lldp.checksims.algorithm.preprocessor.PreprocessorRegistry;
import net.lldp.checksims.algorithm.preprocessor.SubmissionPreprocessor;
import net.lldp.checksims.algorithm.similaritymatrix.output.MatrixPrinter;
import net.lldp.checksims.algorithm.similaritymatrix.output.MatrixPrinterRegistry;
import net.lldp.checksims.submission.EmptySubmissionException;
import net.lldp.checksims.submission.Submission;
import net.lldp.checksims.token.TokenType;
import net.lldp.checksims.token.tokenizer.Tokenizer;
import net.lldp.checksims.util.output.OutputAsFilePrinter;
import net.lldp.checksims.util.output.OutputPrinter;
import org.apache.commons.cli.*;
import org.apache.commons.collections4.list.SetUniqueList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.SimpleLogger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Parses Checksims' command-line options.
 */
public final class ChecksimsCommandLine {
    private static Logger logs;

    private ChecksimsCommandLine() {}

    /**
     * @param level Logging level to use. Supported levels are 0 (nonverbose), 1 (verbose), 2 (very verbose)
     * @return Logger with appropriate logging level
     */
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

    /**
     * @param anyRequired Whether any arguments are required
     * @return CLI options used in Checksims
     */
    static Options getOpts(boolean anyRequired) {
        Options opts = new Options();

        Option alg = Option.builder("a")
                .longOpt("algorithm")
                .hasArg()
                .argName("name")
                .desc("algorithm to compare with")
                .build();

        Option token = Option.builder("t")
                .longOpt("token")
                .hasArg()
                .argName("type")
                .desc("tokenization to use for submissions")
                .build();

        Option out = Option.builder("o")
                .longOpt("output")
                .hasArgs()
                .argName("name1[,name2,...]")
                .valueSeparator(',')
                .desc("output format(s) to use, comma-separated if multiple given")
                .build();

        Option file = Option.builder("f")
                .longOpt("file")
                .hasArg()
                .argName("filename")
                .desc("print output to given file")
                .build();

        Option preprocess = Option.builder("p")
                .longOpt("preprocess")
                .hasArgs()
                .argName("name1[,name2,...]")
                .valueSeparator(',')
                .desc("preprocessor(s) to apply, comma-separated if multiple given")
                .build();

        Option jobs = Option.builder("j")
                .longOpt("jobs")
                .hasArg()
                .argName("num")
                .desc("number of threads to use")
                .build();

        Option glob = Option.builder("g")
                .longOpt("glob")
                .hasArg()
                .argName("matchpattern")
                .desc("match pattern to determine files included in submissions")
                .build();

        OptionGroup verbosity = new OptionGroup();
        Option verbose = new Option("v", "verbose", false, "specify verbose output. conflicts with -vv");
        Option doubleVerbose = new Option("vv", "veryverbose", false,
                "specify very verbose output. conflicts with -v");
        verbosity.addOption(verbose);
        verbosity.addOption(doubleVerbose);

        Option help = new Option("h", "help", false, "show usage information");

        Option common = Option.builder("c")
                .longOpt("common")
                .hasArg()
                .argName("path")
                .desc("directory containing common code which will be removed from all submissions")
                .build();

        Option recursive = new Option("r", "recursive", false,
                "recursively traverse subdirectories to generate submissions");

        Option version = new Option("version", false, "print version of Checksims");

        Option archiveDir = Option.builder("archive")
                .longOpt("archivedir")
                .desc("archive submissions - compared to main submissions but not each other")
                .argName("path")
                .hasArgs()
                .valueSeparator('*')
                .build();

        Option submissionDir = Option.builder("s")
                .longOpt("submissiondir")
                .desc("directory or directories containing submissions to compare - mandatory!")
                .argName("path")
                .hasArgs()
                .valueSeparator('*')
                .build();

        if(anyRequired) {
            submissionDir.setRequired(true);
        }

        opts.addOption(alg);
        opts.addOption(token);
        opts.addOption(out);
        opts.addOption(file);
        opts.addOption(preprocess);
        opts.addOption(jobs);
        opts.addOption(glob);
        opts.addOptionGroup(verbosity);
        opts.addOption(help);
        opts.addOption(common);
        opts.addOption(recursive);
        opts.addOption(version);
        opts.addOption(archiveDir);
        opts.addOption(submissionDir);

        return opts;
    }

    /**
     * Parse a given set of CLI arguments into a Commons CLI CommandLine.
     *
     * @param args Arguments to parse
     * @param anyRequired Whether arguments should be required
     * @return CommandLine from parsed arguments
     * @throws ParseException Thrown on error parsing arguments
     */
    static CommandLine parseOpts(String[] args, boolean anyRequired) throws ParseException {
        checkNotNull(args);

        DefaultParser parser = new DefaultParser();

        // Parse the CLI args
        return parser.parse(getOpts(anyRequired), args);
    }

    /**
     * Print help message.
     */
    static void printHelp() {
        HelpFormatter f = new HelpFormatter();
        PrintWriter systemErr = new PrintWriter(System.err, true);

        f.printHelp(systemErr, 80, "checksims [args]",
                "checksims: check similarity of student submissions", getOpts(true), 2, 4, "");

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
     * Parse basic CLI flags and produce a ChecksimsConfig.
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

            String[] preprocessorsToUse = cli.getOptionValues("p");
            for (String s : preprocessorsToUse) {
                SubmissionPreprocessor p = PreprocessorRegistry.getInstance().getImplementationInstance(s);
                preprocessors.add(p);
            }
            config = config.setPreprocessors(preprocessors);
        }

        // Parse output strategies
        // Ensure no duplicates
        if(cli.hasOption("o")) {
            String[] desiredStrategies = cli.getOptionValues("o");
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
     * Parse flags which require submissions to be built.
     *
     * TODO unit tests
     *
     * @param cli Parse CLI options
     * @param baseConfig Base configuration to work off
     * @return Modified baseConfig with submissions (and possibly common code and archive submissions) changed
     * @throws ChecksimsException Thrown on bad argument
     * @throws IOException Thrown on error building submissions
     */
    static ChecksimsConfig parseFileFlags(CommandLine cli, ChecksimsConfig baseConfig)
            throws ChecksimsException, IOException {
        checkNotNull(cli);
        checkNotNull(baseConfig);

        ChecksimsConfig toReturn = new ChecksimsConfig(baseConfig);

        // Get glob match pattern
        // Default to *
        String globPattern = cli.getOptionValue("g", "*");

        // Check if we are recursively building
        boolean recursive = cli.hasOption("r");

        // Get the tokenizer specified by base config
        Tokenizer tokenizer = Tokenizer.getTokenizer(baseConfig.getTokenization());

        // Get submission directories
        if(!cli.hasOption("s")) {
            throw new ChecksimsException("Must provide at least one submission directory!");
        }

        String[] submissionDirsString = cli.getOptionValues("s");

        // Make a Set<File> from those submission directories
        // Map to absolute file, to ensure no dups
        Set<File> submissionDirs = Arrays.stream(submissionDirsString)
                .map(File::new)
                .map(File::getAbsoluteFile)
                .collect(Collectors.toSet());

        if(submissionDirs.isEmpty()) {
            throw new ChecksimsException("Must provide at least one submission directory!");
        }

        // Generate submissions
        Set<Submission> submissions = getSubmissions(submissionDirs, globPattern, tokenizer, recursive);

        if(submissions.isEmpty()) {
            throw new ChecksimsException("Could build any submissions to operate on!");
        }

        toReturn = toReturn.setSubmissions(submissions);

        // Check if we need to perform common code removal
        if(cli.hasOption("c")) {
            // Get the directory containing the common code
            String commonCodeDirString = cli.getOptionValue("c");

            // Make a file from it
            File commonCodeDir = new File(commonCodeDirString).getAbsoluteFile();

            // Verify that it's not a submission dir
            if(submissionDirs.contains(commonCodeDir)) {
                throw new ChecksimsException("Common code directory cannot be a submission directory!");
            }

            // All right, parse common code
            Submission commonCodeSubmission = Submission.submissionFromDir(commonCodeDir, globPattern, tokenizer,
                    recursive);

            try {
                CommonCodeHandler handler = new CommonCodeLineRemovalHandler(commonCodeSubmission);

                toReturn = toReturn.setCommonCodeHandler(handler);
            } catch(EmptySubmissionException e) {
                // Empty common code will not be removed
                // Warn about this
                logs.warn(e.getMessage());
            }
        }

        // Check if we need to add archive directories
        if(cli.hasOption("archive")) {
            String[] archiveDirsString = cli.getOptionValues("archive");

            // Convert them into a set of files, again using getAbsoluteFile
            Set<File> archiveDirs = Arrays.stream(archiveDirsString)
                    .map(File::new)
                    .map(File::getAbsoluteFile)
                    .collect(Collectors.toSet());

            // Ensure that none of them are also submission directories
            for(File archiveDir : archiveDirs) {
                if(submissionDirs.contains(archiveDir)) {
                    throw new ChecksimsException("Directory is both an archive directory and submission directory: "
                            + archiveDir.getAbsolutePath());
                }
            }

            // Get set of archive submissions
            Set<Submission> archiveSubmissions = getSubmissions(archiveDirs, globPattern, tokenizer, recursive);
            toReturn = toReturn.setArchiveSubmissions(archiveSubmissions);
        }

        return toReturn;
    }

    /**
     * Build the collection of submissions Checksims will be run on.
     *
     * TODO add unit tests
     *
     * @param submissionDirs Directories to build submissions from
     * @param glob Glob matcher to use when building submissions
     * @param tokenizer Tokenizer to use when building submissions
     * @param recursive Whether to recursively traverse when building submissions
     * @return Collection of submissions which will be used to run Checksims
     * @throws IOException Thrown on issue reading files or traversing directories to build submissions
     */
    static Set<Submission> getSubmissions(Set<File> submissionDirs, String glob, Tokenizer tokenizer, boolean recursive)
            throws IOException, ChecksimsException {
        checkNotNull(submissionDirs);
        checkArgument(!submissionDirs.isEmpty(), "Must provide at least one submission directory!");
        checkNotNull(glob);
        checkNotNull(tokenizer);

        // Generate submissions to work on
        Set<Submission> submissions = new HashSet<>();
        for(File dir : submissionDirs) {
            submissions.addAll(Submission.submissionListFromDir(dir, glob, tokenizer, recursive));
        }

        return submissions;
    }

    /**
     * Parse CLI arguments into a ChecksimsConfig.
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

        // Parse options, first round: nothing required, so we can check for --help and --version
        CommandLine cli = parseOpts(args, false);

        // Print CLI Help
        if(cli.hasOption("h")) {
            printHelp();
        }

        // Print version
        if(cli.hasOption("version")) {
            System.err.println("Checksims version " + ChecksimsRunner.getChecksimsVersion());
            System.exit(0);
        }

        // Parse options, second round: required arguments are required
        cli = parseOpts(args, true);

        // Parse verbose setting
        if(cli.hasOption("vv")) {
            logs = startLogger(2);
        } else if(cli.hasOption("v")) {
            logs = startLogger(1);
        } else {
            logs = startLogger(0);
        }

        // First, parse basic flags
        ChecksimsConfig config = parseBaseFlags(cli);

        // Parse file flags
        config = parseFileFlags(cli, config);

        logs.trace("CLI parsing complete!");

        return config;
    }
}
