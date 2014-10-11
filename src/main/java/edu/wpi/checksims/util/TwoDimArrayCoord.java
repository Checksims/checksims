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
