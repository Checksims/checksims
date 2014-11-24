package edu.wpi.checksims.algorithm.smithwaterman;

import edu.wpi.checksims.ChecksimException;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.algorithm.AlgorithmResults;
import edu.wpi.checksims.algorithm.PlagiarismDetector;
import edu.wpi.checksims.util.TwoDimArrayCoord;
import edu.wpi.checksims.util.TwoDimIntArray;
import edu.wpi.checksims.token.TokenList;
import edu.wpi.checksims.token.TokenType;
import edu.wpi.checksims.token.ValidityEnsuringToken;

/**
 * Performs the actual Smith-Waterman algorithm
 */
public class SmithWaterman implements PlagiarismDetector {
    private final SmithWatermanParameters params;

    public SmithWaterman(SmithWatermanParameters params) {
        this.params = params;
    }

    public SmithWaterman() {
        this.params = SmithWatermanParameters.getDefaultParams();
    }

    @Override
    public String getName() {
        return "smithwaterman";
    }

    @Override
    public TokenType getDefaultTokenType() {
        return TokenType.WHITESPACE;
    }

    /**
     * Detect plagiarism in a submission using the Smith-Waterman Algorithm
     *
     * @param a First submission to check
     * @param b Second submission to check
     * @return AlgorithmResults indicating number of matched tokens
     */
    @Override
    public AlgorithmResults detectPlagiarism(Submission a, Submission b) throws ChecksimException {
        if(!a.getTokenType().equals(b.getTokenType())) {
            throw new ChecksimException("Token list type mismatch: submission " + a.getName() + " has type " +
                    a.getTokenList().type.toString() + ", while submission " + b.getName() + " has type " +
                    b.getTokenList().type.toString());
        }

        return applySmithWatermanPlagiarismDetection(a, b, this.params);
    }

    /**
     * Required by the PlagiarismDetector interface
     *
     * @return Default instance of the Smith-Waterman algorithm
     */
    public static SmithWaterman getInstance() {
        return new SmithWaterman();
    }

    static AlgorithmResults applySmithWatermanPlagiarismDetection(Submission a, Submission b, SmithWatermanParameters params) {
        SmithWatermanResults firstRun = applySmithWaterman(a.getTokenList(), b.getTokenList(), params);

        if(firstRun == null || !firstRun.hasMatch()) {
            // No similarities found on first run, no need to loop
            return new AlgorithmResults(a, b, 0, 0, a.getTokenList(), b.getTokenList());
        }

        // Represents the total portions of the tokenization lists matched by the Smith-Waterman algorithm
        int totalOverlay = 0;
        SmithWatermanResults currResults = firstRun;
        TokenList newA = currResults.setMatchInvalidA();
        TokenList newB = currResults.setMatchInvalidB();

        while(currResults.getMatchLength() >= params.matchSizeThreshold) {
            totalOverlay += currResults.getMatchLength();

            newA = currResults.setMatchInvalidA();
            newB = currResults.setMatchInvalidB();

            currResults = applySmithWaterman(newA, newB, params);
        }

        // Always add the last overlay. Makes sure that, if the last result is under the threshold,
        // We report it regardless
        totalOverlay += currResults.getMatchLength();

        return new AlgorithmResults(a, b, totalOverlay, totalOverlay, newA, newB);
    }

    static SmithWatermanResults applySmithWaterman(TokenList a, TokenList b, SmithWatermanParameters params) {
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

        // Iterate through and fill arrays
        for(int i = 1; i < width; i++) {
            for(int j = 1; j < height; j++) {
                TwoDimArrayCoord curr = new TwoDimArrayCoord(i, j);
                TwoDimArrayCoord predecessor = new TwoDimArrayCoord(i - 1, j - 1);

                int newS;
                int newM;

                // Generate a prospective value for S[i,j] and M[i,j]
                // The outermost if generates S[i,j]
                // Based off this, we then generate M[i,j]
                if(ValidityEnsuringToken.validityEnsuringToken(a.get(curr.x - 1)).equals(ValidityEnsuringToken.validityEnsuringToken(b.get(curr.y - 1)))) {
                    // If the two characters match, we increment S[i-1,j-1] by 1 to get the new S[i,j]
                    int sPredecessor = s.getValue(predecessor);
                    int mPredecessor = m.getValue(predecessor);

                    newS = sPredecessor + params.h;

                    // Generate M table value from S table value
                    if(sPredecessor > mPredecessor) {
                        newM = sPredecessor;
                    } else {
                        newM = mPredecessor;
                    }
                } else {
                    // Get the max of our predecessors, and subtract D
                    // TODO D and R are distinct quantities, should respect this, even if we usually fix them as 1
                    int sPredMax = s.getMaxOfPredecessors(curr);

                    newS = sPredMax - params.d;

                    if(newS < 0) {
                        newS = 0;
                    }

                    // Generate M table value from S table value
                    if(newS == 0) {
                        newM = 0;
                    } else {
                        int mPredMax = m.getMaxOfPredecessors(curr);

                        if(sPredMax > mPredMax) {
                            newM = sPredMax;
                        } else {
                            newM = mPredMax;
                        }
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

        return new SmithWatermanResults(s, a, b);
    }
}
