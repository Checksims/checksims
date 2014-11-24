package edu.wpi.checksims.algorithm.commonsubstring;

import edu.wpi.checksims.ChecksimException;
import edu.wpi.checksims.algorithm.AlgorithmResults;
import edu.wpi.checksims.algorithm.PlagiarismDetector;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.token.Token;
import edu.wpi.checksims.token.TokenList;
import edu.wpi.checksims.token.TokenType;
import edu.wpi.checksims.token.ValidityIgnoringToken;
import edu.wpi.checksims.token.tree.SubmissionID;
import edu.wpi.checksims.token.tree.SuffixTreeNode;
import edu.wpi.checksims.token.tree.SuffixTreeRoot;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Suffix Tree-based Longest Common Substring
 */
public class LongestCommonSubstring implements PlagiarismDetector {
    public final int Threshold = 5;

    private static LongestCommonSubstring instance;

    private LongestCommonSubstring() {
    }

    public static LongestCommonSubstring getInstance() {
        if (instance == null) {
            instance = new LongestCommonSubstring();
        }

        return instance;
    }

    @Override
    public String getName() {
        return "lcs";
    }

    @Override
    public TokenType getDefaultTokenType() {
        return TokenType.CHARACTER;
    }

    @Override
    public AlgorithmResults detectPlagiarism(Submission a, Submission b) throws ChecksimException {
        if (!a.getTokenType().equals(b.getTokenType())) {
            throw new ChecksimException("Token type mismatch between submissions " + a.getName() + " and " + b.getName());
        }

        // No point building the tree is one of the submissions was empty
        if(a.getTokenList().isEmpty() || b.getTokenList().isEmpty()) {
            return new AlgorithmResults(a, b, 0, 0, a.getTokenList(), b.getTokenList());
        }

        Triple<Submission, Submission, Integer> onePassResult = onePassLCS(a, b);

        // if we got no matches, no point continuing
        // Return an empty AlgorithmResults
        if (onePassResult.getRight() == 0) {
            return new AlgorithmResults(a, b, 0, 0, a.getTokenList(), b.getTokenList());
        }

        Triple<Submission, Submission, Integer> current = onePassResult;
        int totalMatched = onePassResult.getRight();

        while (current.getRight() >= Threshold) {
            current = onePassLCS(current.getLeft(), current.getMiddle());
            totalMatched += current.getRight();
        }

        return new AlgorithmResults(a, b, totalMatched, totalMatched, current.getLeft().getTokenList(), current.getMiddle().getTokenList());
    }

    static Triple<Submission, Submission, Integer> onePassLCS(Submission a, Submission b) throws ChecksimException {
        if (!a.getTokenType().equals(b.getTokenType())) {
            throw new ChecksimException("Token type mismatch between submissions " + a.getName() + " and " + b.getName());
        }

        SuffixTreeRoot tree = SuffixTreeRoot.suffixTreeFromSubmissions(a, b);

        // Get an LCS match
        List<SuffixTreeNode> match = getLCS(tree);

        return setLCSInvalidForSubmissions(a, b, match);
    }

    static Triple<Submission, Submission, Integer> setLCSInvalidForSubmissions(Submission a, Submission b, List<SuffixTreeNode> lcsMatch) throws ChecksimException {
        if(lcsMatch.isEmpty()) {
            return Triple.of(a, b, 0);
        }

        TokenList toModifyA = TokenList.cloneTokenList(a.getTokenList());
        TokenList toModifyB = TokenList.cloneTokenList(b.getTokenList());

        // Arbitrarily choose valid start indices
        int submissionIndexA = lcsMatch.get(0).getOccurrencesA().get(0);
        int submissionIndexB = lcsMatch.get(0).getOccurrencesB().get(0);

        for(SuffixTreeNode matchNode : lcsMatch) {
            Token submissionTokenA = toModifyA.get(submissionIndexA);
            Token submissionTokenB = toModifyB.get(submissionIndexB);
            Token matchToken = ValidityIgnoringToken.validityIgnoringToken(matchNode.getContent());

            if (!matchToken.equals(submissionTokenA)) {
                throw new ChecksimException("Token mismatch at index " + submissionIndexA + " of submission " +
                        a.getName() + " : \"" + submissionTokenA.getTokenAsString() + "\", \"" +
                        matchToken.getTokenAsString() + "\"");
            } else if (!matchToken.equals(submissionTokenB)) {
                throw new ChecksimException("Token mismatch at index " + submissionIndexB + " of submission " +
                        b.getName() + " : \"" + submissionTokenB.getTokenAsString() + "\", \"" +
                        matchToken.getTokenAsString() + "\"");
            }

            submissionTokenA.setValid(false);
            submissionTokenB.setValid(false);

            submissionIndexA++;
            submissionIndexB++;
        }

        return Triple.of(new Submission(a.getName(), toModifyA), new Submission(b.getName(), toModifyB), lcsMatch.size());
    }

    static List<SuffixTreeNode> getLCS(SuffixTreeRoot root) throws ChecksimException {
        List<SuffixTreeNode> children = root.getChildrenAsList();
        List<SuffixTreeNode> commonSequence = new LinkedList<>();

        List<SuffixTreeNode> sharedAndValid = children.stream().filter((node) -> node.isValid() && node.isShared()).collect(Collectors.toList());

        if (sharedAndValid.isEmpty()) {
            return commonSequence; // No match, return empty list
        }

        // Get maximum length of all matches
        int maxMatchLen = Collections.max(sharedAndValid.stream().map(SuffixTreeNode::getMaxSharedDepth).collect(Collectors.toList()));

        // Filter down to the max-length matches
        List<SuffixTreeNode> maxLengthMatches = sharedAndValid.stream().filter((node) -> node.getMaxSharedDepth() == maxMatchLen).collect(Collectors.toList());

        // This should never happen
        if (maxLengthMatches.isEmpty()) {
            throw new ChecksimException("No matches of maximum detected match length could be found in suffix tree!");
        }

        // Arbitrarily break ties
        SuffixTreeNode chosenMatch = maxLengthMatches.get(0);

        // Add our root the the common sequence
        commonSequence.add(chosenMatch);

        int curMatchLen = maxMatchLen - 1;

        SuffixTreeNode current = chosenMatch;
        int counter = 1;
        List<Integer> validOccurrencesA = new LinkedList<>(current.getOccurrencesA());
        List<Integer> validOccurrencesB = new LinkedList<>(current.getOccurrencesB());

        // Let's move down the tree
        while (curMatchLen > 0) {
            current = getSharedAndValidChildWithGivenMatchLength(current, curMatchLen);

            commonSequence.add(current);

            // Variables used in lambdas must be "effectively final"
            // So, uh, yeah. Redeclare them once per loop iteration.
            int curCounter = counter;
            SuffixTreeNode finalCurrent = current;

            // Filter valid occurrences
            validOccurrencesA = validOccurrencesA.stream().filter((num) -> finalCurrent.containsOccurrenceA((num + curCounter))).collect(Collectors.toList());
            validOccurrencesB = validOccurrencesB.stream().filter((num) -> finalCurrent.containsOccurrenceB((num + curCounter))).collect(Collectors.toList());

            curMatchLen--;
            counter++;
        }

        if (validOccurrencesA.isEmpty()) {
            throw new ChecksimException("Filtered all valid occurrences for submission A!");
        } else if (validOccurrencesB.isEmpty()) {
            throw new ChecksimException("Filtered all valid occurrences for submission B!");
        }

        // Remove the first element of the common sequence, reconstruct with filtered valid occurrence, and return
        SuffixTreeNode rebuiltNode = new SuffixTreeNode(commonSequence.get(0).getContent());

        // Add newly-filtered valid occurrences to rebuilt node
        validOccurrencesA.forEach(rebuiltNode::addOccurrenceA);
        validOccurrencesB.forEach(rebuiltNode::addOccurrenceB);

        // Replace first node with rebuild node
        commonSequence.remove(0);
        commonSequence.add(0, rebuiltNode);

        return commonSequence;
    }

    static SuffixTreeNode getSharedAndValidChildWithGivenMatchLength(SuffixTreeNode childOf, int givenLength) throws ChecksimException {
        if(givenLength == 0) {
            throw new ChecksimException("Match length of 0 is invalid in this context!");
        }

        List<SuffixTreeNode> sharedAndValidChildren = childOf.getChildrenAsList().stream().filter((node) -> node.isValid() && node.isShared()).collect(Collectors.toList());

        if(sharedAndValidChildren.isEmpty()) {
            throw new ChecksimException("Could not find any children of node containing \"" +
                    childOf.getContent().getTokenAsString() + "\" which are shared and valid (has " + childOf.getChildrenAsList().size() + " children)");
        }

        List<SuffixTreeNode> nodesWithGivenLength = sharedAndValidChildren.stream().filter((node) -> node.getMaxSharedDepth() == givenLength).collect(Collectors.toList());

        if(nodesWithGivenLength.isEmpty()) {
            throw new ChecksimException("Could not find any children of node containing \"" +
                    childOf.getContent().getTokenAsString() + "\" with match length of " + givenLength);
        }

        // Arbitrarily break ties
        return nodesWithGivenLength.get(0);
    }
}
