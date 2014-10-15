package edu.wpi.checksims.algorithm.smithwaterman;

import edu.wpi.checksims.Submission;
import edu.wpi.checksims.algorithm.AlgorithmResults;
import edu.wpi.checksims.algorithm.PlagiarismDetector;
import edu.wpi.checksims.util.Token;
import edu.wpi.checksims.util.TwoDimArrayCoord;
import edu.wpi.checksims.util.TwoDimIntArray;

import static edu.wpi.checksims.util.Direction.*;

import java.util.List;

/**
 * Performs the actual Smith-Waterman algorithm
 */
public class SmithWaterman<T2 extends Comparable<T2>> implements PlagiarismDetector<T2> {
    private final SmithWatermanParameters params;

    public SmithWaterman(SmithWatermanParameters params) {
        this.params = params;
    }

    public SmithWaterman() {
        this.params = SmithWatermanParameters.getDefaultParams();
    }

    /**
     * Detect plagiarism in a submission using the Smith-Waterman Algorithm
     *
     * @param a First submission to check
     * @param b Second submission to check
     * @return AlgorithmResults indicating number of matched tokens
     */
    @Override
    public AlgorithmResults<T2> detectPlagiarism(Submission<T2> a, Submission<T2> b) {
        // TODO add verbose option slash better logging
        System.out.println("Running Smith-Waterman plagiarism detection on submissions " + a.getName() + " and " + b.getName());

        return applySmithWatermanPlagiarismDetection(a, b, this.params);
    }

    public static <T extends Comparable<T>> AlgorithmResults<T> applySmithWatermanPlagiarismDetection(Submission<T> a,
                                                                                                Submission<T> b,
                                                                                                SmithWatermanParameters params) {
        SmithWatermanResults<T> firstRun = applySmithWaterman(a.getTokenList(), b.getTokenList(), params);

        if(firstRun == null || !firstRun.hasMatch()) {
            // No similarities found on first run, no need to loop
            return new AlgorithmResults<>(a, b, 0, 0);
        }

        // Represents the total portions of the token lists matched by the Smith-Waterman algorithm
        int totalOverlay = 0;
        SmithWatermanResults<T> currResults = firstRun;

        while(currResults.getMatchLength() >= params.matchSizeThreshold) {
            totalOverlay += currResults.getMatchLength();

            List<Token<T>> newA = currResults.setMatchInvalidA();
            List<Token<T>> newB = currResults.setMatchInvalidB();

            currResults = applySmithWaterman(newA, newB, params);
        }

        // Always add the last overlay. Makes sure that, if the last result is under the threshold,
        // We report it regardless
        totalOverlay += currResults.getMatchLength();

        return new AlgorithmResults<>(a, b, totalOverlay, totalOverlay);
    }

    static <T extends Comparable<T>> SmithWatermanResults<T> applySmithWaterman(List<Token<T>> a, List<Token<T>> b,
                                                                          SmithWatermanParameters params) {
        if(a.isEmpty() || b.isEmpty()) {
            // If one of the lists is empty, there can be no matches
            return null;
        }

        // We add 1 to each for an extra, all-0s row/column
        int width = a.size() + 1;
        int height = b.size() + 1;

        // Create M[] and S[] arrays
        TwoDimIntArray s = new TwoDimIntArray(width, height);
        TwoDimIntArray m = new TwoDimIntArray(width, height);

        // Zero first row and column
        for(int i = 0; i < width; i++) {
            TwoDimArrayCoord t = new TwoDimArrayCoord(i, 0);
            s.setValue(0, t);
        }
        for(int j = 1; j < height; j++) {
            TwoDimArrayCoord t = new TwoDimArrayCoord(0, j);
            s.setValue(0, t);
        }

        // Iterate through and fill arrays
        for(int i = 1; i < width; i++) {
            for(int j = 1; j < height; j++) {
                TwoDimArrayCoord curr = new TwoDimArrayCoord(i, j);
                TwoDimArrayCoord predecessor = curr.getAdjacent(UPLEFT);

                int newS;
                int newM;

                // Generate a prospective value for S[i,j]
                if(a.get(curr.x - 1).equals(b.get(curr.y - 1))) {
                    // If the two characters match, we increment S[i-1,j-1] by 1 to get the new S[i,j]
                    // Predecessors[0] is always the upper-left diagonal
                    newS = s.getValue(predecessor) + params.h;
                } else {
                    // Get the max of our predecessors, and subtract D
                    // TODO D and R are distinct quantities, should respect this, even if we usually fix them as 1

                    newS = s.getMaxOfPredecessors(curr) - params.d;
                }

                // Generate a prospective value for M[i.j]
                if(newS == 0) {
                    // If the prospective S is 0, prospective M is always 0
                    newM = 0;
                } else if(a.get(curr.x - 1).equals(b.get(curr.y - 1))) {
                    // If the characters match, get the match of the upper-left diagonal in M and S
                    int sVal = s.getValue(predecessor);
                    int mVal = m.getValue(predecessor);

                    if(sVal > mVal) {
                        newM = sVal;
                    } else {
                        newM = mVal;
                    }
                } else {
                    // Get the max of our predecessors in M and S
                    int sVal = s.getMaxOfPredecessors(curr);
                    int mVal = m.getMaxOfPredecessors(curr);

                    if(sVal > mVal) {
                        newM = sVal;
                    } else {
                        newM = mVal;
                    }
                }

                // Get newM - newS and check against our threshold
                if(newM - newS >= params.overlapThreshold) {
                    // M table dominates S table, probably overlap - zero out S[i,j] and M[i,j]
                    newS = 0;
                    newM = 0;
                }

                // Set S[i,j] and M[i,j]
                s.setValue(newS, curr);
                m.setValue(newM, curr);
            }
        }

        return new SmithWatermanResults<>(s, a, b);
    }
}
