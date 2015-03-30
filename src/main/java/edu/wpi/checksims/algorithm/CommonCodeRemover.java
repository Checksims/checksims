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

package edu.wpi.checksims.algorithm;

import edu.wpi.checksims.ChecksimException;
import edu.wpi.checksims.submission.ConcreteSubmission;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.submission.ValidityIgnoringSubmission;
import edu.wpi.checksims.token.TokenList;
import edu.wpi.checksims.token.TokenType;
import edu.wpi.checksims.token.tokenizer.FileTokenizer;
import edu.wpi.checksims.util.UnorderedPair;
import edu.wpi.checksims.util.threading.ParallelAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Remove common code from submissions
 */
public final class CommonCodeRemover {
    private static final Logger logs = LoggerFactory.getLogger(CommonCodeRemover.class);

    private CommonCodeRemover() {}

    /**
     * Perform common code removal
     *
     * @param removeFrom Collection of submissions to remove common code from
     * @param common Common code to remove
     * @param algorithm Algorithm to use for common code removal
     * @return Submissions from removeFrom rebuilt without common code
     */
    public static Collection<Submission> removeCommonCodeFromSubmissions(Collection<Submission> removeFrom, Submission common, SimilarityDetector algorithm) {
        return ParallelAlgorithm.parallelCommonCodeRemoval(algorithm, common, removeFrom);
    }
}
