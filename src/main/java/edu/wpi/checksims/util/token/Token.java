package edu.wpi.checksims.util.token;

/**
 * Interface for comparable tokens of various types
 */
public abstract class Token {
    private boolean isValid;

    public Token(boolean isValid) {
        this.isValid = isValid;
    }

    public abstract TokenType getType();

    public abstract Object getToken();

    public abstract String getTokenAsString();

    /**
     * @return Whether this token is valid
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * @param isValid New value for validity of this token
     */
    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    public abstract Token lowerCase();

    /**
     * FAILS IF EITHER TOKEN IS INVALID!
     *
     * @param other Object to compare to
     * @return True if object compared to is a Token with same type and equiv. token value
     */
    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Token)) {
            return false;
        }

        Token otherToken = (Token)other;

        return (otherToken.getType().equals(this.getType())) && (otherToken.getToken().equals(this.getToken())) && (otherToken.isValid()) && (this.isValid());
    }
}
