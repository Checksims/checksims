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

package net.lldp.checksims.util.threading;

import net.lldp.checksims.algorithm.preprocessor.SubmissionPreprocessor;
import net.lldp.checksims.submission.Submission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Worker for parallel preprocessor application.
 */
public class PreprocessorWorker implements Callable<Submission> {
    private final Submission preprocess;
    private final SubmissionPreprocessor preprocessor;

    /**
     * Create a Callable worker to preprocess a single submission.
     *
     * @param toPreprocess Submission to preprocess
     * @param preprocessor Preprocessor to apply
     */
    public PreprocessorWorker(Submission toPreprocess, SubmissionPreprocessor preprocessor) {
        checkNotNull(toPreprocess);
        checkNotNull(preprocessor);

        this.preprocess = toPreprocess;
        this.preprocessor = preprocessor;
    }

    /**
     * Preprocesses given submission using given preprocessor.
     *
     * @return Result of preprocessing
     * @throws Exception Internal Algorithm Exception may be thrown if an error occurs while preprocessing
     */
    @Override
    public Submission call() throws Exception {
        Logger logs = LoggerFactory.getLogger(PreprocessorWorker.class);

        logs.trace("Preprocessing submission " + preprocess.getName() + " with preprocessor " + preprocessor.getName());

        return preprocessor.process(preprocess);
    }

    @Override
    public String toString() {
        return "Preprocessor worker for submission " + preprocess.getName();
    }
}
