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

package net.lldp.checksims.algorithm.smithwaterman;

import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import net.lldp.checksims.algorithm.InternalAlgorithmError;
import net.lldp.checksims.token.Token;
import net.lldp.checksims.token.TokenList;
import net.lldp.checksims.token.ValidityEnsuringToken;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Actual implementation of the Smith-Waterman algorithm.
 */
public class SmithWatermanAlgorithm {
    private final TokenList xList;
    private final TokenList yList;
    private final ArraySubset wholeArray;
    private final ArraySubset wholeArrayBounds;
    private final int[][] s;
    private final int[][] m;
    private Map<Integer, Set<Coordinate>> candidates;

    private static Logger logs = LoggerFactory.getLogger(SmithWatermanAlgorithm.class);

    private static final int threshold = 5;
    private static final int swConstant = 1;

    /**
     * Prepare for a Smith-Waterman alignment.
     *
     * @param a First token list to align
     * @param b Second token list to align
     */
    public SmithWatermanAlgorithm(TokenList a, TokenList b) {
        checkNotNull(a);
        checkNotNull(b);
        checkArgument(!a.isEmpty(), "Cowardly refusing to perform alignment with empty token list A");
        checkArgument(!b.isEmpty(), "Cowardly refusing to perform alignment with empty token list B");

        xList = TokenList.cloneTokenList(a);
        yList = TokenList.cloneTokenList(b);

        wholeArray = ArraySubset.of(1, 1, xList.size() + 1, yList.size() + 1);
        wholeArrayBounds = ArraySubset.of(1, 1, xList.size(), yList.size());

        s = new int[wholeArray.getMax().getX()][wholeArray.getMax().getY()];
        m = new int[wholeArray.getMax().getX()][wholeArray.getMax().getY()];

        candidates = new HashMap<>();
    }

    /**
     * INTERNAL ONLY - for use in unit tests.
     *
     * @return Smith-Waterman S table
     */
    int[][] getS() {
        return s;
    }

    /**
     * INTERNAL ONLY - for use in unit tests.
     *
     * @return Smith-Waterman M table
     */
    int[][] getM() {
        return m;
    }

    /**
     * INTERNAL ONLY - for use in unit tests.
     *
     * @return ArraySubset containing bounds of entire array
     */
    ArraySubset getWholeArray() {
        return wholeArray;
    }

    /**
     * INTERNAL ONLY - for use in unit tests.
     *
     * @return Smith-Waterman match candidates
     */
    Map<Integer, Set<Coordinate>> getCandidates() {
        return candidates;
    }

    /**
     * INTERNAL ONLY - for use in unit tests.
     *
     * @return Token list along X axis
     */
    TokenList getXList() {
        return xList;
    }

    /**
     * INTERNAL ONLY - for use in unit tests.
     *
     * @return Token list along Y axis
     */
    TokenList getYList() {
        return yList;
    }

    /**
     * Compute a Smith-Waterman alignment through exhaustive (but more reliable) process.
     *
     * TODO tests for this (already tested through SmithWaterman)
     *
     * @return Pair of TokenList representing optimal alignments
     * @throws InternalAlgorithmError Thrown if internal error causes violation of preconditions
     */
    public Pair<TokenList, TokenList> computeSmithWatermanAlignmentExhaustive() throws InternalAlgorithmError {
        Map<Integer, Set<Coordinate>> localCandidates;

        // Keep computing while we have results over threshold
        do {
            // Recompute whole array
            localCandidates = computeArraySubset(wholeArray);

            if(localCandidates.isEmpty()) {
                break;
            }

            // Get the largest key
            int largestKey = Ordering.natural().max(localCandidates.keySet());

            // Get matching coordinates
            Set<Coordinate> largestCoords = localCandidates.get(largestKey);

            if(largestCoords == null || largestCoords.isEmpty()) {
                throw new InternalAlgorithmError("Error: largest key " + largestKey
                        + " maps to null or empty candidate set!");
            }

            // Arbitrarily break ties
            Coordinate chosenCoord = Iterables.get(largestCoords, 0);

            // Get match coordinates
            Set<Coordinate> matchCoords = getMatchCoordinates(chosenCoord);

            // Set match invalid
            setMatchesInvalid(matchCoords);
        } while(!localCandidates.isEmpty());

        // IntelliJ has an aversion to passing anything with a 'y' in it as the right side of a pair
        // This alleviates the warning
        //noinspection SuspiciousNameCombination
        return Pair.of(xList, yList);
    }

    /**
     * Compute a Smith-Waterman alignment.
     *
     * TODO tests for this
     *
     * @return Pair of Token Lists representing optimal detected alignments
     * @throws InternalAlgorithmError Thrown if internal error causes violation of preconditions
     */
    public Pair<TokenList, TokenList> computeSmithWatermanAlignment() throws InternalAlgorithmError {
        // Make sure our candidates list is initially empty
        candidates.clear();

        // Start by computing the entire array, and adding the results to candidates
        mergeIntoCandidates(computeArraySubset(wholeArray));

        // Go through all candidates
        while(!candidates.isEmpty()) {
            // Need to identify the largest key (largest value in the S-W array)
            int largestKey = Ordering.natural().max(candidates.keySet());

            // Get coordinate(s) with largest value in S-W array
            Set<Coordinate> largestCoords = candidates.get(largestKey);

            if(largestCoords == null || largestCoords.isEmpty()) {
                throw new InternalAlgorithmError("Null or empty mapping from largest coordinates!");
            }

            // Arbitrarily break ties, if they exist
            Coordinate currMax = Iterables.get(largestCoords, 0);

            // Check to verify that this match is over the threshold
            // This should never happen, so log if it does
            // TODO investigate why this is happening
            if(s[currMax.getX()][currMax.getY()] < threshold) {
                logs.trace("Potential algorithm error: identified candidate pointing to 0 at " + currMax);
                largestCoords.remove(currMax);
                if(largestCoords.isEmpty()) {
                    candidates.remove(largestKey);
                } else {
                    candidates.put(largestKey, largestCoords);
                }
                continue;
            }

            // Get match coordinates
            Set<Coordinate> coords = getMatchCoordinates(currMax);

            // Get match origin
            Coordinate currOrigin = getFirstMatchCoordinate(coords);

            if(currMax.equals(currOrigin)) {
                throw new InternalAlgorithmError("Maximum and Origin point to same point - " + currMax + " and " +
                        currOrigin + ". Size of match coordinates set is " + coords.size());
            }

            // Filter postdominated results
            candidates = filterPostdominated(currOrigin, currMax);

            // Set match invalid
            setMatchesInvalid(coords);

            // Zero the match
            zeroMatch(currOrigin, currMax);

            // Generate array subsets we need to recompute
            Set<ArraySubset> subsetsToCompute = generateSubsets(currOrigin, currMax);

            // Recompute given array subsets
            for(ArraySubset subset : subsetsToCompute) {
                mergeIntoCandidates(computeArraySubset(subset));
            }
        }

        // IntelliJ has an aversion to passing anything with a 'y' in it as the right side of a pair
        // This alleviates the warning
        //noinspection SuspiciousNameCombination
        return Pair.of(xList, yList);
    }

    /**
     * Generate subsets of the Smith-Waterman arrays that require recomputation.
     *
     * TODO unit tests for this once optimizations are added
     *
     * @param origin Origin of match requiring recomputation
     * @param max Max of match requiring recomputation
     * @return Set of array subsets requiring recomputation
     */
    Set<ArraySubset> generateSubsets(Coordinate origin, Coordinate max) {
        checkNotNull(origin);
        checkNotNull(max);
        checkArgument(wholeArray.contains(origin), "Origin of requested area out of bounds: " + origin
                + " not within " + wholeArray);
        checkArgument(wholeArray.contains(max), "Max of requested area out of bounds: " + max
                + " not within " + wholeArray);

        Set<ArraySubset> toRecompute = new HashSet<>();

        // There are potentially 4 zones we need to care about

        // First: above and to the left
        // Check if it exists
        if(origin.getX() > 1 && origin.getY() > 1) {
            toRecompute.add(ArraySubset.of(1, 1, origin.getX(), origin.getY()));
        }

        // Second: Above and to the right
        // Check if it exists
        if(max.getX() < (wholeArray.getMax().getX() - 1) && origin.getY() > 1) {
            toRecompute.add(ArraySubset.of(max.getX(), 1, wholeArray.getMax().getX(), origin.getY()));
        }

        // Third: Below and to the left
        // Check if it exists
        if(origin.getX() > 1 && max.getY() < (wholeArray.getMax().getY() - 1)) {
            toRecompute.add(ArraySubset.of(1, max.getY(), origin.getX(), wholeArray.getMax().getY() - 1));
        }

        // Fourth: Below and to the right
        // Check if it exists
        if(max.getX() < (wholeArray.getMax().getX() - 1) && max.getY() < (wholeArray.getMax().getY() - 1)) {
            toRecompute.add(ArraySubset.of(max.getX(), max.getY(), wholeArray.getMax().getX() - 1,
                    wholeArray.getMax().getY() - 1));
        }

        // If none of the subsets were added, we matched the entire array
        // Nothing to do here, just return
        if(toRecompute.isEmpty()) {
            return toRecompute;
        }

        // Now, if we DIDN'T match the entire array
        // We're going to want to narrow down these subsets
        // We can do this by removing invalid areas
        // TODO this optimization

        return toRecompute;
    }

    /**
     * Zero out the portion of S and M arrays that was matched.
     *
     * @param origin Origin of the match
     * @param max Endpoint of the match
     */
    void zeroMatch(Coordinate origin, Coordinate max) {
        checkNotNull(origin);
        checkNotNull(max);
        checkArgument(wholeArrayBounds.contains(origin), "Origin of requested area out of bounds: " + origin
                + " not within " + wholeArray);
        checkArgument(wholeArrayBounds.contains(max), "Max of requested area out of bounds: " + max
                + " not within " + wholeArray);

        int xLower = origin.getX();
        int xUpper = max.getX();

        // Zero out the X match
        for(int x = xLower; x <= xUpper; x++) {
            for(int y = 1; y < s[0].length; y++) {
                s[x][y] = 0;
                m[x][y] = 0;
            }
        }

        int yLower = origin.getY();
        int yUpper = max.getY();

        // Zero out the Y match
        for(int x = 1; x < s.length; x++) {
            for(int y = yLower; y <= yUpper; y++) {
                s[x][y] = 0;
                m[x][y] = 0;
            }
        }
    }

    /**
     * Filter postdominated results of a match.
     *
     * @param max Endpoint of match
     * @return Filtered version of candidate results set, with all results postdominated by match removed
     */
    Map<Integer, Set<Coordinate>> filterPostdominated(Coordinate origin, Coordinate max) {
        checkNotNull(origin);
        checkNotNull(max);
        checkArgument(wholeArray.contains(origin), "Origin of requested area out of bounds: " + origin + " not within "
                + wholeArray);
        checkArgument(wholeArray.contains(max), "Max of requested area out of bounds: " + max + " not within "
                + wholeArray);

        if(candidates.isEmpty()) {
            return candidates;
        }

        Map<Integer, Set<Coordinate>> filteredResults = new HashMap<>();

        // X match invalidation
        ArraySubset xInval = ArraySubset.of(origin.getX(), 0, max.getX(), wholeArray.getMax().getY());
        ArraySubset yInval = ArraySubset.of(0, origin.getY(), wholeArray.getMax().getX(), max.getY());

        // Loop through all candidates and see if they need to be filtered
        for(int key : candidates.keySet()) {
            Set<Coordinate> allCandidates = candidates.get(key);

            Set<Coordinate> newSet = new HashSet<>();

            for(Coordinate coord : allCandidates) {
                // Unclear how this candidate got added, but it's no longer valid
                // This shouldn't happen, so log it as well
                // TODO investigate why this is happening
                if(s[coord.getX()][coord.getY()] < threshold) {
                    logs.trace("Potential algorithm error - filtered match lower than threshold at " + coord);
                    continue;
                }

                // Identify the origin of the result
                Coordinate originOfCandidate = getFirstMatchCoordinate(getMatchCoordinates(coord));

                // If the origin is NOT the same as the given origin, it's a candidate
                if(!originOfCandidate.equals(origin)) {
                    // Also need to check if the origin and max are not within the rectangles identified
                    if(xInval.contains(coord)
                            || yInval.contains(coord)
                            || xInval.contains(max)
                            || yInval.contains(max)) {
                        newSet.add(coord);
                    }
                }
            }

            if(!newSet.isEmpty()) {
                // We didn't filter everything
                // Add the filtered set to our filtered results
                filteredResults.put(key, newSet);
            }
        }

        return filteredResults;
    }

    /**
     * Compute a subset of the array.
     *
     * @param toCompute Subset to recompute. Can be entire array, if desired.
     * @return Map containing all candidate results identified while computing
     */
    Map<Integer, Set<Coordinate>> computeArraySubset(ArraySubset toCompute) {
        checkNotNull(toCompute);
        checkArgument(wholeArray.contains(toCompute.getOrigin()), "Origin of subset out of bounds: "
                + toCompute.getOrigin() + " not within " + wholeArray);
        checkArgument(wholeArray.contains(toCompute.getMax()), "Maximum of subset out of bounds: "
                + toCompute.getMax() + " not within " + wholeArray);

        Map<Integer, Set<Coordinate>> newCandidates = new HashMap<>();

        for(int x = toCompute.getOrigin().getX(); x < toCompute.getMax().getX(); x++) {
            Token xToken = new ValidityEnsuringToken(xList.get(x - 1));

            for(int y = toCompute.getOrigin().getY(); y < toCompute.getMax().getY(); y++) {
                int prevX = x - 1;
                int prevY = y - 1;

                int newS;
                int newM;

                // Token Match - increment S table
                if(xToken.isValid() && xToken.equals(yList.get(y - 1))) {
                    int sPred = s[prevX][prevY];
                    int mPred = m[prevX][prevY];

                    newS = sPred + swConstant;

                    // Predecessors table is the largest of the S table or M table predecessors
                    if(sPred > mPred) {
                        newM = sPred;
                    } else {
                        newM = mPred;
                    }
                } else {
                    // Tokens did not match
                    // Get the max of S table predecessors and decrement
                    int a = s[prevX][prevY];
                    int b = s[prevX][y];
                    int c = s[x][prevY];

                    int max = getMaxOfInts(a, b, c);

                    newS = max - swConstant;

                    if(newS < 0) {
                        newS = 0;
                    }

                    // If S is 0, zero out the predecessor table entry
                    if(newS == 0) {
                        newM = 0;
                    } else {
                        int aM = m[prevX][prevY];
                        int bM = m[prevX][y];
                        int cM = m[x][prevY];

                        // Get largest predecessor in M table
                        int maxM = getMaxOfInts(aM, bM, cM);

                        // If S nonzero, predecessor table entry is largest of the predecessors in the S and M tables
                        if(max > maxM) {
                            newM = max;
                        } else {
                            newM = maxM;
                        }
                    }
                }

                // Check threshold
                if(newM - newS >= threshold) {
                    newM = 0;
                    newS = 0;
                }

                // Set S and M table entries
                s[x][y] = newS;
                m[x][y] = newM;

                // Check if we our result is significant
                if(newS >= threshold && newS > newM) {
                    // It's significant, add it to our results
                    if(newCandidates.containsKey(newS)) {
                        Set<Coordinate> valuesForKey = newCandidates.get(newS);

                        valuesForKey.add(Coordinate.of(x, y));
                    } else {
                        Set<Coordinate> valuesForKey = new HashSet<>();

                        valuesForKey.add(Coordinate.of(x, y));

                        newCandidates.put(newS, valuesForKey);
                    }
                }
            }
        }

        return newCandidates;
    }

    /**
     * Get the closest coordinate to the origin from a given set.
     *
     * @param coordinates Coordinates to search within
     * @return Closest coordinate to origin --- (0,0)
     */
    static Coordinate getFirstMatchCoordinate(Set<Coordinate> coordinates) {
        checkNotNull(coordinates);
        checkArgument(!coordinates.isEmpty(), "Cannot get first match coordinate as match set is empty!");

        if(coordinates.size() == 1) {
            return Iterables.get(coordinates, 0);
        }

        Coordinate candidate = Iterables.get(coordinates, 0);

        // Search for a set of coordinates closer to the origin
        for(Coordinate coord : coordinates) {
            if(coord.getX() <= candidate.getX() && coord.getY() <= candidate.getY()) {
                candidate = coord;
            }
        }

        return candidate;
    }

    /**
     * Set matched tokens invalid.
     *
     * @param coordinates Set of matched coordinates in the S array
     */
    void setMatchesInvalid(Set<Coordinate> coordinates) {
        checkNotNull(coordinates);

        if(coordinates.isEmpty()) {
            return;
        }

        // Iterate through all match coordinates and set them invalid
        for(Coordinate coordinate : coordinates) {
            int x = coordinate.getX() - 1;
            int y = coordinate.getY() - 1;

            xList.get(x).setValid(false);
            yList.get(y).setValid(false);
        }
    }

    /**
     * Retrieve a set of the coordinates that make up a match.
     *
     * @param matchCoord Coordinate of the end of the match. Must be within the S array.
     * @return Set of all coordinates that form the match
     */
    Set<Coordinate> getMatchCoordinates(Coordinate matchCoord) {
        checkNotNull(matchCoord);
        checkArgument(wholeArray.contains(matchCoord), "Requested match coordinate is out of bounds: "
                + matchCoord + " not within " + wholeArray);
        checkArgument(s[matchCoord.getX()][matchCoord.getY()] != 0, "Requested match coordinate "
                + matchCoord + " points to 0 in S array!");

        Set<Coordinate> matchCoordinates = new HashSet<>();

        int x = matchCoord.getX();
        int y = matchCoord.getY();

        int largestPredecessor;
        do {
            // Only add the current coordinate if the tokens at the given point match
            if(new ValidityEnsuringToken(xList.get(x - 1)).equals(yList.get(y - 1))) {
                matchCoordinates.add(Coordinate.of(x, y));

                // If they match, the predecessor is always the upper-left diagonal
                x = x - 1;
                y = y - 1;

                largestPredecessor = s[x][y];

                continue;
            }

            // Get predecessors
            int a = s[x - 1][y - 1];
            int b = s[x - 1][y];
            int c = s[x][y - 1];

            largestPredecessor = getMaxOfInts(a, b, c);

            // Figure out which predecessor is the largest, and move to its coordinates
            if(a == largestPredecessor) {
                x = x - 1;
                y = y - 1;
            } else if(b == largestPredecessor) {
                x = x - 1;
            } else if(c == largestPredecessor) {
                y = y - 1;
            } else {
                throw new RuntimeException("Unreachable code!");
            }
        } while(largestPredecessor > 0);

        return matchCoordinates;
    }

    /**
     * Get the coordinate with the largest value in the S matrix from a given set to check.
     *
     * @param toTest Set of coordinates to check within
     * @return Coordinate from toTest which maps to the largest value in the S matrix. Ties broken arbitrarily.
     */
    Coordinate getMaxOfCoordinates(Set<Coordinate> toTest) {
        checkNotNull(toTest);
        checkArgument(!toTest.isEmpty(), "Cannot get the maximum of an empty set of coordinates!");

        Coordinate candidate = Iterables.get(toTest, 0);
        int value = s[candidate.getX()][candidate.getY()];

        for(Coordinate newCandidate : toTest) {
            int newValue = s[newCandidate.getX()][newCandidate.getY()];

            if(newValue > value) {
                candidate = newCandidate;
                value = newValue;
            }
        }

        return candidate;
    }

    /**
     * Merge given map into the Candidates list.
     *
     * @param merge Map to merge into candidates
     */
    void mergeIntoCandidates(Map<Integer, Set<Coordinate>> merge) {
        checkNotNull(merge);

        for(Integer key : merge.keySet()) {
            Set<Coordinate> contentsToMerge = merge.get(key);

            if(!candidates.containsKey(key)) {
                candidates.put(key, contentsToMerge);
            } else {
                Set<Coordinate> contentsMergeInto = candidates.get(key);

                contentsMergeInto.addAll(contentsToMerge);
            }
        }
    }

    /**
     * Get the maximum of 3 integers.
     *
     * @param a First int
     * @param b Second int
     * @param c Third int
     * @return Largest of a, b, and c
     */
    static int getMaxOfInts(int a, int b, int c) {
        if(a < b) {
            if(b < c) {
                return c;
            } else {
                return b;
            }
        } else {
            if(b < c) {
                if(a < c) {
                    return c;
                } else {
                    return a;
                }
            } else {
                return a;
            }
        }
    }
}
