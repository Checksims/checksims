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

import net.lldp.checksims.algorithm.linesimilarity.LineSimilarityChecker;
import net.lldp.checksims.util.reflection.NoSuchImplementationException;
import net.lldp.checksims.util.reflection.RegistryWithDefault;

/**
 * Registry for all supported similarity detection algorithms.
 */
public final class AlgorithmRegistry extends RegistryWithDefault<SimilarityDetector> {
    private static AlgorithmRegistry instance;

    private AlgorithmRegistry() throws NoSuchImplementationException {
        super("net.lldp.checksims.algorithm", SimilarityDetector.class, LineSimilarityChecker.getInstance().getName());
    }

    /**
     * @return Singleton instance of AlgorithmRegistry
     */
    public static AlgorithmRegistry getInstance() {
        if(instance == null) {
            try {
                instance = new AlgorithmRegistry();
            } catch(NoSuchImplementationException e) {
                throw new RuntimeException("Cannot instantiate AlgorithmRegistry", e);
            }
        }

        return instance;
    }

    @Override
    public String toString() {
        return "Singleton instance of AlgorithmRegistry";
    }
}
