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

/**
 * This package contains the implementation of a Registry.
 *
 * Registries contain a instances of a number of implementations of a specific interface. These implementations are all
 * named, and can be retrieved by their name. Some Registry implementations also have a default implementation.
 *
 * Registries are used as a sort of module system. They use reflection to identify all implementations of a given
 * interface within a given package, then instantiate them and provide a means for retrieving them. This allows for
 * pluggable implementations which require no modification of existing code to add another.
 *
 * Everything contained in a Registry must implement NamedInstantiable, which provides a method for retrieving the
 * unique name of an implementation. There are also further constraints, described in the NamedInstantiable
 * documentation.
 */
package net.lldp.checksims.util.reflection;
