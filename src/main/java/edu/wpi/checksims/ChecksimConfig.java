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
import edu.wpi.checksims.algorithm.SimilarityDetector;
import edu.wpi.checksims.algorithm.output.OutputRegistry;
import edu.wpi.checksims.algorithm.output.SimilarityMatrixPrinter;
import edu.wpi.checksims.algorithm.preprocessor.SubmissionPreprocessor;
import edu.wpi.checksims.submission.ConcreteSubmission;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.token.TokenList;
import edu.wpi.checksims.token.TokenType;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Per-run configuration of Checksims
 */
public final class ChecksimConfig {
    private SimilarityDetector algorithm;
    private TokenType tokenization;
    private ImmutableList<SubmissionPreprocessor> preprocessors;
    private ImmutableList<Submission> submissions;
    private boolean removeCommonCode;
    private SimilarityDetector commonCodeRemovalAlgorithm;
    private Submission commonCode;
    private ImmutableList<SimilarityMatrixPrinter> outputPrinters;
    private boolean outputToFile;
    private File outputFile;

    private static final File NOT_EXIST = new File("NULL_FILE_DOES_NOT_EXIST");

    private ChecksimConfig(SimilarityDetector algorithm, TokenType tokenization, List<SubmissionPreprocessor> preprocessors,
                          List<Submission> submissions, boolean removeCommonCode, SimilarityDetector commonCodeRemovalAlgorithm,
                          Submission commonCode, List<SimilarityMatrixPrinter> outputPrinters, boolean outputToFile, File outputFile) {
        this.algorithm = algorithm;
        this.tokenization = tokenization;
        this.removeCommonCode = removeCommonCode;
        this.commonCodeRemovalAlgorithm = commonCodeRemovalAlgorithm;
        this.commonCode = commonCode;
        this.preprocessors = ImmutableList.copyOf(preprocessors);

        this.submissions = ImmutableList.copyOf(submissions);

        this.outputPrinters = ImmutableList.copyOf(outputPrinters);
        this.outputToFile = outputToFile;
        this.outputFile = outputFile;
    }

    /**
     * Base constructor, returns base config
     */
    public ChecksimConfig() {
        this.algorithm = AlgorithmRegistry.getInstance().getDefaultImplementation();
        this.tokenization = this.algorithm.getDefaultTokenType();
        this.submissions = ImmutableList.copyOf(new LinkedList<>());
        this.preprocessors = ImmutableList.copyOf(new LinkedList<>());
        this.removeCommonCode = false;
        this.commonCodeRemovalAlgorithm = this.algorithm;
        this.commonCode = new ConcreteSubmission("Empty/Null Submission", "", new TokenList(this.tokenization));
        this.outputPrinters = ImmutableList.copyOf(Arrays.asList(OutputRegistry.getInstance().getDefaultImplementation()));
        this.outputToFile = false;
        this.outputFile = NOT_EXIST;
    }

    /**
     * Check is this config is ready to be used
     */
    public void isReady() throws ChecksimException {
        if(submissions.isEmpty()) {
            throw new ChecksimException("No submissions provided - cannot run Checksims!");
        }

        if(outputPrinters.isEmpty()) {
            throw new ChecksimException("No output printers provided - cannot run Checksims!");
        }

        if(removeCommonCode && commonCode.getContentAsString().isEmpty()) {
            throw new ChecksimException("Common code removal specified but common code is empty - possible user error?");
        }

        if(outputToFile && this.outputFile.equals(NOT_EXIST)) {
            throw new ChecksimException("Output to file requested, but valid file not given!");
        }
    }

    private ChecksimConfig getCopy() {
        return new ChecksimConfig(algorithm, tokenization, preprocessors, submissions, removeCommonCode, commonCodeRemovalAlgorithm, commonCode, outputPrinters, outputToFile, outputFile);
    }

    /**
     * @param newAlgorithm New similarity detection algorithm to use
     * @return Copy of configuration with new detection algorithm
     */
    public ChecksimConfig setAlgorithm(SimilarityDetector newAlgorithm) {
        if(newAlgorithm == null) {
            throw new RuntimeException("Attempt to set Algorithm to null!");
        }

        ChecksimConfig newConfig = getCopy();
        newConfig.algorithm = newAlgorithm;

        return newConfig;
    }

    /**
     * @param newTokenization New tokenization algorithm to use
     * @return Copy of configuration with new tokenization algorithm
     */
    public ChecksimConfig setTokenization(TokenType newTokenization) {
        if(newTokenization == null) {
            throw new RuntimeException("Attempt to set Tokenization to null!");
        }

        ChecksimConfig newConfig = getCopy();
        newConfig.tokenization = newTokenization;

        return newConfig;
    }

    /**
     * @param newPreprocessors New list of preprocessors to apply
     * @return Copy of configuration with new preprocessor list
     */
    public ChecksimConfig setPreprocessors(List<SubmissionPreprocessor> newPreprocessors) {
        if(newPreprocessors == null) {
            throw new RuntimeException("Attempt to set Preprocessors to null!");
        }

        ChecksimConfig newConfig = getCopy();
        newConfig.preprocessors = ImmutableList.copyOf(newPreprocessors);

        return newConfig;
    }

    /**
     * @param newSubmissions New list of submissions to work on
     * @return Copy of configuration with new submissions list
     */
    public ChecksimConfig setSubmissions(List<Submission> newSubmissions) {
        if(newSubmissions == null) {
            throw new RuntimeException("Attempt to set Submissions to null!");
        }

        ChecksimConfig newConfig = getCopy();
        newConfig.submissions = ImmutableList.copyOf(newSubmissions);

        return newConfig;
    }

    /**
     * @param remove Whether to remove common code
     * @param newCommonCode Common code to remove; unused (and will not be saved!) if remove is false
     * @return Copy of configuration with new common code removal settings
     */
    public ChecksimConfig setCommonCodeRemoval(boolean remove, Submission newCommonCode) {
        if(remove && commonCode == null) {
            throw new RuntimeException("Attempt to set Common Code to null!");
        }

        ChecksimConfig newConfig = getCopy();
        newConfig.removeCommonCode = remove;
        if(remove) {
            newConfig.commonCode = newCommonCode;
        }

        return newConfig;
    }

    /**
     * @param newAlgorithm Algorithm to use for common code removal
     * @return Copy of configuration with new common code removal algorithm
     */
    public ChecksimConfig setCommonCodeRemovalAlgorithm(SimilarityDetector newAlgorithm) {
        if(newAlgorithm == null) {
            throw new RuntimeException("Attempt to set Common Code Removal Algorithm to null!");
        }

        ChecksimConfig newConfig = getCopy();
        newConfig.commonCodeRemovalAlgorithm = newAlgorithm;

        return newConfig;
    }

    /**
     * @param newOutputPrinters List of output strategies to use
     * @return Copy of configuration with new list of output strategies
     */
    public ChecksimConfig setOutputPrinters(List<SimilarityMatrixPrinter> newOutputPrinters) {
        if(newOutputPrinters == null) {
            throw new RuntimeException("Attempt to set Output Printers to null!");
        }

        ChecksimConfig newConfig = getCopy();
        newConfig.outputPrinters = ImmutableList.copyOf(newOutputPrinters);

        return newConfig;
    }

    /**
     * @param doOutputToFile Whether to output to a file (if not set, output will be to STDOUT)
     * @param outputTo File to output to. Ignored (and not saved!) if doOutputToFile is false
     * @return Copy of configuration with new file output config
     */
    public ChecksimConfig setOutputToFile(boolean doOutputToFile, File outputTo) {
        if(doOutputToFile && outputTo == null) {
            throw new RuntimeException("Attempt to set Output File to null!");
        }

        ChecksimConfig newConfig = getCopy();
        newConfig.outputToFile = doOutputToFile;
        if(doOutputToFile) {
            newConfig.outputFile = outputTo;
        }

        return newConfig;
    }

    /**
     * @return Similarity detection algorithm to use
     */
    public SimilarityDetector getAlgorithm() {
        return algorithm;
    }

    /**
     * @return Tokenization algorithm to use
     */
    public TokenType getTokenization() {
        return tokenization;
    }

    /**
     * @return List of preprocessors to use
     */
    public ImmutableList<SubmissionPreprocessor> getPreprocessors() {
        return preprocessors;
    }

    /**
     * @return List of submissions to run on
     */
    public ImmutableList<Submission> getSubmissions() {
        return submissions;
    }

    /**
     * @return Whether to perform common code removal
     */
    public boolean doRemoveCommonCode() {
        return removeCommonCode;
    }

    /**
     * @return Algorithm to use when removing common code
     */
    public SimilarityDetector getCommonCodeRemovalAlgorithm() {
        return commonCodeRemovalAlgorithm;
    }

    /**
     * @return Common code to remove
     */
    public Submission getCommonCode() {
        return commonCode;
    }

    /**
     * @return List of output methods requested
     */
    public ImmutableList<SimilarityMatrixPrinter> getOutputPrinters() {
        return outputPrinters;
    }

    /**
     * @return Whether to output to a file
     */
    public boolean doOutputToFile() {
        return outputToFile;
    }

    /**
     * @return File to output to
     */
    public File getOutputFile() {
        return outputFile;
    }

    @Override
    public String toString() {
        return "ChecksimConfig with algorithm " + algorithm.getName();
    }
}
