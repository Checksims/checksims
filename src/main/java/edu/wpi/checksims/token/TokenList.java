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

import com.google.common.collect.ImmutableList;
import org.apache.commons.collections4.list.PredicatedList;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A list of tokens of a specific type
 */
public class TokenList extends PredicatedList<Token> {
    public final TokenType type;

    private static final long serialVersionUID = 1L;

    public TokenList(TokenType type) {
        super(new LinkedList<>(), (token) -> token.getType().equals(type));
        this.type = type;
    }

    private TokenList(TokenType type, List<Token> baseList) {
        super(baseList, (token) -> token.getType().equals(type));
        this.type = type;
    }

    /**
     * Peforms a shallow copy of a TokenList, returning an immutable version of the initial list
     *
     * @param cloneFrom List to copy
     * @return Immutable copy of cloneFrom
     */
    public static TokenList immutableCopy(TokenList cloneFrom) {
        return new TokenList(cloneFrom.type, ImmutableList.copyOf(cloneFrom));
    }

    /**
     * Perform a deep copy of a TokenList
     *
     * @param cloneFrom List to deep copy
     * @return Cloned copy of the tokenization list
     */
    public static TokenList cloneTokenList(TokenList cloneFrom) {
        Supplier<TokenList> tokenListSupplier = () -> new TokenList(cloneFrom.type);

        return cloneFrom.stream().map(Token::cloneToken).collect(Collectors.toCollection(tokenListSupplier));
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof TokenList)) {
            return false;
        }

        TokenList otherList = (TokenList)other;

        return otherList.type.equals(this.type) && super.equals(otherList);
    }

    @Override
    public String toString() {
        return "Token list of type " + type.toString() + " containing " + super.toString();
    }

    @Override
    public int hashCode() {
        return type.hashCode() ^ super.hashCode();
    }
}
