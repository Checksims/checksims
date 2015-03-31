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
import edu.wpi.checksims.algorithm.CommonCodeRemover;
import edu.wpi.checksims.algorithm.output.SimilarityMatrix;
import edu.wpi.checksims.algorithm.output.SimilarityMatrixPrinter;
import edu.wpi.checksims.algorithm.preprocessor.PreprocessSubmissions;
import edu.wpi.checksims.algorithm.preprocessor.SubmissionPreprocessor;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.util.file.FileStringWriter;
import edu.wpi.checksims.util.output.OutputPrinter;
import edu.wpi.checksims.util.threading.ParallelAlgorithm;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Entry point for Checksims
 */
public class ChecksimsRunner {
    private static Logger logs;

    /**
     * CLI entrypoint of Checksims
     *
     * @param args CLI arguments
     */
    public static void main(String[] args) {
        ChecksimsConfig config;

        try {
            config = ChecksimsCommandLine.parseCLI(args);
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing command-line options", e);
        } catch (ChecksimsException e) {
            throw new RuntimeException("Error interpreting command-line options", e);
        }

        logs = LoggerFactory.getLogger(ChecksimsRunner.class);

        runChecksims(config);

        System.exit(0);
    }

    /**
     * Main public entrypoint to Checksims. Runs similarity detection according to given configuration.
     *
     * @param config Configuration defining how Checksims will be run
     */
    public static void runChecksims(ChecksimsConfig config) {
        // Check to see that the config is usable and user-specified CLI opts are good
        try {
            config.isReady();
        } catch(ChecksimsException e) {
            logs.error("Error: invalid run configuration specified!");
            throw new RuntimeException(e);
        }

        // Set parallelism
        int threads = config.getNumThreads();
        ParallelAlgorithm.setThreadCount(threads);
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "" + threads);

        ImmutableList<Submission> submissions = config.getSubmissions();

        logs.info("Got " + submissions.size() + " submissions to test.");

        if(submissions.size() == 0) {
            logs.error("No student submissions were found! Nothing to do!");
            System.exit(0);
        }

        // Apply the common code handler (which may just be a pass-through operation, if there is no common code)
        submissions = ImmutableList.copyOf(config.getCommonCodeHandler().handleCommonCode(submissions));

        // Apply all preprocessors
        for(SubmissionPreprocessor p : config.getPreprocessors()) {
            submissions = ImmutableList.copyOf(PreprocessSubmissions.process(p::process, submissions));
        }

        // Apply algorithm to submission
        SimilarityMatrix results = SimilarityMatrix.generate(submissions, config.getAlgorithm());

        // Output using all output printers
        OutputPrinter printer = config.getOutputMethod();
        for(SimilarityMatrixPrinter p : config.getOutputPrinters()) {
            logs.info("Generating " + p.getName() + " output");

            printer.print(results, p);
        }
    }
}
