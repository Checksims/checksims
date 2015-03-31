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

/**
 * Represents a cell reference in a two-dimensional array
 */
public class TwoDimArrayCoord {
    public final int x;
    public final int y;

    public TwoDimArrayCoord(int x, int y) {
        if(x < 0 || y < 0) {
            throw new RuntimeException("Array coordinates must be positive!"); // TODO convert to checked
        }

        this.x = x;
        this.y = y;
    }

    // TODO error handling in here to ensure we give back a valid coordinate that does not throw an exception
    public TwoDimArrayCoord getAdjacent(Direction dir) {
        switch(dir) {
            case UP:
                return new TwoDimArrayCoord(x, y - 1);
            case DOWN:
                return new TwoDimArrayCoord(x, y + 1);
            case LEFT:
                return new TwoDimArrayCoord(x - 1, y);
            case RIGHT:
                return new TwoDimArrayCoord(x + 1, y);
            case UPLEFT:
                return new TwoDimArrayCoord(x - 1, y - 1);
            case UPRIGHT:
                return new TwoDimArrayCoord(x + 1, y - 1);
            case DOWNLEFT:
                return new TwoDimArrayCoord(x - 1, y + 1);
            case DOWNRIGHT:
                return new TwoDimArrayCoord(x + 1, y + 1);
            default:
                throw new RuntimeException("Unreachable point reached!");
        }
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    @Override
    public int hashCode() {
        return (x * 5) ^ (y * 13);
    }
}
