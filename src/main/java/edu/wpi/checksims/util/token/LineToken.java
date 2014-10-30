package edu.wpi.checksims.util.token;

/**
 * Token containing a string representing a single line in the submission
 */
public class LineToken extends Token {
    private final String token;

    public LineToken(String token) {
        super(true);
        this.token = token;
    }

    public LineToken(String token, boolean valid) {
        super(valid);
        this.token = token;
    }

    @Override
    public TokenType getType() {
        return TokenType.LINE;
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
        return "A newline-delineated token containing " + token;
    }
}
