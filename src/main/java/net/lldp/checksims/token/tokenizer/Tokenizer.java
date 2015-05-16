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

package net.lldp.checksims.token.tokenizer;

import net.lldp.checksims.token.TokenList;
import net.lldp.checksims.token.TokenType;

/**
 * Interface to convert a string into a list of tokens of a certain type.
 */
public interface Tokenizer {
    /**
     * Tokenize a String.
     *
     * @param string String to tokenize
     * @return A TokenList of type returned by getType(), containing tokens generated from the string
     */
    TokenList splitString(String string);

    /**
     * @return Type of tokens produced by this tokenizer.
     */
    TokenType getType();

    /**
     * Get a Tokenizer for given token type.
     *
     * @param type Type of token
     * @return Tokenizer for given type of token
     */
    static Tokenizer getTokenizer(TokenType type) {
        switch(type) {
            case CHARACTER:
                return CharTokenizer.getInstance();
            case LINE:
                return LineTokenizer.getInstance();
            case WHITESPACE:
                return WhitespaceTokenizer.getInstance();
            default:
                // TODO handle more gracefully
                throw new RuntimeException("Unhandled tokenization requested!");
        }
    }
}
