package edu.wpi.checksims.token;

/**
 * Token which will only be equal to other tokens which are valid
 *
 * Decorates other tokens to override their equals() methods
 */
public class ValidityEnsuringToken extends AbstractTokenDecorator {
    public ValidityEnsuringToken(Token wrappedToken) {
        super(wrappedToken);
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Token)) {
            return false;
        }

        Token otherToken = (Token)other;

        return otherToken.getType().equals(this.getType()) && otherToken.getLexeme() == this.getLexeme() && otherToken.isValid() && this.isValid();
    }
}
