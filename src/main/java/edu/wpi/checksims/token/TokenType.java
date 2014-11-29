package edu.wpi.checksims.token;

import edu.wpi.checksims.ChecksimException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Supported tokenization types
 */
public enum TokenType {
    CHARACTER("character"),
    WHITESPACE("whitespace"),
    LINE("line");

    private String name;

    private TokenType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Token of type " + name;
    }

    public static TokenType fromString(String input) throws ChecksimException {
        String lowerInput = input.toLowerCase();

        TokenType[] types = TokenType.class.getEnumConstants();

        // Filter to find anything with a matching name
        List<TokenType> matching = Arrays.stream(types).filter((type) -> type.name.equals(lowerInput)).collect(Collectors.toList());

        // If we find nothing, throw an exception
        if(matching.size() == 0) {
            throw new ChecksimException("No tokenization with name " + input + " found!");
        }

        // If we get two or more, there's a serious problem
        if(matching.size() > 1) {
            throw new ChecksimException("FATAL: Encountered multiple tokenizations with identical names!");
        }

        // Otherwise, just return the sole thing we filtered to
        return matching.get(0);
    }
}
