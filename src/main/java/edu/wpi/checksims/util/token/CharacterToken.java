package edu.wpi.checksims.util.token;

/**
 * Token containing a single char
 */
public class CharacterToken extends Token {
    private final char token;

    public CharacterToken(char token) {
        super(true);
        this.token = token;
    }

    public CharacterToken(char token, boolean valid) {
        super(valid);
        this.token = token;
    }

    @Override
    public TokenType getType() {
        return TokenType.CHARACTER;
    }

    @Override
    public Object getToken() {
        return token;
    }

    @Override
    public String getTokenAsString() {
        return "" + token;
    }

    @Override
    public String toString() {
        return "A character token containing " + token;
    }
}
