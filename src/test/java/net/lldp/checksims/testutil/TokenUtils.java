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

package net.lldp.checksims.testutil;

import net.lldp.checksims.token.ConcreteToken;
import net.lldp.checksims.token.Token;
import net.lldp.checksims.token.TokenList;
import net.lldp.checksims.token.TokenType;

import java.util.Collections;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Test utils related to tokens
 */
public class TokenUtils {
    private TokenUtils() {}

    /**
     * @param token Tokens to convert to list
     * @return Token List with given tokens in given order as contained tokens
     */
    public static TokenList makeTokenListCharacter(Token... token) {
        TokenList tokens = new TokenList(TokenType.CHARACTER);

        Collections.addAll(tokens, token);

        return tokens;
    }

    /**
     * @param token Characters to convert to list
     * @return Token List with given characters (as tokens) in given order as contained tokens
     */
    public static TokenList makeTokenListCharacter(Character... token) {
        TokenList tokens = new TokenList(TokenType.CHARACTER);

        for(Character c : token) {
            tokens.add(makeCharToken(c));
        }

        return tokens;
    }

    /**
     * @param token Tokens to convert to list
     * @return Token List with given tokens in given order as contained tokens
     */
    public static TokenList makeTokenListWhitespace(Token... token) {
        TokenList tokens = new TokenList(TokenType.WHITESPACE);

        Collections.addAll(tokens, token);

        return tokens;
    }

    /**
     * @param token Strings to convert to list
     * @return Token List with given strings (as tokens) in given order as contained tokens
     */
    public static TokenList makeTokenListWhitespace(String... token) {
        TokenList tokens = new TokenList(TokenType.WHITESPACE);

        for(String s : token) {
            tokens.add(makeWhitespaceToken(s));
        }

        return tokens;
    }

    /**
     * @param token Tokens to convert to list
     * @return Token List with given tokens in given order as contained tokens
     */
    public static TokenList makeTokenListLine(Token... token) {
        TokenList tokens = new TokenList(TokenType.LINE);

        Collections.addAll(tokens, token);

        return tokens;
    }

    /**
     * @param token Strings to convert to list
     * @return Token List with given strings (as tokens) in given order as contained tokens
     */
    public static TokenList makeTokenListLine(String... token) {
        TokenList tokens = new TokenList(TokenType.LINE);

        for(String s : token) {
            tokens.add(makeLineToken(s));
        }

        return tokens;
    }

    /**
     * @param c Character to convert to token
     * @return Valid character token with given content
     */
    public static Token makeCharToken(Character c) {
        checkNotNull(c);

        return new ConcreteToken(c, TokenType.CHARACTER);
    }

    /**
     * Does NOT perform validity checking
     *
     * If there is whitespace in the token, it will never match a token produced by FileWhitespaceTokenizer
     *
     * @param s String to convert to token
     * @return Valid Whitespace token with given content
     */
    public static Token makeWhitespaceToken(String s) {
        checkNotNull(s);

        return new ConcreteToken(s, TokenType.WHITESPACE);
    }

    /**
     * Does NOT perform validity checking
     *
     * If there are newline(s) in the token, it will never match a token produced by FileWhitespaceTokenizer
     *
     * @param s String to convert to token
     * @return Valid Line token with given content
     */
    public static Token makeLineToken(String s) {
        checkNotNull(s);

        return new ConcreteToken(s, TokenType.LINE);
    }
}
