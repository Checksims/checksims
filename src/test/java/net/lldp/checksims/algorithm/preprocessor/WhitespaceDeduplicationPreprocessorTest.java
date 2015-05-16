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

package net.lldp.checksims.algorithm.preprocessor;

import net.lldp.checksims.submission.Submission;
import org.junit.Before;
import org.junit.Test;

import static net.lldp.checksims.testutil.PreprocessorUtils.checkPreprocessSubmission;
import static net.lldp.checksims.testutil.PreprocessorUtils.checkPreprocessSubmissionIdentity;
import static net.lldp.checksims.testutil.SubmissionUtils.charSubmissionFromString;
import static net.lldp.checksims.testutil.SubmissionUtils.lineSubmissionFromString;
import static net.lldp.checksims.testutil.SubmissionUtils.whitespaceSubmissionFromString;

/**
 * Tests for the Whitespace Deduplication preprocessor
 */
public class WhitespaceDeduplicationPreprocessorTest {
    private WhitespaceDeduplicationPreprocessor preprocessor;
    private Submission abcCharacter;
    private Submission abcWhitespace;
    private Submission abcLine;
    private Submission characterWhitespaceNonDuplicated;
    private Submission whitespaceWhitespaceNonDuplicated;
    private Submission lineWhitespaceNonDuplicated;
    private Submission characterWhitespaceDuplicated;
    private Submission whitespaceWhitespaceDuplicated;
    private Submission lineWhitespaceDuplicated;

    private final String abcNoDupExpected = "A\n B\n C\n";
    private final String abcDupExpected = " A\n B\n C\n";

    @Before
    public void setUp() {
        final String abc = "ABC";
        final String abcNoDup = "A\n B\n\tC\n";
        final String abcDup = " \t A\n    B\n\n\n  \t\t\t C\n";

        preprocessor = WhitespaceDeduplicationPreprocessor.getInstance();

        abcCharacter = charSubmissionFromString(abc, abc);
        abcWhitespace = whitespaceSubmissionFromString(abc, abc);
        abcLine = lineSubmissionFromString(abc, abc);

        characterWhitespaceNonDuplicated = charSubmissionFromString(abcNoDup, abcNoDup);
        whitespaceWhitespaceNonDuplicated = whitespaceSubmissionFromString(abcNoDup, abcNoDup);
        lineWhitespaceNonDuplicated = lineSubmissionFromString(abcNoDup, abcNoDup);

        characterWhitespaceDuplicated = charSubmissionFromString(abcDup, abcDup);
        whitespaceWhitespaceDuplicated = whitespaceSubmissionFromString(abcDup, abcDup);
        lineWhitespaceDuplicated = lineSubmissionFromString(abcDup, abcDup);
    }

    @Test
    public void TestNoEffectOnNoWhitespaceCharacter() throws Exception {
        checkPreprocessSubmissionIdentity(abcCharacter, preprocessor);
    }

    @Test
    public void TestNoEffectNoWhitespaceWhitespace() throws Exception {
        checkPreprocessSubmissionIdentity(abcWhitespace, preprocessor);
    }

    @Test
    public void TestNoEffectNoWhitespaceLine() throws Exception {
        checkPreprocessSubmissionIdentity(abcLine, preprocessor);
    }

    @Test
    public void TestNoEffectNonDuplicatedCharacter() throws Exception {
        checkPreprocessSubmission(characterWhitespaceNonDuplicated, abcNoDupExpected, preprocessor);
    }

    @Test
    public void TestNoEffectNonDuplicatedWhitespace() throws Exception {
        checkPreprocessSubmission(whitespaceWhitespaceNonDuplicated, abcNoDupExpected, preprocessor);
    }

    @Test
    public void TestNoEffectNonDuplicatedLine() throws Exception {
        checkPreprocessSubmission(lineWhitespaceNonDuplicated, abcNoDupExpected, preprocessor);
    }

    @Test
    public void TestDedupCharacter() throws Exception {
        checkPreprocessSubmission(characterWhitespaceDuplicated, abcDupExpected, preprocessor);
    }

    @Test
    public void TestDedupWhitespace() throws Exception {
        checkPreprocessSubmission(whitespaceWhitespaceDuplicated, abcDupExpected, preprocessor);
    }

    @Test
    public void TestDedupLine() throws Exception {
        checkPreprocessSubmission(lineWhitespaceDuplicated, abcDupExpected, preprocessor);
    }
}
