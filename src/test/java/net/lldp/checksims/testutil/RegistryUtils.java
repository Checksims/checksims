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

package net.lldp.checksims.testutil;

import net.lldp.checksims.util.reflection.NamedInstantiable;
import net.lldp.checksims.util.reflection.NoSuchImplementationException;
import net.lldp.checksims.util.reflection.Registry;
import net.lldp.checksims.util.reflection.RegistryWithDefault;

import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Test utilities for registries
 */
public class RegistryUtils {
    private RegistryUtils() {}

    public static <T extends NamedInstantiable> void checkRegistryContainsImpl(String impl, Registry<T> toTest) throws NoSuchImplementationException {
        Collection<String> supportedPrinters = toTest.getSupportedImplementationNames();

        assertNotNull(supportedPrinters);
        assertFalse(supportedPrinters.isEmpty());
        assertTrue(supportedPrinters.contains(impl));

        T implInstance = toTest.getImplementationInstance(impl);
        assertNotNull(implInstance);
        assertEquals(implInstance.getName(), impl);
    }

    public static <T extends NamedInstantiable> void checkRegistryDefault(String expectedDefault, RegistryWithDefault<T> toTest) {
        assertEquals(expectedDefault, toTest.getDefaultImplementationName());

        T defaultImpl = toTest.getDefaultImplementation();
        assertNotNull(defaultImpl);
        assertEquals(expectedDefault, defaultImpl.getName());
    }
}
