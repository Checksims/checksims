package edu.wpi.checksims.util;

import java.util.Comparator;

/**
 * Represents a single token which will be compared by one of our algorithms
 */
public class Token<T extends Comparable<T>> {
    private final T token;
    private boolean isValid;

    public Token(T token) {
        this.token = token;
        this.isValid = true;
    }

    public void setValid() {
        this.isValid = true;
    }

    public void setInvalid() {
        this.isValid = false;
    }

    public T getToken() {
        return token;
    }

    public boolean isValid() {
        return isValid;
    }

    public boolean equals(Object other) {
        if(!(other instanceof Token)) {
            return false;
        }

        Token otherToken = (Token)other;

        return (otherToken.getToken().compareTo(token) == 0 && otherToken.isValid() && this.isValid);
    }

    @Override
    public String toString() {
        String toReturn = "Token containing " + token.toString();

        if(!isValid) {
            toReturn = "(Invalid) " + toReturn;
        }

        return toReturn;
    }

    @Override
    public int hashCode() {
        return token.hashCode();
    }
}
