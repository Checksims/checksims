package edu.wpi.checksims.token;

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
    public Token lowerCase() {
        return new LineToken(token.toLowerCase(), this.isValid());
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
        return "A newline-delineated tokenization containing " + token;
    }

    @Override
    public int hashCode() {
        return token.hashCode();
    }
}
