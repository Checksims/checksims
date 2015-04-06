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

import edu.wpi.checksims.ChecksimsException;

/**
 * Extension of a Registry with the ability to retrieve a default value
 */
public class RegistryWithDefault<T extends NamedInstantiable> extends Registry<T> {
    private final String defaultImplementation;
    private final T instanceOfDefault;

    /**
     * Create a Registry instance for implementations of a given base class in the given package and subpackages
     *
     * @param initPath Package to (recursively) search for implementations
     * @param baseClazz Base class or interface which all implementations in the registry extend or implement
     * @param defaultImplementation Name of default implementation for this registry
     */
    public RegistryWithDefault(String initPath, Class<T> baseClazz, String defaultImplementation) throws NoSuchImplementationException {
        super(initPath, baseClazz);

        instanceOfDefault = super.getImplementationInstance(defaultImplementation);

        this.defaultImplementation = defaultImplementation;
    }

    /**
     * @return Name of the default implementation for this registry
     */
    public String getDefaultImplementationName() {
        return defaultImplementation;
    }

    /**
     * @return Instance of the default implementation for this registry
     */
    public T getDefaultImplementation() {
        return instanceOfDefault;
    }
}
