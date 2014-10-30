package edu.wpi.checksims;

import edu.wpi.checksims.algorithm.AlgorithmRegistry;
import edu.wpi.checksims.algorithm.PlagiarismDetector;
import edu.wpi.checksims.algorithm.output.SimilarityMatrix;
import edu.wpi.checksims.algorithm.output.SimilarityMatrixPrinter;
import edu.wpi.checksims.algorithm.output.SimilarityMatrixThresholdPrinter;
import edu.wpi.checksims.algorithm.preprocessor.SubmissionPreprocessor;
import edu.wpi.checksims.util.token.FileTokenizer;
import edu.wpi.checksims.util.token.TokenType;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Entry point for Checksims
 */
public class ChecksimRunner {
    public static void main(String[] args) throws IOException {
        // TODO should split CLI handling into separate function and add unit tests

        // CLI Argument Handling
        Options opts = new Options();
        Parser parser = new GnuParser();
        CommandLine cli = null;

        Option alg = new Option("a", "algorithm", true, "algorithm to use");
        Option out = new Option("o", "output", true, "output format");
        Option file = new Option("f", "file", true, "file to output to");
        Option preprocess = new Option("p", "preprocess", true, "preprocessors to apply");
        Option verbose = new Option("v", "verbose", false, "specify verbose output");
        Option help = new Option("h", "help", false, "show usage information");

        opts.addOption(alg);
        opts.addOption(out);
        opts.addOption(file);
        opts.addOption(preprocess);
        opts.addOption(verbose);
        opts.addOption(help);

        // Parse the CLI args
        try {
            cli = parser.parse(opts, args);
        } catch (ParseException e) {
            System.err.println("Error parsing CLI arguments: " + e.getMessage());
            System.exit(-1);
        }

        // Get unconsumed arguments
        String[] unusedArgs = cli.getArgs();

        if(unusedArgs.length < 2) {
            System.out.println("Expecting at least two arguments: File match glob, and folder(s) to check");
            System.exit(-1);
        }

        // First non-flag argument is the glob matcher
        // All the rest are directories containing student submissions
        String glob = unusedArgs[0];
        List<File> submissionDirs = new LinkedList<>();

        System.out.println("Got glob matcher as " + glob);

        for(int i = 1; i < unusedArgs.length; i++) {
            System.out.println("Adding directory " + args[i]);
            submissionDirs.add(new File(unusedArgs[i]));
        }

        // Parse plagiarism detection algorithm
        PlagiarismDetector algorithm = null;
        String algorithmName = cli.getOptionValue("a");
        if(algorithmName == null) {
            algorithm = AlgorithmRegistry.getInstance().getDefaultAlgorithm();
        } else {
            try {
                algorithm = AlgorithmRegistry.getInstance().getAlgorithmInstance(algorithmName);
            } catch(ChecksimException e) {
                System.err.println("Error getting algorithm: " + e.getMessage());
                System.exit(-1);
            }
        }

        // Parse file output value
        boolean outputToFile = false;
        String outputFile = cli.getOptionValue("f");
        File outputFileAsFile = null;
        if(outputFile != null) {
            outputToFile = true;
            outputFileAsFile = new File(outputFile);
        }

        boolean verboseLogging = false;
        // Parse verbose setting
        if(cli.hasOption("v")) {
            verboseLogging = true;
        }

        // TODO tokenization parsing
        TokenType tokenization = TokenType.WHITESPACE;

        // TODO preprocessor parsing
        List<SubmissionPreprocessor> preprocessors = new LinkedList<>();

        // TODO output method parsing
        SimilarityMatrixPrinter outputPrinter = new SimilarityMatrixThresholdPrinter(0.5f);

        ChecksimConfig config = new ChecksimConfig(algorithm, tokenization, preprocessors, submissionDirs, glob,
                verboseLogging, outputPrinter, outputToFile, outputFileAsFile);

        runChecksims(config);

        System.exit(0);
    }

    public static void runChecksims(ChecksimConfig config) {
        FileTokenizer tokenizer = FileTokenizer.getTokenizer(config.tokenization);

        List<Submission> submissions = new LinkedList<>();

        try {
            for (File f : config.submissionDirectories) {
                submissions.addAll(Submission.submissionsFromDir(f, config.globMatcher, tokenizer));
            }
        } catch(IOException e) {
            System.err.println("Error creating submissions from directory: " + e.getMessage());
            System.exit(-1);
        }

        // TODO apply submission preprocessors

        // Apply algorithm to submission
        SimilarityMatrix results = SimilarityMatrix.generate(submissions, config.algorithm);

        String output = config.outputPrinter.printMatrix(results);

        // TODO write output to file code

        System.out.println(output);
    }
}
