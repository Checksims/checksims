package edu.wpi.checksims.token;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Interface for comparable tokens of various types
 */
public class ConcreteToken implements Token {
    private static final BiMap<Object, Integer> lexemeMap = Maps.synchronizedBiMap(HashBiMap.create());
    private static final AtomicInteger lexemeIndex = new AtomicInteger();

    private boolean isValid;
    private final int lexeme;
    private final TokenType type;

    public ConcreteToken(Object token, TokenType type) {
        this(token, type, true);
    }

    public ConcreteToken(Object token, TokenType type, boolean isValid) {
        this.isValid = isValid;
        this.type = type;

        if(lexemeMap.containsKey(token)) {
            this.lexeme = lexemeMap.get(token);
        } else {
            this.lexeme = lexemeIndex.getAndIncrement();
            lexemeMap.put(token, lexeme);
        }
    }

    private ConcreteToken(int lexeme, TokenType type, boolean isValid) {
        this.isValid = isValid;
        this.type = type;
        this.lexeme = lexeme;
    }

    @Override
    public int getLexeme() {
        return lexeme;
    }

    @Override
    public TokenType getType() {
        return type;
    }

    @Override
    public Object getToken() {
        if(!lexemeMap.inverse().containsKey(lexeme)) {
            throw new RuntimeException("No mapping for lexeme " + lexeme);
        }

        return lexemeMap.inverse().get(lexeme);
    }

    @Override
    public String getTokenAsString() {
        return getToken().toString();
    }

    /**
     * @return Whether this tokenization is valid
     */
    @Override
    public boolean isValid() {
        return isValid;
    }

    /**
     * @param isValid New value for validity of this tokenization
     */
    @Override
    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    @Override
    public Token lowerCase() {
        Object lowerToken;

        switch(type) {
            case CHARACTER:
                lowerToken = Character.toLowerCase((Character)getToken());
                break;
            case WHITESPACE:
                // Fall through to line
            case LINE:
                lowerToken = ((String)getToken()).toLowerCase();
                break;
            default:
                throw new RuntimeException("Unsupported token type!");
        }

        return new ConcreteToken(lowerToken, type, isValid);
    }

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

        return otherToken.getType().equals(this.type) && otherToken.getLexeme() == this.lexeme && otherToken.isValid() == this.isValid;
    }

    @Override
    public String toString() {
        return "A " + type + " token containing \"" + getTokenAsString() + "\", represented by lexeme " + lexeme;
    }

    @Override
    public int hashCode() {
        return lexeme;
    }

    public static Token cloneToken(Token token) {
        return new ConcreteToken(token.getLexeme(), token.getType(), token.isValid());
    }
}
