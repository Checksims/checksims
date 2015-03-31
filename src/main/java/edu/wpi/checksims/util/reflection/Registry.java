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

package edu.wpi.checksims.util.reflection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import edu.wpi.checksims.ChecksimException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Parent class for all registry implementations
 */
public class Registry<T extends NamedInstantiable> {
    private final List<T> registeredHandlers;

    /**
     * Create a Registry instance for implementations of a given base class in the given package and subpackages
     *
     * @param initPath Package to (recursively) search for implementations
     * @param baseClazz Base class or interface which all implementations in the registry extend or implement
     */
    public Registry(String initPath, Class baseClazz) {
        List<T> handlers = ReflectiveInstantiator.reflectiveInstantiator(initPath, baseClazz);

        if(handlers.isEmpty()) {
            throw new RuntimeException("Cannot find any valid classes to instantiate in " + initPath);
        }

        // Get a list without duplicates
        // If it's a different size, then duplicates existed, which is bad
        // Throw a RuntimeException for that!
        ImmutableList<String> noDups = ImmutableSet.copyOf(handlers.stream().map((handler) -> handler.getName().toLowerCase()).collect(Collectors.toList())).asList();
        if(noDups.size() < handlers.size()) {
            throw new RuntimeException("Some algorithm names were not globally unique!");
        }

        // The final list should never change at runtime
        registeredHandlers = ImmutableList.copyOf(handlers);
    }

    /**
     * @return Names of all supported implementations in this registry
     */
    public Collection<String> getSupportedImplementationNames() {
        return registeredHandlers.stream().map((handler) -> handler.getName()).collect(Collectors.toList());
    }

    /**
     * Get an instance of an implementation with given name
     *
     * @param name Name to search for
     * @return Instance of implementation with given name
     * @throws ChecksimException Thrown if no instance with given name can be found
     */
    public T getImplementationInstance(String name) throws ChecksimException {
        List<T> matchingImpls = registeredHandlers.stream().filter((handler) -> handler.getName().equalsIgnoreCase(name)).collect(Collectors.toList());

        if(matchingImpls.size() == 0) {
            throw new ChecksimException("No implementation available with name " + name);
        } else if(matchingImpls.size() > 1) {
            throw new ChecksimException("INTERNAL ERROR: Two implementations found with same name " + name +" !");
        }

        return matchingImpls.get(0);
    }
}
