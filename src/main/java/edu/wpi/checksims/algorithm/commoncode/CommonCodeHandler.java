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

package edu.wpi.checksims.algorithm.commoncode;

import edu.wpi.checksims.submission.Submission;

import java.util.Set;

/**
 * A handler for Common Code Removal
 */
public interface CommonCodeHandler {
    /**
     * Handle common code in given submissions
     *
     * @param input Submissions to handle common code within
     * @return Result of processing submissions for common code
     */
    Set<Submission> handleCommonCode(Set<Submission> input);
}
