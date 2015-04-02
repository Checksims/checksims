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

package edu.wpi.checksims.algorithm;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import edu.wpi.checksims.ChecksimException;
import edu.wpi.checksims.util.reflection.ReflectiveInstantiator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Registry for all supported plagiarism detection algorithms
 */
public class AlgorithmRegistry {
    private final List<SimilarityDetector> supportedAlgorithms;

    private static AlgorithmRegistry instance;

    private AlgorithmRegistry() {
        List<SimilarityDetector> detectors = ReflectiveInstantiator.reflectiveInstantiator("edu.wpi.checksims.algorithm", SimilarityDetector.class);

        if(detectors.isEmpty()) {
            throw new RuntimeException("No plagiarism detection algorithms registered! Cannot continue!");
        }

        // Get a list without duplicates
        // If it's a different size, then duplicates existed, which is bad
        // Throw a RuntimeException for that!
        ImmutableList<String> noDups = ImmutableSet.copyOf(detectors.stream().map(SimilarityDetector::getName).collect(Collectors.toList())).asList();
        if(noDups.size() < detectors.size()) {
            throw new RuntimeException("Some algorithm names were not globally unique!");
        }

        // The final list should never change at runtime
        supportedAlgorithms = ImmutableList.copyOf(detectors);
    }

    public static AlgorithmRegistry getInstance() {
        if(instance == null) {
            instance = new AlgorithmRegistry();
        }

        return instance;
    }

    /**
     * For convenience's sake, the first algorithm added to the list of detectors is considered to be the default
     *
     * @return Default plagiarism detection algorithm
     */
    public SimilarityDetector getDefaultAlgorithm() {
        return supportedAlgorithms.get(0);
    }

    /**
     * @return Name of default plagiarism detection algorithm
     */
    public String getDefaultAlgorithmName() {
        return getDefaultAlgorithm().getName();
    }

    /**
     * @return List of names of supported algorithms
     */
    public List<String> getSupportedAlgorithmNames() {
        return supportedAlgorithms.stream().map(SimilarityDetector::getName).collect(Collectors.toList());
    }

    /**
     * Get a specific plagiarism detection algorithm by its CLI name
     *
     * @param name CLI invocation name
     * @return Plagiarism detection algorithm of that name
     * @throws ChecksimException Thrown on no algorithm or more than one algorithm of that name existing
     */
    public SimilarityDetector getAlgorithmInstance(String name) throws ChecksimException {
        String lowerName = name.toLowerCase(); // Ensure case insensitivity
        List<SimilarityDetector> detectors = supportedAlgorithms.stream().filter((alg) -> alg.getName().equals(lowerName)).collect(Collectors.toList());

        if(detectors.size() == 0) {
            throw new ChecksimException("No algorithm with name " + name);
        } else if(detectors.size() > 1) {
            throw new ChecksimException("INTERNAL ERROR: two algorithms share the same name!");
        }

        return detectors.get(0);
    }
}
