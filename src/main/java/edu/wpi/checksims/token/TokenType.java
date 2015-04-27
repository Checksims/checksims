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

import edu.wpi.checksims.ChecksimsException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Supported tokenization types
 */
public enum TokenType {
    CHARACTER("character"),
    WHITESPACE("whitespace"),
    LINE("line");

    private String name;

    TokenType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Token of type " + name;
    }

    public static TokenType fromString(String input) throws ChecksimsException {
        checkNotNull(input);
        checkArgument(!input.isEmpty(), "Empty string is not a valid token type!");

        String lowerInput = input.toLowerCase();

        TokenType[] types = TokenType.class.getEnumConstants();

        // Filter to find anything with a matching name
        List<TokenType> matching = Arrays.stream(types).filter((type) -> type.name.equals(lowerInput)).collect(Collectors.toList());

        // If we find nothing, throw an exception
        if(matching.size() == 0) {
            throw new ChecksimsException("No tokenization with name " + input + " found!");
        }

        // If we get two or more, there's a serious problem
        if(matching.size() > 1) {
            throw new ChecksimsException("FATAL: Encountered multiple tokenizations with identical names!");
        }

        // Otherwise, just return the sole thing we filtered to
        return matching.get(0);
    }
}
