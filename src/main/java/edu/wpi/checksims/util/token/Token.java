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
     * @return Whether this tokenization is valid
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * @param isValid New value for validity of this tokenization
     */
    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    public abstract Token lowerCase();

    /**
     * @param other Object to compare to
     * @return True if object compared to is a Token with same type and equiv. tokenization value
     */
    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Token)) {
            return false;
        }

        Token otherToken = (Token)other;

        return (otherToken.getType().equals(this.getType())) && (otherToken.getToken().equals(this.getToken())) && (otherToken.isValid() == this.isValid());
    }

    /**
     * @param token Token to clone
     * @return Clone of token toClone()
     */
    public static Token cloneToken(Token token) {
        switch(token.getType()) {
            case CHARACTER:
                return new CharacterToken((char)token.getToken(), token.isValid());
            case WHITESPACE:
                return new WhitespaceToken((String)token.getToken(), token.isValid());
            case LINE:
                return new LineToken((String)token.getToken(), token.isValid());
            default:
                // TODO make this neater
                throw new RuntimeException("Unrecognized tokenization type encountered!");
        }
    }
}
