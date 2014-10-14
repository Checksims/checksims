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

        for(int i = 0; i < tokens.size(); i++) {
            Token<T> newToken = new Token<>(tokens.get(i).getToken());
            if(!tokens.get(i).isValid()) {
                newToken.setInvalid();
            }

            newTokens.add(newToken);
        }

        return newTokens;
    }
}
