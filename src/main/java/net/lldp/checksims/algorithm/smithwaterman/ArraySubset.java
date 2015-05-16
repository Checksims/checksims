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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An immutable subset of a 2-Dimensional Array.
 */
public final class ArraySubset {
    private final Coordinate origin;
    private final Coordinate max;

    /**
     * Construct a new array subset.
     *
     * @param origin Origin point. Must be closer to the origin (smaller in both X and Y!) than max
     * @param max Point of maximum extent
     */
    public ArraySubset(Coordinate origin, Coordinate max) {
        checkNotNull(origin);
        checkNotNull(max);
        checkArgument(origin.getX() < max.getX(), "Error creating array subset - maximum X of " + max.getX() +
                " not greater than origin X of " + origin.getX());
        checkArgument(origin.getY() < max.getY(), "Error creating array subset - maximum Y of " + max.getY() +
                " not greater than origin Y of " + origin.getY());

        this.origin = origin;
        this.max = max;
    }

    /**
     * @param x1 X coordinate of origin
     * @param y1 Y coordinate of origin
     * @param x2 X coordinate of max
     * @param y2 Y coordinate of max
     * @return Array Subset built from given coordinates
     */
    public static ArraySubset of(int x1, int y1, int x2, int y2) {
        return new ArraySubset(Coordinate.of(x1, y1), Coordinate.of(x2, y2));
    }

    /**
     * @return Coordinate representing the lower bound of this subset
     */
    public Coordinate getOrigin() {
        return origin;
    }

    /**
     * @return Coordinate representing the upper bound of this subset
     */
    public Coordinate getMax() {
        return max;
    }

    /**
     * @param toCheck Point to check
     * @return True if given point is within this array subset
     */
    public boolean contains(Coordinate toCheck) {
        checkNotNull(toCheck);

        return (origin.getX() <= toCheck.getX() && toCheck.getX() <= max.getX()) && (origin.getY() <= toCheck.getY()
                && toCheck.getY() <= max.getY());
    }

    @Override
    public String toString() {
        return "An array subset starting at " + origin.toString() + " and ending at " + max.toString();
    }

    @Override
    public int hashCode() {
        return origin.hashCode() ^ max.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof ArraySubset)) {
            return false;
        }

        ArraySubset otherSubset = (ArraySubset)other;

        return otherSubset.getOrigin().equals(origin) && otherSubset.getMax().equals(max);
    }
}
