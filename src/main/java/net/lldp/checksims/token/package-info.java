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
 * The Token package contains all classes related to Tokens, the most basic unit of comparison in Checksims.
 *
 * Tokens represent a single element of a Submission. They can be a single character, or a string representing a number
 * of characters in the original text.
 *
 * Tokens have a type, representing how the original submission was broken down. Checksims will usually only work with
 * one type of token at a time, and all tokens will be of this type (though this is not a rule, and is not guaranteed;
 * see the Line Comparison Common Code Removal preprocessor for an example of this being violated). Two tokens with
 * different types can never be equal.
 *
 * Tokens also have a Validity. When a token is matched in a Similarity Detector, it is marked as invalid, to indicate
 * that it has been detected as similar (some detectors will also use validity for internal accounting, to ensure that
 * the same token is not matched more than once). A number of Decorators are provided to mutate token equality depending
 * on validity --- for example, to consider two tokens not equal even if identical, if both are invalid.
 *
 * Token itself is an interface, with a single concrete implementation and a number of decorators. The concrete
 * implementation maps individual tokens to "lexemes" --- a single integer representing the input. This results in a
 * size ballooning in the case of characters (UTF-16 code point vs. 32-bit integer), but a size decrease and speed
 * increase for strings. More information can be found in LexemeMap.
 */
package net.lldp.checksims.token;