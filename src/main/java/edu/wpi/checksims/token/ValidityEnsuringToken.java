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

/**
 * Token which will only be equal to other tokens which are valid
 *
 * Decorates other tokens to override their equals() methods
 */
public final class ValidityEnsuringToken extends AbstractTokenDecorator {
    public ValidityEnsuringToken(Token wrappedToken) {
        super(wrappedToken);
    }

    @Override
    public boolean equals(Object other) {
        // Ensure that comparison against invalid tokens is Very Fast by making this the first condition
        if(!this.isValid()) {
            return false;
        }

        if(!(other instanceof Token)) {
            return false;
        }

        Token otherToken = (Token)other;

        return otherToken.getType().equals(this.getType())
                && otherToken.getLexeme() == this.getLexeme()
                && otherToken.isValid();
    }
}
