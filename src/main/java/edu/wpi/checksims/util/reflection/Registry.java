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

import com.google.common.collect.ImmutableMap;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Parent class for all registry implementations
 */
public class Registry<T extends NamedInstantiable> {
    private final Map<String, T> registeredHandlers;

    /**
     * Create a Registry instance for implementations of a given base class in the given package and subpackages
     *
     * Please note that inner classes *WILL NOT BE REGISTERED* - only top-level classes will be included in a registry!
     *
     * @param initPath Package to (recursively) search for implementations
     * @param baseClazz Base class or interface which all implementations in the registry extend or implement
     */
    public Registry(String initPath, Class<T> baseClazz) {
        checkNotNull(initPath);
        checkNotNull(baseClazz);

        Map<String, T> handlers = reflectiveInstantiator(initPath, baseClazz);

        if(handlers.isEmpty()) {
            throw new RuntimeException("Cannot find any valid classes to instantiate in " + initPath);
        }

        // The final list should never change at runtime
        registeredHandlers = ImmutableMap.copyOf(handlers);
    }

    /**
     * @return Names of all supported implementations in this registry
     */
    public Set<String> getSupportedImplementationNames() {
        return registeredHandlers.keySet();
    }

    /**
     * Get an instance of an implementation with given name
     *
     * @param name Name to search for
     * @return Instance of implementation with given name
     * @throws NoSuchImplementationException Thrown if no instance with given name can be found
     */
    public T getImplementationInstance(String name) throws NoSuchImplementationException {
        checkNotNull(name);

        if(!registeredHandlers.containsKey(name.toLowerCase())) {
            throw new NoSuchImplementationException("No implementation available with name " + name);
        }

        return registeredHandlers.get(name.toLowerCase());
    }

    /**
     * Instantiate all subclasses of a class in a given package
     *
     * All subclasses MUST implement a static, no arguments getInstance method
     *
     * Please note that reflectiveInstantiator ignores inner classes --- all classes instantiated will be top level
     *
     * @param packageName Package name to instantiate in
     * @param subclassesOf Class to instantiate subclasses of
     * @param <T> Type of the original class, which all subclasses will be as well
     * @return List of instances of classes extending/implementing subclassesOf
     */
    public static <T extends NamedInstantiable> Map<String, T> reflectiveInstantiator(String packageName, Class<T> subclassesOf) {
        Logger logs = LoggerFactory.getLogger(Registry.class);

        Map<String, T> allInstances = new HashMap<>();

        // Ensure no annoying logs
        Reflections.log = null;

        Reflections searchPackage = new Reflections(packageName);
        Set<Class<? extends T>> subtypes = searchPackage.getSubTypesOf(subclassesOf);

        // Iterate through all of the subclasses
        for(Class<? extends T> type : subtypes) {
            logs.debug("Initializing class " + type.getName());

            // We don't want to instantiate inner classes
            // So check enclosing class. If it's not null, we've found an inner class.
            if(type.getEnclosingClass() != null) {
                logs.trace("Not adding anonymous class " + type.getName() + " to registry");
                continue;
            }

            // TODO maybe just throw these exceptions, and let Registry constructor handle?
            try {
                // Get getInstance method of the class
                Method getInstance = type.getMethod("getInstance");

                // Ensure that getInstance is static
                if(!Modifier.isStatic(getInstance.getModifiers())) {
                    throw new RuntimeException("getInstance method for class " + type.getName() + " is not static!");
                }

                // Ensure that getInstance returns a T
                if(!subclassesOf.isAssignableFrom(getInstance.getReturnType())) {
                    throw new RuntimeException("getInstance method for class " + type.getName() + " does not return a " + subclassesOf.getName());
                }

                // Invoke the method to get an instance
                // Suppress the unchecked cast warning because, while technically unchecked, we verify it works with reflection above
                @SuppressWarnings("unchecked")
                T instance = (T)getInstance.invoke(null);

                if(!allInstances.containsKey(instance.getName())) {
                    allInstances.put(instance.getName().toLowerCase(), instance);
                } else {
                    throw new RuntimeException("Found two instances with duplicated name " + instance.getName());
                }
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Class " + type.getName() + " has no getInstance method!");
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException("Error invoking getInstance for class " + type.getName(), e);
            }
        }

        return allInstances;
    }
}
