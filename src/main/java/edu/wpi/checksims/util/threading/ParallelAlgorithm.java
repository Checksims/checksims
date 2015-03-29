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
 * Apply a given similarity detection algorithm to a given set of unordered pairs
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

    public static Collection<AlgorithmResults> applyAlgorithm(SimilarityDetector algorithm, Collection<UnorderedPair<Submission>> pairs) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(threadCount, threadCount, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new ThreadPoolExecutor.AbortPolicy());

        logs.info("Starting similarity detection using " + threadCount + " threads.");

        // Map the pairs to ChecksimsWorker instances
        Collection<ChecksimsWorker> workers = pairs.stream().map((pair) -> new ChecksimsWorker(algorithm, pair)).collect(Collectors.toList());

        // Invoke the executor on all the ChecksimsWorker instances
        try {
            // Create a monitoring thread to show progress
            MonitorThread monitor = new MonitorThread(executor);
            Thread monitorThread = new Thread(monitor);
            monitorThread.start();

            List<Future<AlgorithmResults>> results = executor.invokeAll(workers);

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

class MonitorThread implements Runnable {
    private final ThreadPoolExecutor toMonitor;
    private boolean doRun;
    long currentComplete;
    long total;

    private static Logger logs = LoggerFactory.getLogger(MonitorThread.class);

    MonitorThread(ThreadPoolExecutor toMonitor) {
        this.toMonitor = toMonitor;
        doRun = true;
        currentComplete = 0;
        total = toMonitor.getTaskCount();
    }

    public void shutDown() {
        this.doRun = false;
    }

    @Override
    public void run() {
        while(doRun) {
            long newComplete = toMonitor.getCompletedTaskCount();
            total = toMonitor.getTaskCount();

            // Only print if we have an update
            if(newComplete != currentComplete) {
                currentComplete = newComplete;
                logs.info("Processed " + currentComplete + "/" + total + " pairs");
            }

            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                logs.error("Interrupted while sleeping!");
                throw new RuntimeException(e);
            }
        }
    }
}