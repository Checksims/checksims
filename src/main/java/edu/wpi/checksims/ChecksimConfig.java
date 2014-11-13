package edu.wpi.checksims;

import com.google.common.collect.ImmutableList;
import edu.wpi.checksims.algorithm.PlagiarismDetector;
import edu.wpi.checksims.algorithm.output.SimilarityMatrixPrinter;
import edu.wpi.checksims.algorithm.preprocessor.SubmissionPreprocessor;
import edu.wpi.checksims.util.token.TokenType;

import java.io.File;
import java.util.List;

/**
 * Non-persistent configuration of Checksims
 */
public class ChecksimConfig {
    public final PlagiarismDetector algorithm;
    public final TokenType tokenization;
    public final List<SubmissionPreprocessor> preprocessors;
    public final List<File> submissionDirectories;
    public final String globMatcher;
    public final boolean verbose;
    public final SimilarityMatrixPrinter outputPrinter;
    public final boolean outputToFile;
    public final File outputFile;

    public ChecksimConfig(PlagiarismDetector algorithm, TokenType tokenization, List<SubmissionPreprocessor> preprocessors,
                          List<File> submissionDirectories, String globMatcher, boolean verbose,
                          SimilarityMatrixPrinter outputPrinter, boolean outputToFile, File outputFile) {
        this.algorithm = algorithm;
        this.tokenization = tokenization;
        this.preprocessors = ImmutableList.copyOf(preprocessors);

        // TODO consider refactor to just accept a List of submissions - might make it easier to call from EvalSims
        this.globMatcher = globMatcher;
        this.submissionDirectories = ImmutableList.copyOf(submissionDirectories);

        this.verbose = verbose;
        this.outputPrinter = outputPrinter;
        this.outputToFile = outputToFile;
        this.outputFile = outputFile;
    }

    @Override
    public String toString() {
        return "ChecksimConfig with algorithm " + algorithm.getName();
    }
}
