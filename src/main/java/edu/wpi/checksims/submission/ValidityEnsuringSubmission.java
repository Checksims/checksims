package edu.wpi.checksims.submission;

import edu.wpi.checksims.token.TokenList;
import edu.wpi.checksims.token.ValidityEnsuringToken;

import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Submission which enforces token validity - two tokens, even if invalid, are not considered equal.
 *
 * Decorates another submission and overrides equals()
 */
public final class ValidityEnsuringSubmission extends AbstractSubmissionDecorator {
    public ValidityEnsuringSubmission(Submission wrappedSubmission) {
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
        TokenList thisList = this.getTokenList().stream().map((token) -> new ValidityEnsuringToken(token)).collect(Collectors.toCollection(tokenListSupplier));
        TokenList otherList = otherSubmission.getTokenList().stream().map((token) -> new ValidityEnsuringToken(token)).collect(Collectors.toCollection(tokenListSupplier));

        return thisList.equals(otherList);
    }
}
