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
 * Copyright (c) 2014-2015 Matthew Heon and Dolan Murvihill
 */

package edu.wpi.checksims.token;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Interface for comparable tokens of various types.
 */
public final class ConcreteToken implements Token {
    private boolean isValid;
    private final int lexeme;
    private final TokenType type;

    /**
     * Construct a valid token with given type.
     *
     * @param token Object the token represents
     * @param type Type of token
     */
    public ConcreteToken(Object token, TokenType type) {
        this(token, type, true);
    }

    /**
     * Construct a token with given type.
     *
     * @param token Object the token represents
     * @param type Type of token
     * @param isValid Whether the token is valid
     */
    public ConcreteToken(Object token, TokenType type, boolean isValid) {
        checkNotNull(token);
        checkNotNull(type);

        this.isValid = isValid;
        this.type = type;
        this.lexeme = LexemeMap.getLexemeForToken(token);
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
        return LexemeMap.getTokenForLexeme(lexeme);
    }

    @Override
    public String getTokenAsString() {
        return getToken().toString();
    }

    /**
     * @return Whether this token is valid
     */
    @Override
    public boolean isValid() {
        return isValid;
    }

    /**
     * @param isValid New value for validity of this token
     */
    @Override
    public void setValid(boolean isValid) {
        this.isValid = isValid;
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

        return otherToken.getType().equals(this.type)
                && otherToken.getLexeme() == this.lexeme
                && otherToken.isValid() == this.isValid;
    }

    @Override
    public String toString() {
        return "A " + type + " token containing \"" + getTokenAsString() + "\", represented by lexeme " + lexeme;
    }

    @Override
    public int hashCode() {
        return lexeme;
    }

    /**
     * Perform a deep-copy of a token, returning a new, identical instance.
     *
     * @param token Token to copy
     * @return New, identical copy of that token
     */
    public static Token cloneToken(Token token) {
        checkNotNull(token);

        return new ConcreteToken(token.getLexeme(), token.getType(), token.isValid());
    }
}
