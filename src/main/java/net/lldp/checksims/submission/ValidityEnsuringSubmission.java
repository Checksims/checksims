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
import net.lldp.checksims.token.ValidityEnsuringToken;

import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Submission which enforces token validity - two tokens, even if invalid, are not considered equal.
 *
 * Decorates another submission and overrides equals()
 */
public final class ValidityEnsuringSubmission extends AbstractSubmissionDecorator {
    public ValidityEnsuringSubmission(Submission wrappedSubmission) {
        super(wrappedSubmission);
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Submission)) {
            return false;
        }

        Submission otherSubmission = (Submission)other;

        if(!otherSubmission.getTokenType().equals(this.getTokenType())
                || !otherSubmission.getName().equals(this.getName())
                || !(otherSubmission.getNumTokens() == this.getNumTokens())
                || !(otherSubmission.getContentAsString().equals(this.getContentAsString()))) {
            return false;
        }

        Supplier<TokenList> tokenListSupplier = () -> new TokenList(this.getTokenType());
        TokenList thisList = this.getContentAsTokens().stream()
                .map(ValidityEnsuringToken::new)
                .collect(Collectors.toCollection(tokenListSupplier));
        TokenList otherList = otherSubmission.getContentAsTokens().stream()
                .map(ValidityEnsuringToken::new)
                .collect(Collectors.toCollection(tokenListSupplier));

        return thisList.equals(otherList);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
