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

package edu.wpi.checksims.algorithm.output;

import edu.wpi.checksims.util.reflection.NoSuchImplementationException;
import edu.wpi.checksims.util.reflection.RegistryWithDefault;

/**
 * Registry for valid output strategies
 */
public final class OutputRegistry extends RegistryWithDefault<SimilarityMatrixPrinter> {
    private static OutputRegistry instance;

    private OutputRegistry() throws NoSuchImplementationException {
        super("edu.wpi.checksims.algorithm.output", SimilarityMatrixPrinter.class, SimilarityMatrixThresholdPrinter.getInstance().getName());
    }

    /**
     * @return Sole instance of the OutputRegistry singleton
     */
    public static OutputRegistry getInstance() {
        if(instance == null) {
            try {
                instance = new OutputRegistry();
            } catch(NoSuchImplementationException e) {
                throw new RuntimeException("Error instantiating OutputRegistry", e);
            }
        }

        return instance;
    }

    @Override
    public String toString() {
        return "Singleton instance of OutputRegistry";
    }
}
