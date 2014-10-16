package edu.wpi.checksims.util;

import java.util.LinkedList;
import java.util.List;

/**
 * Clones a list of tokens, producing a new, distinct list
 */
public class TokenListCloner {
    private TokenListCloner() {}

    public static <T extends Comparable<T>> List<Token<T>> cloneList(List<Token<T>> tokens) {
        List<Token<T>> newTokens = new LinkedList<>();

        tokens.stream().forEachOrdered((token) -> newTokens.add(new Token<>(token.getToken(), token.isValid())));

        return newTokens;
    }
}
