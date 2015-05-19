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

package net.lldp.checksims.algorithm.preprocessor;

import net.lldp.checksims.ChecksimsException;
import net.lldp.checksims.submission.Submission;
import net.lldp.checksims.util.threading.ParallelAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Apply a preprocessor (maps Submission to Submission) to a given list of submissions.
 */
public final class PreprocessSubmissions {
    private PreprocessSubmissions() {}

    /**
     * Apply a given mapping function to each submission in a list of submissions.
     *
     * Is NOT expected to preserve tokenization validity, as these are to be applied before that is significant
     *
     * @param preprocessor Preprocessor to apply. SHOULD NOT MUTATE THE EXISTING TOKENS
     * @param submissions Input list of submissions to apply to
     * @return New list formed by applying the mapping function to each submission. Retains order of input list.
     */
    public static Set<Submission> process(SubmissionPreprocessor preprocessor, Set<Submission> submissions)
            throws ChecksimsException {
        checkNotNull(preprocessor);
        checkNotNull(submissions);

        Logger logs = LoggerFactory.getLogger(PreprocessSubmissions.class);

        logs.info("Preprocessing " + submissions.size() + " submissions with preprocessor " + preprocessor.getName());

        return ParallelAlgorithm.parallelSubmissionPreprocessing(preprocessor, submissions);
    }
}
