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

package net.lldp.checksims.submission;

import net.lldp.checksims.token.TokenList;
import net.lldp.checksims.token.TokenType;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Superclass for submission decorators.
 */
public abstract class AbstractSubmissionDecorator implements Submission {
    private final Submission wrappedSubmission;

    public AbstractSubmissionDecorator(Submission wrappedSubmission) {
        checkNotNull(wrappedSubmission);

        this.wrappedSubmission = wrappedSubmission;
    }

    @Override
    public TokenList getContentAsTokens() {
        return wrappedSubmission.getContentAsTokens();
    }

    @Override
    public String getContentAsString() {
        return wrappedSubmission.getContentAsString();
    }

    @Override
    public String getName() {
        return wrappedSubmission.getName();
    }

    @Override
    public int getNumTokens() {
        return wrappedSubmission.getNumTokens();
    }

    @Override
    public TokenType getTokenType() {
        return wrappedSubmission.getTokenType();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Submission && wrappedSubmission.equals(other);
    }

    @Override
    public String toString() {
        return wrappedSubmission.toString();
    }

    @Override
    public int hashCode() {
        return wrappedSubmission.hashCode();
    }

    @Override
    public int compareTo(Submission other) {
        return wrappedSubmission.compareTo(other);
    }
}
