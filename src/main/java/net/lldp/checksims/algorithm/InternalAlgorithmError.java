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

package net.lldp.checksims.algorithm;

import net.lldp.checksims.ChecksimsException;

/**
 * Exception thrown on internal algorithm error.
 */
public class InternalAlgorithmError extends ChecksimsException {
    private static final long serialVersionUID = 1L;

    public InternalAlgorithmError(String message) {
        super(message);
    }

    public InternalAlgorithmError(String message, Throwable cause) {
        super(message, cause);
    }
}
