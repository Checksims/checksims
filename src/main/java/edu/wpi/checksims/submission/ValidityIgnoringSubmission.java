package edu.wpi.checksims.submission;

import edu.wpi.checksims.util.token.TokenList;
import edu.wpi.checksims.util.token.TokenType;
import edu.wpi.checksims.util.token.ValidityEnsuringToken;
import edu.wpi.checksims.util.token.ValidityIgnoringToken;

import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Submission which ignores validity - tokens are compared ignoring their validity
 *
 * Decorates another submission and overrides equals()
 */
public class ValidityIgnoringSubmission extends Submission {
    private final Submission wrappedSubmission;

    private ValidityIgnoringSubmission(Submission wrappedSubmission) {
        super("", new TokenList(TokenType.LINE));
        this.wrappedSubmission = wrappedSubmission;
    }

    public TokenList getTokenList() {
        return wrappedSubmission.getTokenList();
    }

    public String getName() {
        return wrappedSubmission.getName();
    }

    public int getNumTokens() {
        return wrappedSubmission.getNumTokens();
    }

    public TokenType getTokenType() {
        return wrappedSubmission.getTokenType();
    }

    @Override
    public String toString() {
        return wrappedSubmission.toString();
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Submission)) {
            return false;
        }

        Submission otherSubmission = (Submission)other;

        boolean initialChecks = otherSubmission.getTokenType().equals(getTokenType()) &&
                otherSubmission.getName().equals(getName()) &&
                (otherSubmission.getTokenList().size() == getTokenList().size());

        if(!initialChecks) {
            return false;
        }

        Supplier<TokenList> tokenListSupplier = () -> new TokenList(this.getTokenType());

        TokenList thisIgnoringValidity = wrappedSubmission.getTokenList().stream().map(ValidityIgnoringToken::validityIgnoringToken).collect(Collectors.toCollection(tokenListSupplier));

        return thisIgnoringValidity.equals(otherSubmission.getTokenList());
    }

    public static ValidityIgnoringSubmission validityIgnoringSubmission(Submission toWrap) {
        return new ValidityIgnoringSubmission(toWrap);
    }
}
