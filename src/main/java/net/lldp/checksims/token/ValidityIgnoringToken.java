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

package net.lldp.checksims.token;

/**
 * Token which ignores validity when comparing.
 *
 * Decorates other tokens to override their equals() method
 */
public final class ValidityIgnoringToken extends AbstractTokenDecorator {
    public ValidityIgnoringToken(Token wrappedToken) {
        super(wrappedToken);
    }

    /**
     * This method checks another token for equality, ignoring their validity.
     *
     * This means that, if two tokens with the same type and content but different validites are compare, this method
     * WILL RETURN TRUE. This is a violation of the equals() contract. Hence, use ValidityIgnoringToken sparingly and
     * with care.
     *
     * @param other Object to compare against
     * @return True if Other is a token of identical type and content (IGNORES VALIDITY)
     */
    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Token)) {
            return false;
        }

        Token otherToken = (Token)other;

        return otherToken.getType().equals(this.getType()) && otherToken.getLexeme() == this.getLexeme();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
