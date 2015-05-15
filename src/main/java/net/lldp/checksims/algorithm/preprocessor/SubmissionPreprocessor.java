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

import net.lldp.checksims.algorithm.InternalAlgorithmError;
import net.lldp.checksims.submission.Submission;
import net.lldp.checksims.util.reflection.NamedInstantiable;

/**
 * Interface for submission preprocessors which act on submissions.
 *
 * The contract for PreprocessSubmissions() requests a Function from Submission<T> to Submission<T>,
 * which this can act as via a method reference.
 */
public interface SubmissionPreprocessor extends NamedInstantiable {
    /**
     * Perform some implementation-specific transformation on the input submission.
     *
     * @param submission Submission to transform
     * @return Result of transforming the input submission's contents
     * @throws InternalAlgorithmError Thrown on internal error preprocessing submission
     */
    Submission process(Submission submission) throws InternalAlgorithmError;
}
