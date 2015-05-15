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

package net.lldp.checksims.util;

import net.lldp.checksims.submission.Submission;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Generates unordered pairs of submissions.
 */
public final class PairGenerator {
    private PairGenerator() {}

    /**
     * Generate all possible unique, unordered pairs of submissions.
     *
     * @param submissions Submissions to generate pairs from
     * @return Set of all unique, unordered pairs of submissions
     */
    public static Set<Pair<Submission, Submission>> generatePairs(Set<Submission> submissions) {
        checkNotNull(submissions);
        checkArgument(submissions.size() >= 2, "Cannot generate pairs with less than 2 submissions!");

        Set<Pair<Submission, Submission>> pairs = new HashSet<>();

        List<Submission> remaining = new ArrayList<>();
        remaining.addAll(submissions);

        while(remaining.size() >= 2) {
            // Get the first submission in the list and remove it
            Submission first = remaining.get(0);
            remaining.remove(0);

            // Form a pair for every remaining submission by pairing with the first, removed submission
            for(Submission submission : remaining) {
                Pair<Submission, Submission> pair = Pair.of(first, submission);
                Pair<Submission, Submission> reversed = Pair.of(submission, first);

                // Something's wrong, we've made a duplicate pair (but reversed)
                // Should never happen
                if(pairs.contains(reversed)) {
                    throw new RuntimeException("Internal error in pair generation: duplicate pair produced!");
                }

                // Add the newly-generated pair to our return
                pairs.add(pair);
            }
        }

        return pairs;
    }

    /**
     * Generate all pairs for normal submissions, and pairs for archive submissions to compare to normal submissions.
     *
     * @param submissions Normal submissions - compared to each other and archive submissions
     * @param archiveSubmissions Archive submissions - only compared to normal submissions, not each other
     * @return Set of all unordered pairs required for comparison with archive directory
     */
    public static Set<Pair<Submission, Submission>> generatePairsWithArchive(Set<Submission> submissions,
                                                                             Set<Submission> archiveSubmissions) {
        checkNotNull(submissions);
        checkNotNull(archiveSubmissions);

        // TODO it may be desirable to allow comparison of a single submission to an archive
        // However, generatePairs fails if only 1 submission is given
        // (This would also require tweaks in the frontend)
        Set<Pair<Submission, Submission>> basePairs = generatePairs(submissions);

        // If we have no archive submissions, just return the same result generatePairs would
        if(archiveSubmissions.isEmpty()) {
            return basePairs;
        }

        // Now we need to add pairs for the archive submissions
        List<Submission> remaining = new ArrayList<>();
        remaining.addAll(archiveSubmissions);

        // Loop through each archive submission
        while(!remaining.isEmpty()) {
            Submission first = remaining.get(0);
            remaining.remove(0);

            // For each archive submission, generate pairs for each normal submission
            for(Submission s : submissions) {
                Pair<Submission, Submission> pair = Pair.of(first, s);
                Pair<Submission, Submission> reversed = Pair.of(s, first);

                // Something's wrong, we've made a duplicate pair (but reversed)
                // Should never happen
                if(basePairs.contains(reversed)) {
                    throw new RuntimeException("Internal error in pair generation: duplicate pair produced!");
                }

                // One pair for each normal submission, consisting of the archive submission and the normal submission
                basePairs.add(pair);
            }
        }

        return basePairs;
    }
}
