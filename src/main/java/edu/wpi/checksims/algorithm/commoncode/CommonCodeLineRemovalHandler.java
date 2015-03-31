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

import edu.wpi.checksims.ChecksimsException;
import edu.wpi.checksims.algorithm.AlgorithmRegistry;
import edu.wpi.checksims.algorithm.SimilarityDetector;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.util.threading.ParallelAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Remove common code from input submissions using the LineSimilarityChecker algorithm
 */
public class CommonCodeLineRemovalHandler implements CommonCodeHandler {
    private final Submission common;
    private final SimilarityDetector lineCompare;

    /**
     * Instantiate a new common code remover
     *
     * @param common Common code to remove
     */
    public CommonCodeLineRemovalHandler(Submission common) {
        this.common = common;
        try {
            this.lineCompare = AlgorithmRegistry.getInstance().getImplementationInstance("linecompare");
        } catch(ChecksimsException e) {
            throw new RuntimeException("Could not obtain instance of LineCompare!", e);
        }
    }

    /**
     * Perform parallel common code removal using the Line Comparison algorithm
     *
     * @param input Submissions to handle common code within
     * @return Input submissions with lines contained in the common code removed
     */
    @Override
    public Collection<Submission> handleCommonCode(Collection<Submission> input) {
        Logger logs = LoggerFactory.getLogger(CommonCodeLineRemovalHandler.class);

        logs.info("Removing common code from " + input.size() + " submissions");

        // TODO consider making this a checked exception
        if(common.getContentAsString().isEmpty()) {
            logs.error("Common code is empty, skipping removal! Possible user error?");
            return input;
        }

        return ParallelAlgorithm.parallelCommonCodeRemoval(lineCompare, common, input);
    }

    @Override
    public String toString() {
        return "CommonCodeLineRemovalHandler using submission " + common.getName();
    }
}
