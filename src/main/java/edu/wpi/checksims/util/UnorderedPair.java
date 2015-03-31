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

package edu.wpi.checksims.util;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Holds a pair of some type
 *
 * Unlike the Apache Commons version, this represents an unordered pair of two members of the same datatype
 */
public class UnorderedPair<T> {
    public final T first;
    public final T second;

    public UnorderedPair(T first, T second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Generate a set of all valid unordered 2-tuples from an input list
     *
     * @param input List to generate from
     * @param <T> Generic parameter for the pairs
     * @return Set of Pairs containing all valid unordered pairings of 2 elements from the input list
     */
    public static <T> Set<UnorderedPair<T>> generatePairsFromList(List<T> input) {
        Set<UnorderedPair<T>> pairs = new HashSet<>();
        List<T> remaining = new LinkedList<>(input); // Ensure we don't mutate the input

        while(remaining.size() >= 2) {
            // Get the first element in the list, then remove it
            T toPair = remaining.get(0);
            remaining.remove(0);

            // For each remaining element, generate a pair consisting of it and the removed element
            remaining.stream().forEachOrdered((element) -> pairs.add(new UnorderedPair<>(toPair, element)));
        }

        return pairs;
    }

    /**
     * @param other Pair to compare against
     * @return True if other contains both elements in this pair, ordering independent
     */
    public boolean equalsIgnoreOrder(UnorderedPair<T> other) {
        return (other.first.equals(first) && other.second.equals(second)) ||
                (other.first.equals(second) && other.second.equals(first));
    }

    /**
     * Note that standard equality is order-dependent - UnorderedPair(a,b) will not be equal to UnorderedPair(b,a)!
     *
     * @param other Object to compare to
     * @return True if both objects are identical
     */
    @Override
    public boolean equals(Object other) {
        if(!(other instanceof UnorderedPair)) {
            return false;
        }

        // Due to type erasure, we can't cast to Pair<T>
        // Ignore generated compiler warning
        @SuppressWarnings("rawtypes")
        UnorderedPair otherPair = (UnorderedPair)other;

        return (otherPair.first.equals(first) && otherPair.second.equals(second));
    }

    @Override
    public int hashCode() {
        return first.hashCode() ^ second.hashCode();
    }

    @Override
    public String toString() {
        return "Pair of objects containing " + first + " and " + second;
    }
}
