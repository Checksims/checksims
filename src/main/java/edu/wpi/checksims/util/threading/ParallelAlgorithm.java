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

package edu.wpi.checksims.util.threading;

import edu.wpi.checksims.algorithm.AlgorithmResults;
import edu.wpi.checksims.algorithm.SimilarityDetector;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.util.UnorderedPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Apply a given algorithm to a given set of data in parallel
 */
public final class ParallelAlgorithm {
    private ParallelAlgorithm() {}

    private static Logger logs = LoggerFactory.getLogger(ParallelAlgorithm.class);

    private static int threadCount = Runtime.getRuntime().availableProcessors();

    /**
     * @param threads Number of threads to be used for execution
     */
    public static void setThreadCount(int threads) {
        threadCount = threads;
    }

    /**
     * @return Number of threads to be used for execution
     */
    public static int getThreadCount() {
        return threadCount;
    }

    /**
     * Remove common code in parallel
     *
     * @param algorithm Algorithm to use for common code removal
     * @param common Common code to remove
     * @param submissions Submissions to remove from
     * @return Submissions with common code removed
     */
    public static Collection<Submission> parallelCommonCodeRemoval(SimilarityDetector algorithm, Submission common, Collection<Submission> submissions) {
        Collection<CommonCodeRemovalWorker> workers = submissions.stream().map((submission) -> new CommonCodeRemovalWorker(algorithm, common, submission)).collect(Collectors.toList());

        return executeTasks(workers);
    }

    /**
     * Detect similarities in parallel
     *
     * @param algorithm Algorithm to use for similarity detection
     * @param pairs Pairs of submissions to perform detection on
     * @return Collection of results, one for each pair
     */
    public static Collection<AlgorithmResults> parallelSimilarityDetection(SimilarityDetector algorithm, Collection<UnorderedPair<Submission>> pairs) {
        // Map the pairs to ChecksimsWorker instances
        Collection<SimilarityDetectionWorker> workers = pairs.stream().map((pair) -> new SimilarityDetectionWorker(algorithm, pair)).collect(Collectors.toList());

        return executeTasks(workers);
    }

    /**
     * Internal backend: Execute given tasks on a new thread pool
     *
     * Expects Callable tasks, with non-void returns. If the need for void returning functions emerges, might need
     * another version of this?
     *
     * @param tasks Tasks to execute
     * @param <T> Type returned by the tasks
     * @return Collection of Ts
     */
    private static <T, T2 extends Callable<T>> Collection<T> executeTasks(Collection<T2> tasks) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(threadCount, threadCount, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new ThreadPoolExecutor.AbortPolicy());

        logs.info("Starting work using " + threadCount + " threads.");

        // Invoke the executor on all the worker instances
        try {
            // Create a monitoring thread to show progress
            MonitorThread monitor = new MonitorThread(executor);
            Thread monitorThread = new Thread(monitor);
            monitorThread.start();

            List<Future<T>> results = executor.invokeAll(tasks);

            executor.shutdown();

            while(!executor.isTerminated()) {
                Thread.sleep(250);
            }

            // Stop the monitor
            monitor.shutDown();

            // All tasks should now be done, let's build a results list from the futures
            return results.stream().map((future) -> {
                try {
                    return future.get();
                } catch (InterruptedException|ExecutionException e) {
                    logs.error("Error unpacking future!");
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());
        } catch (InterruptedException e) {
            logs.error("Execution of Checksims was interrupted!");
            throw new RuntimeException(e);
        } catch (RejectedExecutionException e) {
            logs.error("Could not schedule execution of all comparisons --- possibly too few resources available?");
            throw new RuntimeException(e);
        }
    }
}
