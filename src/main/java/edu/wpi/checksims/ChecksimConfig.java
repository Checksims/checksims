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
 * Copyright (c) 2014 Matthew Heon and Dolan Murvihill
 */

package edu.wpi.checksims;

import com.google.common.collect.ImmutableList;
import edu.wpi.checksims.algorithm.SimilarityDetector;
import edu.wpi.checksims.algorithm.output.SimilarityMatrixPrinter;
import edu.wpi.checksims.algorithm.preprocessor.SubmissionPreprocessor;
import edu.wpi.checksims.token.TokenType;

import java.io.File;
import java.util.List;

/**
 * Non-persistent configuration of Checksims
 */
public final class ChecksimConfig {
    public final SimilarityDetector algorithm;
    public final TokenType tokenization;
    public final ImmutableList<SubmissionPreprocessor> preprocessors;
    public final ImmutableList<File> submissionDirectories;
    public final boolean recursive;
    public final boolean removeCommonCode;
    public final SimilarityDetector commonCodeRemovalAlgorithm;
    public final File commonCodeDirectory;
    public final String globMatcher;
    public final SimilarityMatrixPrinter outputPrinter;
    public final boolean outputToFile;
    public final File outputFile;

    public ChecksimConfig(SimilarityDetector algorithm, TokenType tokenization, List<SubmissionPreprocessor> preprocessors,
                          List<File> submissionDirectories, boolean recursive, boolean removeCommonCode, SimilarityDetector commonCodeRemovalAlgorithm,
                          File commonCodeDirectory, String globMatcher, SimilarityMatrixPrinter outputPrinter,
                          boolean outputToFile, File outputFile) {
        this.algorithm = algorithm;
        this.tokenization = tokenization;
        this.recursive = recursive;
        this.removeCommonCode = removeCommonCode;
        this.commonCodeRemovalAlgorithm = commonCodeRemovalAlgorithm;
        this.commonCodeDirectory = commonCodeDirectory;
        this.preprocessors = ImmutableList.copyOf(preprocessors);

        // TODO consider refactor to just accept a List of submissions - might make it easier to call from EvalSims
        this.globMatcher = globMatcher;
        this.submissionDirectories = ImmutableList.copyOf(submissionDirectories);

        this.outputPrinter = outputPrinter;
        this.outputToFile = outputToFile;
        this.outputFile = outputFile;
    }

    @Override
    public String toString() {
        return "ChecksimConfig with algorithm " + algorithm.getName();
    }
}
