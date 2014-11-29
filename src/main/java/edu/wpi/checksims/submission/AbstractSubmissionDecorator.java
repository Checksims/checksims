package edu.wpi.checksims.submission;

import edu.wpi.checksims.token.TokenList;
import edu.wpi.checksims.token.TokenType;

/**
 * Superclass for submission decorators
 */
public abstract class AbstractSubmissionDecorator implements Submission {
    private final Submission wrappedSubmission;

    public AbstractSubmissionDecorator(Submission wrappedSubmission) {
        this.wrappedSubmission = wrappedSubmission;
    }

    @Override
    public TokenList getTokenList() {
        return wrappedSubmission.getTokenList();
    }

    @Override
    public String getName() {
        return wrappedSubmission.getName();
    }

    @Override
    public int getNumTokens() {
        return wrappedSubmission.getNumTokens();
    }

    @Override
    public TokenType getTokenType() {
        return wrappedSubmission.getTokenType();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Submission && wrappedSubmission.equals(other);
    }

    @Override
    public String toString() {
        return wrappedSubmission.toString();
    }

    @Override
    public int hashCode() {
        return wrappedSubmission.hashCode();
    }
}
