package edu.wpi.checksims.token;

/**
 * Superclass for decorators for Tokens
 */
public abstract class AbstractTokenDecorator implements Token {
    private final Token wrappedToken;

    public AbstractTokenDecorator(Token wrappedToken) {
        this.wrappedToken = wrappedToken;
    }

    @Override
    public TokenType getType() {
        return wrappedToken.getType();
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
    public Object getToken() {
        return wrappedToken.getToken();
    }

    @Override
    public String getTokenAsString() {
        return wrappedToken.getTokenAsString();
    }

    @Override
    public int getLexeme() {
        return wrappedToken.getLexeme();
    }

    @Override
    public Token lowerCase() {
        return wrappedToken.lowerCase();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Token && wrappedToken.equals(other);

    }

    @Override
    public String toString() {
        return wrappedToken.toString();
    }

    @Override
    public int hashCode() {
        return wrappedToken.hashCode();
    }
}
