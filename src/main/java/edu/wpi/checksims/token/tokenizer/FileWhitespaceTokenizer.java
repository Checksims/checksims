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

package edu.wpi.checksims.token.tokenizer;

import edu.wpi.checksims.token.ConcreteToken;
import edu.wpi.checksims.token.TokenList;
import edu.wpi.checksims.token.TokenType;

import java.util.Arrays;

/**
 * Split a file into tokens based on spaces
 */
public class FileWhitespaceTokenizer implements FileTokenizer {
    private static FileWhitespaceTokenizer instance;

    private FileWhitespaceTokenizer() {}

    public static FileWhitespaceTokenizer getInstance() {
        if(instance == null) {
            instance = new FileWhitespaceTokenizer();
        }

        return instance;
    }

    @Override
    public TokenList splitFile(String string) {
        TokenList toReturn = new TokenList(this.getType());

        if(string == null) {
            return toReturn;
        }

        String[] split = string.split("\\s+");

        Arrays.stream(split)
                .filter((str) -> !str.isEmpty())
                .map((str) -> new ConcreteToken(str, TokenType.WHITESPACE))
                .forEachOrdered(toReturn::add);

        return toReturn;
    }

    @Override
    public TokenType getType() {
        return TokenType.WHITESPACE;
    }

    @Override
    public String toString() {
        return "Singleton instance of FileSpaceSplitter";
    }
}
