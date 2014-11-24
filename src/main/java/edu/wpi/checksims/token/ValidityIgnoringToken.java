package edu.wpi.checksims.token;

/**
 * Token which ignores validity when comparing
 *
 * Decorates other tokens to override their equals() method
 */
public final class ValidityIgnoringToken extends AbstractTokenDecorator {
    public ValidityIgnoringToken(Token wrappedToken) {
        super(wrappedToken);
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Token)) {
            return false;
        }

        Token otherToken = (Token)other;

        return otherToken.getType().equals(this.getType()) && otherToken.getLexeme() == this.getLexeme();
    }
}
