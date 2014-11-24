package edu.wpi.checksims.submission;

import edu.wpi.checksims.token.TokenList;
import edu.wpi.checksims.token.ValidityIgnoringToken;

import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Submission which ignores validity - tokens are compared ignoring their validity
 *
 * Decorates another submission and overrides equals()
 */
public final class ValidityIgnoringSubmission extends AbstractSubmissionDecorator {
    public ValidityIgnoringSubmission(Submission wrappedSubmission) {
        super(wrappedSubmission);
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Submission)) {
            return false;
        }

        Submission otherSubmission = (Submission)other;

        if(!otherSubmission.getTokenType().equals(this.getTokenType()) || !otherSubmission.getName().equals(this.getName()) || !(otherSubmission.getNumTokens() == this.getNumTokens())) {
            return false;
        }

        Supplier<TokenList> tokenListSupplier = () -> new TokenList(this.getTokenType());
        TokenList thisList = this.getTokenList().stream().map((token) -> new ValidityIgnoringToken(token)).collect(Collectors.toCollection(tokenListSupplier));
        TokenList otherList = otherSubmission.getTokenList().stream().map((token) -> new ValidityIgnoringToken(token)).collect(Collectors.toCollection(tokenListSupplier));

        return thisList.equals(otherList);
    }
}
