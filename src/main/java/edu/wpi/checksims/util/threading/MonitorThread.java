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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Monitor thread for parallel execution jobs
 */
class MonitorThread implements Runnable {
    private final ThreadPoolExecutor toMonitor;
    private boolean doRun;
    long currentComplete;
    long total;

    private static Logger logs = LoggerFactory.getLogger(MonitorThread.class);

    MonitorThread(ThreadPoolExecutor toMonitor) {
        checkNotNull(toMonitor);

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
                logs.info("Processed " + currentComplete + "/" + total + " tasks");
            }

            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                logs.error("Interrupted while sleeping!");
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String toString() {
        return "Monitoring thread for a ThreadPoolExecutor";
    }
}
