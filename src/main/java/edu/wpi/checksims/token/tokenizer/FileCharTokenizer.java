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

package edu.wpi.checksims.token.tokenizer;

import edu.wpi.checksims.token.ConcreteToken;
import edu.wpi.checksims.token.TokenList;
import edu.wpi.checksims.token.TokenType;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Split a file into a list of character tokens.
 */
public class FileCharTokenizer implements FileTokenizer {
    private static FileCharTokenizer instance;

    private FileCharTokenizer() {}

    public static FileCharTokenizer getInstance() {
        if(instance == null) {
            instance = new FileCharTokenizer();
        }

        return instance;
    }

    /**
     * Split a string into character tokens
     *
     * @param string String to split
     * @return Input string, with a single token representing each character
     */
    @Override
    public TokenList splitFile(String string) {
        checkNotNull(string);

        TokenList toReturn = new TokenList(this.getType());

        char[] chars = string.toCharArray();

        Arrays.stream(ArrayUtils.toObject(chars))
                .map((character) -> new ConcreteToken(character, TokenType.CHARACTER))
                .forEachOrdered(toReturn::add);

        return toReturn;
    }

    @Override
    public TokenType getType() {
        return TokenType.CHARACTER;
    }

    @Override
    public String toString() {
        return "Singleton instance of FileCharSplitter";
    }
}
