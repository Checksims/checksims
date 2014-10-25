package edu.wpi.checksims;

import edu.wpi.checksims.algorithm.SimilarityMatrix;
import edu.wpi.checksims.algorithm.SimilarityMatrixPrinter;
import edu.wpi.checksims.algorithm.SimilarityMatrixThresholdPrinter;
import edu.wpi.checksims.algorithm.linesimilarity.LineSimilarityChecker;
import edu.wpi.checksims.algorithm.preprocessor.PreprocessSubmissions;
import edu.wpi.checksims.algorithm.preprocessor.StringLowercasePreprocessor;
import edu.wpi.checksims.algorithm.smithwaterman.SmithWaterman;
import edu.wpi.checksims.util.file.FileLineSplitter;
import edu.wpi.checksims.util.file.FileWhitespaceSplitter;
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
        // CLI Argument Handling
        Options opts = new Options();
        Parser parser = new GnuParser();
        CommandLine cli = null;

        Option alg = new Option("a", "algorithm", true, "algorithm to use");
        Option out = new Option("o", "output", true, "output format");
        Option file = new Option("f", "file", true, "file to output to");
        Option token = new Option("t", "token", true, "what to tokenize into");
        Option preprocess = new Option("p", "preprocess", true, "preprocessors to apply");
        Option verbose = new Option("v", "verbose", false, "specify verbose output");

        opts.addOption(alg);
        opts.addOption(out);
        opts.addOption(file);
        opts.addOption(token);
        opts.addOption(preprocess);
        opts.addOption(verbose);

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

        List<Submission<String>> submissionsWhitespace = new LinkedList<>();
        List<Submission<String>> submissionsLine = new LinkedList<>();

        for(File f : submissionDirs) {
            submissionsWhitespace.addAll(Submission.submissionsFromDir(f, glob, FileWhitespaceSplitter.getInstance()));
            submissionsLine.addAll(Submission.submissionsFromDir(f, glob, FileLineSplitter.getInstance()));
        }

        // Preprocess with a single preprocessor
        submissionsWhitespace = PreprocessSubmissions.process((s) -> StringLowercasePreprocessor.getInstance().process(s), submissionsWhitespace);

        //SimilarityMatrixPrinter<String> p = new SimilarityMatrixAsMatrixPrinter<>();
        SimilarityMatrixPrinter<String> p = new SimilarityMatrixThresholdPrinter<>(0.50f);

        SimilarityMatrix<String> lineSimilarityMatrix = new SimilarityMatrix<>(submissionsLine, LineSimilarityChecker.getInstance());
        System.out.println("\n\nLine Similarity Results:");
        System.out.println(p.printMatrix(lineSimilarityMatrix));

        SimilarityMatrix<String> smithWatermanMatrix = new SimilarityMatrix<>(submissionsWhitespace, new SmithWaterman<>());
        System.out.println("Smith-Waterman Results:");
        System.out.println(p.printMatrix(smithWatermanMatrix));

        System.exit(0);
    }
}
