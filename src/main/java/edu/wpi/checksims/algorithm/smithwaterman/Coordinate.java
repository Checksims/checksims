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

package edu.wpi.checksims.algorithm.smithwaterman;

import org.apache.commons.lang3.tuple.Pair;

/**
 * An immutable 2-D coordinate
 */
public final class Coordinate extends Pair<Integer, Integer> {
    private final int x;
    private final int y;

    private static final long serialVersionUID = 1L;

    /**
     * Construct a 2D coordinate
     *
     * @param x Desired X coordinate
     * @param y Desired Y coordinate
     */
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @param x X coordinate
     * @param y Y coordinate
     * @return 2D coordinate from given X and Y
     */
    public static Coordinate of(int x, int y) {
        return new Coordinate(x, y);
    }

    /**
     * @return X coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * @return Y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * @return X coordinate
     */
    @Override
    public Integer getLeft() {
        return x;
    }

    /**
     * @return Y coordinate
     */
    @Override
    public Integer getRight() {
        return y;
    }

    /**
     * Unsupported as Coordinate is immutable
     *
     * @param value Unused
     * @return Unused
     */
    @Override
    public Integer setValue(Integer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Coordinate)) {
            return false;
        }

        Coordinate otherCoord = (Coordinate)other;

        return (otherCoord.getX() == x) && (otherCoord.getY() == y);
    }

    @Override
    public int hashCode() {
        return (5 * x) ^ (3 * y);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
