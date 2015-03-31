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

package edu.wpi.checksims.algorithm.preprocessor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import edu.wpi.checksims.ChecksimException;
import edu.wpi.checksims.util.reflection.ReflectiveInstantiator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Registry to obtain valid preprocessors
 */
public class PreprocessorRegistry {
    private final List<SubmissionPreprocessor> validPreprocessors;

    private static PreprocessorRegistry instance;

    private PreprocessorRegistry() {
        // Use reflection to get all SubmissionPreprocessors in this package
        List<SubmissionPreprocessor> preprocessors = ReflectiveInstantiator.reflectiveInstantiator("edu.wpi.checksims.algorithm.preprocessor", SubmissionPreprocessor.class);

        // We don't need to ensure that the list isn't empty
        // Output strategies and algorithms must exist to run
        // Preprocessors, not so

        // Get a list without duplicates
        // If it's a different size, then duplicates existed, which is bad
        // Throw a RuntimeException for that!
        ImmutableList<String> noDups = ImmutableSet.copyOf(preprocessors.stream().map(SubmissionPreprocessor::getName).collect(Collectors.toList())).asList();
        if(noDups.size() < preprocessors.size()) {
            throw new RuntimeException("Some algorithm names were not globally unique!");
        }

        // Assign the valid preprocesors list as immutable
        validPreprocessors = ImmutableList.copyOf(preprocessors);
    }

    public static PreprocessorRegistry getInstance() {
        if(instance == null) {
            instance = new PreprocessorRegistry();
        }

        return instance;
    }

    public List<String> getPreprocessorNames() {
        return validPreprocessors.stream().map(SubmissionPreprocessor::getName).collect(Collectors.toList());
    }

    public SubmissionPreprocessor getPreprocessor(String name) throws ChecksimException {
        String lowerName = name.toLowerCase();

        List<SubmissionPreprocessor> matching = validPreprocessors.stream().filter((preprocessor) -> preprocessor.getName().equals(lowerName)).collect(Collectors.toList());

        if(matching.isEmpty()) {
            throw new ChecksimException("No preprocessor with name " + name + " found!");
        } else if(matching.size() > 1) {
            throw new ChecksimException("Overlapping algorithm names encountered - names must be globally unique!");
        }

        return matching.get(0);
    }
}
