package edu.wpi.checksims.token;

/**
 * Interface for Tokens, to enable decorators
 */
public interface Token {
    /**
     * @return Lexeme used to represent this token
     */
    int getLexeme();

    /**
     * @return Type of this token
     */
    TokenType getType();

    /**
     * @return Object representing the token itself
     */
    Object getToken();

    /**
     * @return String representation of the token
     */
    String getTokenAsString();

    /**
     * @return Whether this token is valid
     */
    boolean isValid();

    /**
     * @param isValid New value for token validity
     */
    void setValid(boolean isValid);

    /**
     * @return Lowercase version of this token - matching type and validity
     */
    Token lowerCase();

    /**
     * @param token Token to clone
     * @return Clone of token
     */
    public static Token cloneToken(Token token) {
        return ConcreteToken.cloneToken(token);
    }
}
