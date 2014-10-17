package edu.wpi.checksims;

import edu.wpi.checksims.algorithm.SimilarityMatrix;
import edu.wpi.checksims.algorithm.SimilarityMatrixAsMatrixPrinter;
import edu.wpi.checksims.algorithm.SimilarityMatrixPrinter;
import edu.wpi.checksims.algorithm.SimilarityMatrixThresholdPrinter;
import edu.wpi.checksims.algorithm.linesimilarity.LineSimilarityChecker;
import edu.wpi.checksims.algorithm.smithwaterman.SmithWaterman;
import edu.wpi.checksims.util.file.FileLineSplitter;
import edu.wpi.checksims.util.file.FileWhitespaceSplitter;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Entry point for Checksims
 */
public class ChecksimRunner {
    public static void main(String[] args) throws IOException {
        // TODO better CLI argument parsing
        // Interpret arg1 as match pattern
        // And arg2... as directories to check

        if(args.length < 2) {
            System.out.println("Expecting at least two arguments: File match glob, and folder(s) to check");
            System.exit(-1);
        }

        String glob = args[0];
        List<File> submissionDirs = new LinkedList<>();

        System.out.println("Got glob matcher as " + glob);

        for(int i = 1; i < args.length; i++) {
            System.out.println("Adding directory " + args[i]);
            submissionDirs.add(new File(args[i]));
        }

        List<Submission<String>> submissionsWhitespace = new LinkedList<>();
        List<Submission<String>> submissionsLine = new LinkedList<>();

        for(File f : submissionDirs) {
            submissionsWhitespace.addAll(Submission.submissionsFromDir(f, glob, FileWhitespaceSplitter.getInstance()));
            submissionsLine.addAll(Submission.submissionsFromDir(f, glob, FileLineSplitter.getInstance()));
        }

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
