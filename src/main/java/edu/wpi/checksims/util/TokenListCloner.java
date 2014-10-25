package edu.wpi.checksims.util;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Clones a list of tokens, producing a new, distinct list
 */
public class TokenListCloner {
    private TokenListCloner() {}

    public static <T extends Comparable<T>> List<Token<T>> cloneList(List<Token<T>> tokens) {
        Supplier<List<Token<T>>> listGenerator = LinkedList::new;

        return tokens.stream().map((token) -> new Token<>(token.getToken(), token.isValid())).collect(Collectors.toCollection(listGenerator));
    }
}
