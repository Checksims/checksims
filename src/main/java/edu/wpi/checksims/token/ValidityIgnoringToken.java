package edu.wpi.checksims.token;

/**
 * Token which ignores validity when comparing
 *
 * Decorates other tokens to override their equals() method
 */
public class ValidityIgnoringToken extends Token {
    private final Token wrappedToken;

    private ValidityIgnoringToken(Token wrappedToken) {
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

    /**
     * FALSE IF EITHER TOKEN IS INVALID!
     *
     * This means that two tokens which are completely identical, but both invalid, are not considered equal!
     *
     * @param other Object to compare to
     * @return True if object compared to is a Token with same type and equiv. tokenization value
     */
    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Token)) {
            return false;
        }

        Token otherToken = (Token)other;

        return (otherToken.getType().equals(this.getType())) && (otherToken.getToken().equals(this.getToken()));
    }

    @Override
    public int hashCode() {
        return wrappedToken.getToken().hashCode();
    }

    public static ValidityIgnoringToken validityIgnoringToken(Token toWrap) {
        return new ValidityIgnoringToken(toWrap);
    }
}
