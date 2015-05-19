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

/**
 * Interface for Tokens.
 *
 * A Token is the basic unit of comparison in Checksims. A token represents a "chunk" of a submission --- typically a
 * substring of the submission, or a single character.
 *
 * Tokens are backed by "Lexemes" --- for details, see LexemeMap
 *
 * This interface enables easy use of Decorators for tokens.
 */
public interface Token {
    /**
     * @return Lexeme used to represent this token
     */
    int getLexeme();

    /**
     * @return Type of this token
     */
    TokenType getType();

    /**
     * @return Object representing the token itself
     */
    Object getToken();

    /**
     * @return String representation of the token
     */
    String getTokenAsString();

    /**
     * @return Whether this token is valid
     */
    boolean isValid();

    /**
     * @param isValid New value for token validity
     */
    void setValid(boolean isValid);

    /**
     * @param token Token to clone
     * @return Clone of token
     */
    static Token cloneToken(Token token) {
        return ConcreteToken.cloneToken(token);
    }
}
