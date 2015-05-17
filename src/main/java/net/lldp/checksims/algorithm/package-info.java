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
 * This package contains Similarity Detectors, Preprocessors, the Similarity Matrix, and Output Strategies.
 *
 * Preprocessors, the Similarity Matrix, and Output Strategies are all handled within subpackages. The base package
 * contains SimilarityDetector, the base interface for all Similarity Detection Algorithms, and a registry for these
 * implementations.
 *
 * Also present are utilities for running algorithms, and the Result class representing the output of a Similarity
 * Detector.
 */
package net.lldp.checksims.algorithm;
