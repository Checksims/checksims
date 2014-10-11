package edu.wpi.checksims.algorithm.smithwaterman;

import edu.wpi.checksims.util.Token;
import edu.wpi.checksims.util.TokenListCloner;
import edu.wpi.checksims.util.TwoDimArrayCoord;
import edu.wpi.checksims.util.TwoDimIntArray;

import static edu.wpi.checksims.util.Direction.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Results from one iteration of the Smith-Waterman algorithm
 */
public class SmithWatermanResults<T extends Comparable> {
    private TwoDimIntArray watermanTable;
    private List<Token<T>> a;
    private List<Token<T>> b;

    public SmithWatermanResults(TwoDimIntArray table, List<Token<T>> a, List<Token<T>> b) {
        this.watermanTable = table;
        this.a = a;
        this.b = b;
    }

    // Get the maximum overlay (longest set of matching characters found by the algorithm)
    public int getMaxOverlay() {
        return watermanTable.getMax();
    }

    public boolean hasMatch() {
        return watermanTable.getMax() != 0;
    }

    public List<T> getMatch() {
        List<TwoDimArrayCoord> matches = getOverlayCoords();
        List<T> results = new LinkedList<>();

        for(TwoDimArrayCoord t : matches) {
            results.add(0, a.get(t.x - 1).getToken());
        }

        return results;
    }

    public int getMatchLength() {
        return getMatch().size();
    }

    public int[][] getSmithWatermanTable() {
        return watermanTable.getArray();
    }

    public String getSmithWatermanTableString() {
        return watermanTable.arrayToString();
    }

    public List<Token<T>> setMatchInvalidA() {
        List<TwoDimArrayCoord> matches = getOverlayCoords();
        List<Token<T>> newList = TokenListCloner.cloneList(a);

        for(TwoDimArrayCoord t : matches) {
            newList.get(t.x - 1).setInvalid();
        }

        return newList;
    }

    public List<Token<T>> setMatchInvalidB() {
        List<TwoDimArrayCoord> matches = getOverlayCoords();
        List<Token<T>> newList = TokenListCloner.cloneList(b);

        for(TwoDimArrayCoord t : matches) {
            newList.get(t.y - 1).setInvalid();
        }

        return newList;
    }

    // Get all the coordinates where the two token lists overlay
    private List<TwoDimArrayCoord> getOverlayCoords() {
        List<TwoDimArrayCoord> matches = new LinkedList<>();

        if(!hasMatch()) {
            return matches;
        }

        TwoDimArrayCoord curCoord = watermanTable.getMaxPos();
        TwoDimArrayCoord[] predecessors;
        int max;

        do {
            if(a.get(curCoord.x - 1).equals(b.get(curCoord.y - 1))) {
                matches.add(curCoord);
            }

            predecessors = new TwoDimArrayCoord[] { curCoord.getAdjacent(UP), curCoord.getAdjacent(UPLEFT), curCoord.getAdjacent(LEFT) };
            max = watermanTable.getMaxFrom(predecessors);

            if(max == watermanTable.getValue(curCoord.getAdjacent(UP))) {
                curCoord = curCoord.getAdjacent(UP);
            } else if(max == watermanTable.getValue(curCoord.getAdjacent(LEFT))) {
                curCoord = curCoord.getAdjacent(LEFT);
            } else if(max == watermanTable.getValue(curCoord.getAdjacent(UPLEFT))) {
                curCoord = curCoord.getAdjacent(UPLEFT);
            } else {
                throw new RuntimeException("UNREACHABLE CODE");
            }
        } while(watermanTable.getValue(curCoord) != 0);

        return matches;
    }
}
