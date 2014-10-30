package edu.wpi.checksims.util.token;

/**
 * Token containing a string with no whitespace
 */
public class WhitespaceToken extends Token {
    private final String token;

    public WhitespaceToken(String token) {
        // TODO should validate token to ensure no whitespace
        super(true);
        this.token = token;
    }

    public WhitespaceToken(String token, boolean valid) {
        super(valid);
        this.token = token;
    }

    @Override
    public TokenType getType() {
        return TokenType.WHITESPACE;
    }

    @Override
    public Object getToken() {
        return token;
    }

    @Override
    public String getTokenAsString() {
        return token;
    }

    @Override
    public String toString() {
        return "A whitespace-delineated token containing " + token;
    }
}
