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

import com.google.common.collect.ImmutableSet;
import net.lldp.checksims.algorithm.AlgorithmResults;
import net.lldp.checksims.algorithm.AlgorithmRunner;
import net.lldp.checksims.algorithm.InternalAlgorithmError;
import net.lldp.checksims.algorithm.similaritymatrix.SimilarityMatrix;
import net.lldp.checksims.algorithm.preprocessor.PreprocessSubmissions;
import net.lldp.checksims.algorithm.preprocessor.SubmissionPreprocessor;
import net.lldp.checksims.algorithm.similaritymatrix.output.MatrixPrinter;
import net.lldp.checksims.submission.Submission;
import net.lldp.checksims.util.PairGenerator;
import net.lldp.checksims.util.output.OutputPrinter;
import net.lldp.checksims.util.threading.ParallelAlgorithm;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Entry point for Checksims.
 */
public final class ChecksimsRunner {
    private static Logger logs;

    private ChecksimsRunner() {}

    /**
     * CLI entrypoint of Checksims.
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
        } catch(IOException e) {
            throw new RuntimeException("Error building submissions", e);
        }

        logs = LoggerFactory.getLogger(ChecksimsRunner.class);

        runChecksims(config);

        System.exit(0);
    }

    /**
     * @return Current version of Checksims
     */
    static String getChecksimsVersion() {
        InputStream resource = ChecksimsCommandLine.class.getResourceAsStream("version.txt");

        if(resource == null) {
            return "Error obtaining version number: could not obtain input stream for version.txt";
        }

        try {
            return IOUtils.toString(resource);
        } catch (IOException e) {
            return "Error obtaining version number: " + e.getMessage();
        }
    }

    /**
     * Main public entrypoint to Checksims. Runs similarity detection according to given configuration.
     *
     * @param config Configuration defining how Checksims will be run
     */
    public static void runChecksims(ChecksimsConfig config) {
        checkNotNull(config);

        // Set parallelism
        int threads = config.getNumThreads();
        ParallelAlgorithm.setThreadCount(threads);
        // TODO following line may not be necessary as we no longer use parallel streams?
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "" + threads);

        ImmutableSet<Submission> submissions = config.getSubmissions();

        logs.info("Got " + submissions.size() + " submissions to test.");

        ImmutableSet<Submission> archiveSubmissions = config.getArchiveSubmissions();

        if(!archiveSubmissions.isEmpty()) {
            logs.info("Got " + archiveSubmissions.size() + " archive submissions to test.");
        }

        if(submissions.size() == 0) {
            logs.error("No student submissions were found! Nothing to do!");
            System.exit(0);
        }

        // Apply the common code handler (which may just be a pass-through operation, if there is no common code)
        submissions = ImmutableSet.copyOf(config.getCommonCodeHandler().handleCommonCode(submissions));
        archiveSubmissions = ImmutableSet.copyOf(config.getCommonCodeHandler().handleCommonCode(archiveSubmissions));

        // Apply all preprocessors
        for(SubmissionPreprocessor p : config.getPreprocessors()) {
            submissions = ImmutableSet.copyOf(PreprocessSubmissions.process(p, submissions));
        }

        if(submissions.size() < 2) {
            logs.error("Not enough submissions for a pairwise comparison! Nothing to do!");
            System.exit(0);
        }

        // Apply algorithm to submissions
        Set<Pair<Submission, Submission>> allPairs = PairGenerator.generatePairsWithArchive(submissions,
                archiveSubmissions);
        Set<AlgorithmResults> results = AlgorithmRunner.runAlgorithm(allPairs, config.getAlgorithm());
        try {
            SimilarityMatrix resultsMatrix = SimilarityMatrix.generateMatrix(submissions, archiveSubmissions, results);

            // All parallel jobs are done, shut down the parallel executor
            ParallelAlgorithm.shutdownExecutor();

            // Output using all output printers
            OutputPrinter printer = config.getOutputMethod();
            for(MatrixPrinter p : config.getOutputPrinters()) {
                logs.info("Generating " + p.getName() + " output");

                printer.print(resultsMatrix, p);
            }
        } catch(InternalAlgorithmError e) {
            logs.error("Error generating Similarity Matrix!");
            logs.error(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
