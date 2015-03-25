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
 * Copyright (c) 2014 Matthew Heon and Dolan Murvihill
 */

package edu.wpi.checksims.token;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Interface for comparable tokens of various types
 */
public final class ConcreteToken implements Token {
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
