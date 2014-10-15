package edu.wpi.checksims.util;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Holds a pair of some type
 */
public class Pair<T> {
    public final T first;
    public final T second;

    public Pair(T first, T second) {
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
    public static <T> Set<Pair<T>> generatePairsFromList(List<T> input) {
        Set<Pair<T>> pairs = new HashSet<>();
        List<T> remaining = new LinkedList<>(input); // Ensure we don't mutate the input

        while(remaining.size() >= 2) {
            // Get the first element in the list, then remove it
            T toPair = remaining.get(0);
            remaining.remove(0);

            // For each remaining element, generate a pair consisting of it and the removed element
            remaining.stream().forEachOrdered((element) -> pairs.add(new Pair<>(toPair, element)));
        }

        return pairs;
    }

    /**
     * @param other Pair to compare against
     * @return True if other contains both elements in this pair, ordering independent
     */
    public boolean equalsIgnoreOrder(Pair<T> other) {
        return (other.first.equals(first) && other.second.equals(second)) ||
                (other.first.equals(second) && other.second.equals(first));
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Pair)) {
            return false;
        }

        Pair<T> otherPair = (Pair<T>)other;

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
