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

package net.lldp.checksims.util.reflection;

/**
 * All implementations which desire to be instantiated by a Registry must implement this interface.
 *
 * All NamedInstantiable implementations MUST provide a static factory method for producing a single instance. This
 * method should be named "getInstance()" and take no arguments. Failure to do so will result in a crash on startup.
 *
 * It is recommended (but not mandatory) that all namedInstantiable implementations override hashCode() and equals()
 */
public interface NamedInstantiable {
    /**
     * MUST BE UNIQUE FOR EACH IMPLEMENTATING CLASS.
     *
     * Technically, the uniqueness requirement is only within a single registry. For example, there are separate
     * registries for SimilarityDetector and MatrixPrinter. The names for implementations must be unique within these
     * registries, but it is acceptable for a MatrixPrinter to have the same name as a SimilarityDetector because they
     * will never be in the same registry. Despite this, retaining global uniqueness is desirable for clarity.
     *
     * @return Name of the implementation as it will be seen in the registry
     */
    String getName();
}
