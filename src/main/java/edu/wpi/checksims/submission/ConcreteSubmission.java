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

package edu.wpi.checksims.submission;

import edu.wpi.checksims.token.TokenList;
import edu.wpi.checksims.token.TokenType;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete implementation of the Submission interface.
 *
 * Intended to be the only concrete implementation of Submission that is not a decorator.
 */
public final class ConcreteSubmission implements Submission {
    private final TokenList tokenList;
    private final String content;
    private final String name;

    public ConcreteSubmission(String name, String content, TokenList tokens) {
        checkNotNull(name);
        checkArgument(!name.isEmpty(), "Submission name cannot be empty");
        checkNotNull(content);
        checkNotNull(tokens);

        this.name = name;
        this.content = content;
        this.tokenList = TokenList.immutableCopy(tokens);
    }

    @Override
    public TokenList getContentAsTokens() {
        return tokenList;
    }

    @Override
    public String getContentAsString() {
        return content;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getNumTokens() {
        return tokenList.size();
    }

    @Override
    public TokenType getTokenType() {
        return tokenList.type;
    }

    @Override
    public String toString() {
        return "A submission with name " + name + " and " + getNumTokens() + " tokens";
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof ConcreteSubmission)) {
            return false;
        }

        Submission otherSubmission = (Submission)other;

        return otherSubmission.getName().equals(this.name) && otherSubmission.getNumTokens() == this.getNumTokens() && otherSubmission.getContentAsTokens().equals(this.tokenList) && otherSubmission.getContentAsString().equals(this.content);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
