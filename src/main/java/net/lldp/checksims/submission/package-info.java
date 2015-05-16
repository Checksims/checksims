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
 * The Submission package contains all classes related to Submissions.
 *
 * A Submission contains all the code for a single submission of a single assignment. Checksims compares a number of
 * Submissions for similarities. Submissions are typically formed by reading all files matching a given pattern in a
 * directory, concatenating them, and tokenizing the results (using the methods in the base Submission interface).
 * However, Submissions can be constructed on their own by instantiating a ConcreteSubmission.
 *
 * Submission itself is an interface with one concrete implementation and a number of decorators.
 */
package net.lldp.checksims.submission;