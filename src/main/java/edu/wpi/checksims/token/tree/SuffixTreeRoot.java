package edu.wpi.checksims.token.tree;

import edu.wpi.checksims.ChecksimException;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.token.TokenType;

/**
 * Root of a suffix tree of tokens
 */
public class SuffixTreeRoot extends SuffixTreeShared {
    private final Submission submissionA;
    private final Submission submissionB;
    private final TokenType type;

    public SuffixTreeRoot(Submission submissionA, Submission submissionB) throws ChecksimException {
        if(!submissionA.getTokenType().equals(submissionB.getTokenType())) {
            throw new ChecksimException("Token type mismatch creating suffix tree! Given token types " + submissionA.getTokenType() + " and " + submissionB.getTokenType());
        }

        this.type = submissionA.getTokenType();
        this.submissionA = submissionA;
        this.submissionB = submissionB;
    }

    public TokenType getTokenType() {
        return type;
    }

    public Submission getSubmissionA() {
        return submissionA;
    }

    public Submission getSubmissionB() {
        return submissionB;
    }

    public String toString() {
        return "Root of a suffix tree of tokens containing submissions " + submissionA.getName() + " and " + submissionB.getName();
    }

    public static SuffixTreeRoot suffixTreeFromSubmissions(Submission a, Submission b) throws ChecksimException {
        if(!a.getTokenType().equals(b.getTokenType())) {
            throw new ChecksimException("Token type mismatch when attempting to create suffix tree from submissions " +
                    a.getName() + " and " + b.getName() + ": " + a.getTokenType() + " and " + b.getTokenType());
        }

        SuffixTreeRoot root = new SuffixTreeRoot(a, b);

        if(a.getTokenList().isEmpty() && b.getTokenList().isEmpty()) {
            // Return empty suffix tree
            return root;
        }

        // Add nodes from A
        if(!a.getTokenList().isEmpty()) {
            SuffixTreeNode unaryTree = SuffixTreeNode.listToUnarySuffixTree(a.getTokenList(), SubmissionID.A);

            root.addChildTree(unaryTree);

            while(unaryTree.hasChildren()) {
                unaryTree = unaryTree.getChildrenAsList().get(0);
                root.addChildTree(SuffixTreeNode.cloneNode(unaryTree));
            }
        }

        // Add nodes from B
        if(!b.getTokenList().isEmpty()) {
            SuffixTreeNode unaryTree = SuffixTreeNode.listToUnarySuffixTree(b.getTokenList(), SubmissionID.B);

            root.addChildTree(unaryTree);

            while(unaryTree.hasChildren()) {
                unaryTree = unaryTree.getChildrenAsList().get(0);
                root.addChildTree(SuffixTreeNode.cloneNode(unaryTree));
            }
        }

        return root;
    }
}
