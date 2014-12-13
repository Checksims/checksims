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

/**
 * Superclass for decorators for Tokens
 */
public abstract class AbstractTokenDecorator implements Token {
    private final Token wrappedToken;

    public AbstractTokenDecorator(Token wrappedToken) {
        this.wrappedToken = wrappedToken;
    }

    @Override
    public TokenType getType() {
        return wrappedToken.getType();
    }

    @Override
    public boolean isValid() {
        return wrappedToken.isValid();
    }

    @Override
    public void setValid(boolean valid) {
        wrappedToken.setValid(valid);
    }

    @Override
    public Object getToken() {
        return wrappedToken.getToken();
    }

    @Override
    public String getTokenAsString() {
        return wrappedToken.getTokenAsString();
    }

    @Override
    public int getLexeme() {
        return wrappedToken.getLexeme();
    }

    @Override
    public Token lowerCase() {
        return wrappedToken.lowerCase();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Token && wrappedToken.equals(other);

    }

    @Override
    public String toString() {
        return wrappedToken.toString();
    }

    @Override
    public int hashCode() {
        return wrappedToken.hashCode();
    }
}
