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

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Generic reflective class instantiator
 */
public class ReflectiveInstantiator {
    private ReflectiveInstantiator() {}

    /**
     * Instantiate all subclasses of a class in a given package
     *
     * All subclasses MUST implement a static, no arguments getInstance method
     *
     * @param packageName Package name to instantiate in
     * @param subclassesOf Class to instantiate subclasses of
     * @param <T> Type of the original class, which all subclasses will be as well
     * @return List of instances of classes extending/implementing subclassesOf
     */
    public static <T> List<T> reflectiveInstantiator(String packageName, Class<T> subclassesOf) {
        Logger logs = LoggerFactory.getLogger(ReflectiveInstantiator.class);

        List<T> allInstances = new LinkedList<>();

        // Ensure no annoying logs
        Reflections.log = null;

        Reflections searchPackage = new Reflections(packageName);
        Set<Class<? extends T>> subtypes = searchPackage.getSubTypesOf(subclassesOf);

        // Iterate through all of the subclasses
        subtypes.stream().forEach((type) -> {
            logs.debug("Initializing class " + type.getName());

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
                allInstances.add(instance);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Class " + type.getName() + " has no getInstance method!");
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException("Error invoking getInstance for class " + type.getName() + ": " + e.getMessage());
            }
        });

        return allInstances;
    }
}
