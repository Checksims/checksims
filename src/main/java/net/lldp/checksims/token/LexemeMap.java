/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright (c) 2014-2015 Nicholas DeMarinis, Matthew Heon, and Dolan Murvihill
 */

package net.lldp.checksims.token;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Maps lexemes (integers) to the original token contents.
 */
public final class LexemeMap {
    private LexemeMap() {}

    // TODO: does this need to be synchronized if we're synchronizing the getLexemeForToken method?
    private static final BiMap<Object, Integer> lexemeMap = Maps.synchronizedBiMap(HashBiMap.create());
    private static final AtomicInteger lexemeIndex = new AtomicInteger();

    /**
     * @param token Token to get lexeme for
     * @return Lexeme representing this token. If no such lexeme existed prior, it is created and mapped to the token.
     */
    public static synchronized int getLexemeForToken(Object token) {
        checkNotNull(token);

        if(!lexemeMap.containsKey(token)) {
            int newLexeme = lexemeIndex.getAndIncrement();

            Integer previous = lexemeMap.put(token, newLexeme);

            if(previous != null) {
                throw new RuntimeException("Overwrote lexeme mapping for token " + token.toString());
            }

            return newLexeme;
        } else {
            return lexemeMap.get(token);
        }
    }

    /**
     * Throws RuntimeException if lexeme does not map to any key.
     *
     * TODO Investigate conversion to checked exception?
     *
     * @param lexeme Lexeme being requested
     * @return Token of given type
     */
    public static Object getTokenForLexeme(int lexeme) {
        if(!lexemeMap.inverse().containsKey(lexeme)) {
            throw new RuntimeException("Lexeme " + lexeme + " does not map to any value!");
        }

        return lexemeMap.inverse().get(lexeme);
    }

    /**
     * Used to reset state for unit tests.
     *
     * CAUTION! This will obliterate the existing token mappings! DO NOT CALL IN PRODUCTION CODE!
     */
    static void resetMappings() {
        lexemeMap.clear();
        lexemeIndex.set(0);
    }
}
