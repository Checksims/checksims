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
 * Copyright (c) 2014 Matthew Heon and Dolan Murvihill
 */

package edu.wpi.checksims.algorithm.preprocessor;

import edu.wpi.checksims.submission.Submission;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Apply a preprocessor (maps Submission to Submission) to a given list of submissions
 */
public class PreprocessSubmissions {
    private PreprocessSubmissions() {}

    /**
     * Apply a given mapping function to each submission in a list of submissions
     *
     * Is NOT expected to preserve tokenization validity, as these are to be applied before that is significant
     *
     * @param mapping Mapping function to apply. SHOULD NOT MUTATE THE EXISTING TOKEN - should return a new tokenization
     * @param submissions Input list of submissions to apply to
     * @return New list formed by applying the mapping function to each submission. Retains order of input list.
     */
    public static List<Submission> process(Function<Submission, Submission> mapping, List<Submission> submissions) {
        Supplier<List<Submission>> linkedListFactory = LinkedList::new;

        return submissions.stream().map(mapping::apply).collect(Collectors.toCollection(linkedListFactory));
    }
}
