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
import com.google.common.collect.ImmutableSet;
import edu.wpi.checksims.algorithm.AlgorithmRegistry;
import edu.wpi.checksims.algorithm.SimilarityDetector;
import edu.wpi.checksims.algorithm.commoncode.CommonCodeHandler;
import edu.wpi.checksims.algorithm.commoncode.CommonCodePassthroughHandler;
import edu.wpi.checksims.algorithm.preprocessor.SubmissionPreprocessor;
import edu.wpi.checksims.algorithm.similaritymatrix.output.MatrixPrinter;
import edu.wpi.checksims.algorithm.similaritymatrix.output.MatrixPrinterRegistry;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.token.TokenType;
import edu.wpi.checksims.util.output.OutputPrinter;
import edu.wpi.checksims.util.output.OutputToStdoutPrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.singletonList;

/**
 * Per-run configuration of Checksims
 */
public final class ChecksimsConfig {
    private SimilarityDetector algorithm;
    private TokenType tokenization;
    private ImmutableList<SubmissionPreprocessor> preprocessors;
    private ImmutableSet<Submission> submissions;
    private CommonCodeHandler commonCodeHandler;
    private ImmutableList<MatrixPrinter> outputPrinters;
    private OutputPrinter outputMethod;
    private int numThreads;

    private ChecksimsConfig(SimilarityDetector algorithm, TokenType tokenization,
                            List<SubmissionPreprocessor> preprocessors, Set<Submission> submissions,
                            CommonCodeHandler commonCodeHandler, List<MatrixPrinter> outputPrinters,
                            OutputPrinter outputMethod, int numThreads) {
        this.algorithm = algorithm;
        this.tokenization = tokenization;
        this.commonCodeHandler = commonCodeHandler;

        this.preprocessors = ImmutableList.copyOf(preprocessors);

        this.submissions = ImmutableSet.copyOf(submissions);

        this.outputPrinters = ImmutableList.copyOf(outputPrinters);
        this.outputMethod = outputMethod;
        this.numThreads = numThreads;
    }

    /**
     * Base constructor, returns default config
     */
    public ChecksimsConfig() {
        this.algorithm = AlgorithmRegistry.getInstance().getDefaultImplementation();
        this.tokenization = this.algorithm.getDefaultTokenType();
        this.submissions = ImmutableSet.copyOf(new ArrayList<>());
        this.preprocessors = ImmutableList.copyOf(new ArrayList<>());
        this.commonCodeHandler = CommonCodePassthroughHandler.getInstance();
        this.outputPrinters = ImmutableList.copyOf(
                singletonList(MatrixPrinterRegistry.getInstance().getDefaultImplementation()));
        this.outputMethod = OutputToStdoutPrinter.getInstance();
        this.numThreads = Runtime.getRuntime().availableProcessors();
    }

    private ChecksimsConfig getCopy() {
        return new ChecksimsConfig(algorithm, tokenization, preprocessors, submissions, commonCodeHandler,
                outputPrinters, outputMethod, numThreads);
    }

    /**
     * @param newAlgorithm New similarity detection algorithm to use
     * @return Copy of configuration with new detection algorithm
     */
    public ChecksimsConfig setAlgorithm(SimilarityDetector newAlgorithm) {
        checkNotNull(newAlgorithm);

        ChecksimsConfig newConfig = getCopy();
        newConfig.algorithm = newAlgorithm;

        return newConfig;
    }

    /**
     * @param newTokenization New tokenization algorithm to use
     * @return Copy of configuration with new tokenization algorithm
     */
    public ChecksimsConfig setTokenization(TokenType newTokenization) {
        checkNotNull(newTokenization);

        ChecksimsConfig newConfig = getCopy();
        newConfig.tokenization = newTokenization;

        return newConfig;
    }

    /**
     * @param newPreprocessors New list of preprocessors to apply. Can be empty.
     * @return Copy of configuration with new preprocessor list
     */
    public ChecksimsConfig setPreprocessors(List<SubmissionPreprocessor> newPreprocessors) {
        checkNotNull(newPreprocessors);

        // Ensure that preprocessors are unique
        // Can't use a set, we don't require preprocessors to implement equals() or hashCode() in sane ways
        Set<String> names = newPreprocessors.stream().map(SubmissionPreprocessor::getName).collect(Collectors.toSet());
        if(names.size() != newPreprocessors.size()) {
            throw new IllegalArgumentException("Preprocessors must be unique!");
        }

        ChecksimsConfig newConfig = getCopy();
        newConfig.preprocessors = ImmutableList.copyOf(newPreprocessors);

        return newConfig;
    }

    /**
     * @param newSubmissions New list of submissions to work on. Must contain at least 1 submission.
     * @return Copy of configuration with new submissions list
     */
    public ChecksimsConfig setSubmissions(Set<Submission> newSubmissions) {
        checkNotNull(newSubmissions);
        checkArgument(!newSubmissions.isEmpty(), "Must provide at least one valid submission to run on!");

        ChecksimsConfig newConfig = getCopy();
        newConfig.submissions = ImmutableSet.copyOf(newSubmissions);

        return newConfig;
    }

    /**
     * @param newHandler Handler for common code
     * @return Copy of configuration with new common code handler
     */
    public ChecksimsConfig setCommonCodeHandler(CommonCodeHandler newHandler) {
        checkNotNull(newHandler);

        ChecksimsConfig newConfig = getCopy();
        newConfig.commonCodeHandler = newHandler;

        return newConfig;
    }

    /**
     * @param newOutputPrinters List of output strategies to use. Cannot be empty.
     * @return Copy of configuration with new list of output strategies
     */
    public ChecksimsConfig setOutputPrinters(List<MatrixPrinter> newOutputPrinters) {
        checkNotNull(newOutputPrinters);
        checkArgument(!newOutputPrinters.isEmpty(), "Must provide at least one valid output printer!");

        // Ensure all printers are unique
        // Can't use a set, we don't require output strategies to implement equals() or hashCode() in sane ways
        Set<String> names = newOutputPrinters.stream().map(MatrixPrinter::getName).collect(Collectors.toSet());
        if(names.size() != newOutputPrinters.size()) {
            throw new IllegalArgumentException("Output printers must be unique!");
        }

        ChecksimsConfig newConfig = getCopy();
        newConfig.outputPrinters = ImmutableList.copyOf(newOutputPrinters);

        return newConfig;
    }

    /**
     * @param newOutputMethod How Checksims should present its output
     * @return Copy of configuration with new output method
     */
    public ChecksimsConfig setOutputMethod(OutputPrinter newOutputMethod) {
        checkNotNull(newOutputMethod);

        ChecksimsConfig newConfig = getCopy();
        newConfig.outputMethod = newOutputMethod;

        return newConfig;
    }

    /**
     * @param numThreads Number of threads to be used for parallel operations. Must be greater than 0.
     * @return Copy of configuration with new number of threads set
     */
    public ChecksimsConfig setNumThreads(int numThreads) {
        checkArgument(numThreads > 0, "Attempted to set number of threads to " + numThreads
                + " - must be positive integer!");

        ChecksimsConfig newConfig = getCopy();
        newConfig.numThreads = numThreads;

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
    public ImmutableSet<Submission> getSubmissions() {
        return submissions;
    }

    /**
     * @return Handler which will be used for common code removal
     */
    public CommonCodeHandler getCommonCodeHandler() {
        return commonCodeHandler;
    }

    /**
     * @return List of output methods requested
     */
    public ImmutableList<MatrixPrinter> getOutputPrinters() {
        return outputPrinters;
    }

    /**
     * @return Method by which Checksims will present its output
     */
    public OutputPrinter getOutputMethod() {
        return outputMethod;
    }

    /**
     * @return Number of threads that will be used for parallel operations
     */
    public int getNumThreads() {
        return numThreads;
    }

    @Override
    public String toString() {
        return "ChecksimConfig with algorithm " + algorithm.getName();
    }
}
