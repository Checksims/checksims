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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.lldp.checksims.algorithm.AlgorithmRegistry;
import net.lldp.checksims.algorithm.SimilarityDetector;
import net.lldp.checksims.algorithm.preprocessor.SubmissionPreprocessor;
import net.lldp.checksims.algorithm.similaritymatrix.output.MatrixPrinter;
import net.lldp.checksims.algorithm.similaritymatrix.output.MatrixPrinterRegistry;
import net.lldp.checksims.submission.Submission;
import net.lldp.checksims.token.TokenType;
import net.lldp.checksims.util.output.OutputPrinter;
import net.lldp.checksims.util.output.OutputToStdoutPrinter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.singletonList;

/**
 * Per-run configuration of Checksims.
 *
 * TODO: add a setImmutable method (or an immutable wrapper?) so ChecksimsRunner cannot alter a running config
 */
public final class ChecksimsConfig {
    private SimilarityDetector algorithm;
    private TokenType tokenization;
    private ImmutableList<SubmissionPreprocessor> preprocessors;
    private ImmutableSet<Submission> submissions;
    private ImmutableSet<Submission> archiveSubmissions;
    private ImmutableList<MatrixPrinter> outputPrinters;
    private OutputPrinter outputMethod;
    private int numThreads;

    /**
     * Base constructor, returns default config.
     */
    public ChecksimsConfig() {
        this.algorithm = AlgorithmRegistry.getInstance().getDefaultImplementation();
        this.tokenization = this.algorithm.getDefaultTokenType();
        this.submissions = ImmutableSet.copyOf(new HashSet<>());
        this.archiveSubmissions = ImmutableSet.copyOf(new HashSet<>());
        this.preprocessors = ImmutableList.copyOf(new ArrayList<>());
        this.outputPrinters = ImmutableList.copyOf(
                singletonList(MatrixPrinterRegistry.getInstance().getDefaultImplementation()));
        this.outputMethod = OutputToStdoutPrinter.getInstance();
        this.numThreads = Runtime.getRuntime().availableProcessors();
    }

    /**
     * Copy constructor.
     *
     * @param old Config to copy
     */
    public ChecksimsConfig(ChecksimsConfig old) {
        this.algorithm = old.getAlgorithm();
        this.tokenization = old.getTokenization();
        this.submissions = old.getSubmissions();
        this.archiveSubmissions = old.getArchiveSubmissions();
        this.preprocessors = old.getPreprocessors();
        this.outputPrinters = old.getOutputPrinters();
        this.outputMethod = old.getOutputMethod();
        this.numThreads = old.getNumThreads();
    }

    /**
     * @param newAlgorithm New similarity detection algorithm to use
     * @return This configuration
     */
    public ChecksimsConfig setAlgorithm(SimilarityDetector newAlgorithm) {
        checkNotNull(newAlgorithm);

        algorithm = newAlgorithm;

        return this;
    }

    /**
     * @param newTokenization New tokenization algorithm to use
     * @return This configuration
     */
    public ChecksimsConfig setTokenization(TokenType newTokenization) {
        checkNotNull(newTokenization);

        tokenization = newTokenization;

        return this;
    }

    /**
     * @param newPreprocessors New list of preprocessors to apply. Can be empty.
     * @return This configuration
     */
    public ChecksimsConfig setPreprocessors(List<SubmissionPreprocessor> newPreprocessors) {
        checkNotNull(newPreprocessors);

        // Ensure that preprocessors are unique
        // Can't use a set, we don't require preprocessors to implement equals() or hashCode() in sane ways
        Set<String> names = newPreprocessors.stream().map(SubmissionPreprocessor::getName).collect(Collectors.toSet());
        if(names.size() != newPreprocessors.size()) {
            throw new IllegalArgumentException("Preprocessors must be unique!");
        }

        preprocessors = ImmutableList.copyOf(newPreprocessors);

        return this;
    }

    /**
     * @param newSubmissions New list of submissions to work on. Must contain at least 1 submission.
     * @return This configuration
     */
    public ChecksimsConfig setSubmissions(Set<Submission> newSubmissions) {
        checkNotNull(newSubmissions);
        checkArgument(!newSubmissions.isEmpty(), "Must provide at least one valid submission to run on!");

        submissions = ImmutableSet.copyOf(newSubmissions);

        return this;
    }

    /**
     * @param newArchiveSubmissions New list of archive submissions to use. May be empty.
     * @return This configuration
     */
    public ChecksimsConfig setArchiveSubmissions(Set<Submission> newArchiveSubmissions) {
        checkNotNull(newArchiveSubmissions);

        this.archiveSubmissions = ImmutableSet.copyOf(newArchiveSubmissions);

        return this;
    }

    /**
     * @param newOutputPrinters List of output strategies to use. Cannot be empty.
     * @return This configuration
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

        outputPrinters = ImmutableList.copyOf(newOutputPrinters);

        return this;
    }

    /**
     * @param newOutputMethod How Checksims should present its output
     * @return This configuration
     */
    public ChecksimsConfig setOutputMethod(OutputPrinter newOutputMethod) {
        checkNotNull(newOutputMethod);

        outputMethod = newOutputMethod;

        return this;
    }

    /**
     * @param newNumThreads Number of threads to be used for parallel operations. Must be greater than 0.
     * @return Copy of configuration with new number of threads set
     */
    public ChecksimsConfig setNumThreads(int newNumThreads) {
        checkArgument(newNumThreads > 0, "Attempted to set number of threads to " + newNumThreads
                + " - must be positive integer!");

        numThreads = newNumThreads;

        return this;
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
     * @return Set of submissions to run on
     */
    public ImmutableSet<Submission> getSubmissions() {
        return submissions;
    }

    /**
     * @return Set of archive submissions to run on
     */
    public ImmutableSet<Submission> getArchiveSubmissions() {
        return archiveSubmissions;
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

    @Override
    public int hashCode() {
        return submissions.hashCode() ^ archiveSubmissions.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof ChecksimsConfig)) {
            return false;
        }

        ChecksimsConfig otherConfig = (ChecksimsConfig)other;

        return this.algorithm.equals(otherConfig.getAlgorithm())
                && this.archiveSubmissions.equals(otherConfig.getArchiveSubmissions())
                && this.numThreads == otherConfig.getNumThreads()
                && this.outputPrinters.equals(otherConfig.getOutputPrinters())
                && this.outputMethod.equals(otherConfig.getOutputMethod())
                && this.preprocessors.equals(otherConfig.getPreprocessors())
                && this.submissions.equals(otherConfig.getSubmissions())
                && this.tokenization.equals(otherConfig.getTokenization());
    }
}
