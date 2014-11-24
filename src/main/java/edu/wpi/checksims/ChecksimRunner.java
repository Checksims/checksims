package edu.wpi.checksims;

import edu.wpi.checksims.algorithm.AlgorithmRegistry;
import edu.wpi.checksims.algorithm.CommonCodeRemover;
import edu.wpi.checksims.algorithm.PlagiarismDetector;
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
        Option help = new Option("h", "help", false, "show usage information");
        Option common = new Option("c", "common", true, "remove common code contained in given directory");

        opts.addOption(alg);
        opts.addOption(token);
        opts.addOption(out);
        opts.addOption(file);
        opts.addOption(preprocess);
        opts.addOption(jobs);
        opts.addOption(verbose);
        opts.addOption(help);
        opts.addOption(common);

        return opts;
    }

    // Parse a given set of CLI arguments
    static CommandLine parseOpts(String[] args) throws ParseException {
        Parser parser = new GnuParser();

        // Parse the CLI args
        return parser.parse(getOpts(), args);
    }

    static Logger startLogger(boolean verbose) {
        if(verbose) {
            // Set verbose logging level
            System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
        } else {
            System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO");
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

            System.err.println("\nSupported Plagiarism Detection Algorithms:");
            AlgorithmRegistry.getInstance().getSupportedAlgorithmNames().stream().forEach((name) -> System.err.print(name + ", "));
            System.err.println("\nDefault algorithm is " + AlgorithmRegistry.getInstance().getDefaultAlgorithmName());

            System.err.println("\nSupported Output Strategies:");
            OutputRegistry.getInstance().getAllOutputStrategyNames().stream().forEach((name) -> System.err.print(name + ", "));
            System.err.println("\nDefault strategy is " + AlgorithmRegistry.getInstance().getDefaultAlgorithmName());

            System.err.println("\nAvailable Preprocessors:");
            PreprocessorRegistry.getInstance().getPreprocessorNames().stream().forEach((name) -> System.err.print(name + ", "));
            System.err.println();


            // TODO print supported algorithms, output strategies, preprocessors
            // And defaults of those too!

            System.exit(0);
        }

        // Parse verbose setting
        logs = startLogger(cli.hasOption("v"));

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

        // Parse plagiarism detection algorithm
        PlagiarismDetector algorithm;
        if(cli.hasOption("a")) {
            try {
                algorithm = AlgorithmRegistry.getInstance().getAlgorithmInstance(cli.getOptionValue("a"));
            } catch(ChecksimException e) {
                logs.error("Error obtaining algorithm!");
                throw new RuntimeException(e);
            }
        } else {
            algorithm = AlgorithmRegistry.getInstance().getDefaultAlgorithm();
        }

        TokenType tokenization;
        if(cli.hasOption("t")) {
            try {
                tokenization = TokenType.fromString(cli.getOptionValue("t"));
            } catch(ChecksimException e) {
                logs.error("Error obtaining tokenization!");
                throw new RuntimeException(e);
            }
        } else {
            // If the user didn't specify, use the algorithm's default tokenization
            tokenization = algorithm.getDefaultTokenType();
        }

        // Parse common code detection
        boolean removeCommonCode = cli.hasOption("c");
        File commonCodeDirectory = null;
        // TODO may be desirable for this to be configurable
        // For now default to the same algorithm used for actual detection
        PlagiarismDetector commonCodeRemovalAlgorithm = algorithm;
        if(removeCommonCode) {
            commonCodeDirectory = new File(cli.getOptionValue("c"));
            logs.info("Removing common code (given in directory " + commonCodeDirectory.getName() + ")");
        }

        // Parse file output value
        boolean outputToFile = cli.hasOption("f");
        File outputFileAsFile = null;
        if(outputToFile) {
            outputFileAsFile = new File(cli.getOptionValue("f"));
            logs.info("Saving output to file " + outputFileAsFile.getName());
        }

        if(cli.hasOption("j")) {
            int threads = Integer.parseInt(cli.getOptionValue("j"));

            if(threads < 1) {
                logs.error("Invalid job count specified!");
                throw new RuntimeException("Must specify positive number of threads - got " + threads);
            }

            System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "" + threads);
        }

        List<SubmissionPreprocessor> preprocessors = new LinkedList<>();
        if(cli.hasOption("p")) {
            String[] splitPreprocessors = cli.getOptionValue("p").split(",");
            try {
                for (String s : splitPreprocessors) {
                    SubmissionPreprocessor p = PreprocessorRegistry.getInstance().getPreprocessor(s);
                    preprocessors.add(p);
                }
            } catch(ChecksimException e) {
                logs.error("Error applying preprocessor!");
                throw new RuntimeException(e);
            }
        }

        SimilarityMatrixPrinter outputPrinter;
        if(cli.hasOption("o")) {
            try {
                outputPrinter = OutputRegistry.getInstance().getOutputStrategy(cli.getOptionValue("o"));
            } catch(ChecksimException e) {
                logs.error("Error obtaining output strategy!");
                throw new RuntimeException(e);
            }
        } else {
            outputPrinter = OutputRegistry.getInstance().getDefaultStrategy();
        }

        ChecksimConfig config = new ChecksimConfig(algorithm, tokenization, preprocessors, submissionDirs, removeCommonCode, commonCodeRemovalAlgorithm, commonCodeDirectory, glob,
                outputPrinter, outputToFile, outputFileAsFile);

        runChecksims(config);

        System.exit(0);
    }

    public static void runChecksims(ChecksimConfig config) {
        FileTokenizer tokenizer = FileTokenizer.getTokenizer(config.tokenization);

        List<Submission> submissions = new LinkedList<>();

        try {
            for (File f : config.submissionDirectories) {
                submissions.addAll(Submission.submissionListFromDir(f, config.globMatcher, tokenizer));
            }
        } catch(IOException e) {
            logs.error("Error creating submissions from directory!");
            throw new RuntimeException(e);
        }

        // If we are performing common code detection...
        if(config.removeCommonCode) {
            // Create a submission for the common code
            Submission common;
            try {
                common = Submission.submissionFromDir(config.commonCodeDirectory, config.globMatcher, tokenizer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Perform common code removal before preprocessor application
            submissions = CommonCodeRemover.removeCommonCodeFromSubmissionsInList(submissions, common, config.commonCodeRemovalAlgorithm);
        }

        // Apply all preprocessors
        for(SubmissionPreprocessor p : config.preprocessors) {
            submissions = PreprocessSubmissions.process(p::process, submissions);
        }

        // Apply algorithm to submission
        SimilarityMatrix results = SimilarityMatrix.generate(submissions, config.algorithm);

        String output = config.outputPrinter.printMatrix(results);

        if(config.outputToFile) {
            try {
                FileStringWriter.writeStringToFile(config.outputFile, output);
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
