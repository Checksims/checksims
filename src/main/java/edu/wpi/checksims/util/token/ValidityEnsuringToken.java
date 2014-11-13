package edu.wpi.checksims.util.token;

/**
 * Token which will only be equal to other tokens which are valid
 *
 * Decorates other tokens to override their equals() methods
 */
public class ValidityEnsuringToken extends Token {
    private final Token wrappedToken;

    private ValidityEnsuringToken(Token wrappedToken) {
        super(wrappedToken.isValid());
        this.wrappedToken = wrappedToken;
    }

    @Override
    public boolean isValid() {
        return wrappedToken.isValid();
    }

    @Override
    public void setValid(boolean valid) {
        wrappedToken.setValid(valid);
    }

    @Override
    public TokenType getType() {
        return wrappedToken.getType();
    }

    @Override
    public Object getToken() {
        return wrappedToken.getToken();
    }

    @Override
    public String getTokenAsString() {
        return wrappedToken.getTokenAsString();
    }

    @Override
    public Token lowerCase() {
        return wrappedToken.lowerCase();
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Token)) {
            return false;
        }

        Token otherToken = (Token)other;

        return (otherToken.getType().equals(this.getType())) && (otherToken.getToken().equals(this.getToken())) && this.isValid() && otherToken.isValid();
    }

    @Override
    public int hashCode() {
        return wrappedToken.getToken().hashCode();
    }

    public static ValidityEnsuringToken validityEnsuringToken(Token toWrap) {
        return new ValidityEnsuringToken(toWrap);
    }
}
