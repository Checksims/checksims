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

package net.lldp.checksims.submission;

import net.lldp.checksims.token.TokenType;
import net.lldp.checksims.token.tokenizer.Tokenizer;
import net.lldp.checksims.testutil.SubmissionUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test creation of submissions from files
 */
public class SubmissionGenerationTest {
    private static String basePath;
    private File test1;
    private File test2;
    private File testEmpty;
    private File testEmptyFile;
    private File testOneFile;
    private File testRecursive;
    private File testVariedExtensions;
    private Tokenizer line;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() {
        basePath = SubmissionGenerationTest.class.getResource("").getPath();

        test1 = new File(basePath + "/test1");
        test2 = new File(basePath + "/test2");
        testEmpty = new File(basePath + "/testEmpty");
        testEmptyFile = new File(basePath + "/testEmptyFile");
        testOneFile = new File(basePath + "/testOneFile");
        testRecursive = new File(basePath + "/testRecursive");
        testVariedExtensions = new File(basePath + "/testVariedExtensions");

        line = Tokenizer.getTokenizer(TokenType.LINE);
    }

    public static List<File> namesToFiles(String prefix, List<String> names) {
        return names.stream().map((name) -> new File(basePath + "/" + prefix + "/" + name)).collect(Collectors.toList());
    }

    @Test
    public void TestFileListingTest1JustTxt() throws Exception {
        Collection<File> files = Submission.getAllMatchingFiles(test1, "*.txt", false);

        SubmissionUtils.checkFileCollections(files, namesToFiles(test1.getName(), Arrays.asList("test.txt", "test2.txt", "test3.txt")));
    }

    @Test
    public void TestFileListingTest2JustTxt() throws Exception {
        Collection<File> files = Submission.getAllMatchingFiles(test2, "*.txt", false);

        SubmissionUtils.checkFileCollections(files, namesToFiles(test2.getName(), Arrays.asList("test.txt", "test2.txt")));
    }

    @Test
    public void TestFileListingTestEmptyJustTxt() throws Exception {
        Collection<File> files = Submission.getAllMatchingFiles(testEmpty, "*.txt", false);

        SubmissionUtils.checkFileCollections(files, new LinkedList<>());
    }

    @Test
    public void TestFileListingTestOneFileJustTxt() throws Exception {
        Collection<File> files = Submission.getAllMatchingFiles(testOneFile, "*.txt", false);

        SubmissionUtils.checkFileCollections(files, namesToFiles(testOneFile.getName(), singletonList("test.txt")));
    }

    @Test
    public void TestFileListingTestEmptyFileJustTxt() throws Exception {
        Collection<File> files = Submission.getAllMatchingFiles(testEmptyFile, "*.txt", false);

        SubmissionUtils.checkFileCollections(files, namesToFiles(testEmptyFile.getName(), singletonList("empty.txt")));
    }

    @Test(expected = NoSuchFileException.class)
    public void TestNonExistantFileThrowsException() throws Exception {
        Submission.getAllMatchingFiles(new File("does_not_exist"), "*.txt", false);
    }

    @Test(expected = NotDirectoryException.class)
    public void TestFileNotDirectoryThrowsException() throws Exception {
        Submission.getAllMatchingFiles(new File(test1.getAbsolutePath() + "/test.txt"), "*.txt", false);
    }

    @Test
    public void TestFileListingVariedExtensionsTxt() throws Exception {
        Collection<File> files = Submission.getAllMatchingFiles(testVariedExtensions, "*.txt", false);

        SubmissionUtils.checkFileCollections(files, namesToFiles(testVariedExtensions.getName(), singletonList("test.txt")));
    }

    @Test
    public void TestFileListingVariedExtensionsC() throws Exception {
        Collection<File> files = Submission.getAllMatchingFiles(testVariedExtensions, "*.c", false);

        SubmissionUtils.checkFileCollections(files, namesToFiles(testVariedExtensions.getName(), singletonList("test.c")));
    }

    @Test
    public void TestFileListingVariedExtensionsCAndH() throws Exception {
        Collection<File> files = Submission.getAllMatchingFiles(testVariedExtensions, "*.{c,h}", false);

        SubmissionUtils.checkFileCollections(files, namesToFiles(testVariedExtensions.getName(), Arrays.asList("test.c", "test.h")));
    }

    @Test
    public void TestNullPointerThrowsRuntimeException() throws Exception {
        expectedEx.expect(RuntimeException.class);

        Submission.getAllMatchingFiles(null, "*.txt", false);
    }

    @Test
    public void TestFileListingNonRecursive() throws Exception {
        Collection<File> files = Submission.getAllMatchingFiles(testRecursive, "*.txt", false);

        SubmissionUtils.checkFileCollections(files, namesToFiles(testRecursive.getName(), singletonList("test1.txt")));
    }

    @Test
    public void TestFileListingRecursive() throws Exception {
        Collection<File> files = Submission.getAllMatchingFiles(testRecursive, "*.txt", true);

        SubmissionUtils.checkFileCollections(files, namesToFiles(testRecursive.getName(), Arrays.asList("test1.txt", "subDir/test2.txt")));
    }

    @Test(expected = NoSuchFileException.class)
    public void TestGenerateSubmissionNonexistantDir() throws Exception {
        Submission.submissionFromDir(new File("does_not_exist"), "*.txt", line, false);
    }

    @Test(expected = NotDirectoryException.class)
    public void TestGenerateSubmissionFromFileNotDir() throws Exception {
        Submission.submissionFromDir(new File(testEmptyFile.getAbsolutePath() + "/empty.txt"), "*.txt", line, false);
    }

    @Test(expected = NoMatchingFilesException.class)
    public void TestGenerateEmptySubmission() throws Exception {
        Submission.submissionFromDir(testEmpty, "*.txt", line, false);
    }

    @Test
    public void TestGenerateEmptySubmissionOneFile() throws Exception {
        Submission empty = Submission.submissionFromDir(testEmptyFile, "*.txt", line, false);
        Submission expected = SubmissionUtils.lineSubmissionFromString(testEmptyFile.getName(), "");

        assertNotNull(empty);
        assertEquals(expected, empty);
    }

    @Test
    public void TestGenerateEmptySubmissionMultiFile() throws Exception {
        Submission emptyMulti = Submission.submissionFromDir(test2, "*.txt", line, false);
        Submission expected = SubmissionUtils.lineSubmissionFromString(test2.getName(), "");

        assertNotNull(emptyMulti);
        assertEquals(expected, emptyMulti);
    }

    @Test
    public void TestGenerateSingleFileSubmissionNonEmpty() throws Exception {
        Submission oneFile = Submission.submissionFromDir(testOneFile, "*.txt", line, false);
        Submission expected = SubmissionUtils.lineSubmissionFromString(testOneFile.getName(), "Hello world.\n");

        assertNotNull(oneFile);
        assertEquals(expected, oneFile);
    }

    @Test
    public void TestGenerateMultiFileSubmissionNonEmpty() throws Exception {
        Submission test = Submission.submissionFromDir(test1, "*.txt", line, false);
        Submission expected = SubmissionUtils.lineSubmissionFromString(test1.getName(), "Test 1\nTest 2\nTest 3\n");

        assertNotNull(test);
        assertEquals(expected, test);
    }

    @Test(expected = NoSuchFileException.class)
    public void TestGenerateListOfSubmissionFromNonexistantFile() throws Exception {
        Submission.submissionListFromDir(new File("does_not_exist"), "*.txt", line, false);
    }

    @Test(expected = NotDirectoryException.class)
    public void TestGenerateListOfSubmissionFromFile() throws Exception {
        Submission.submissionListFromDir(new File(testEmptyFile.getAbsolutePath() + "/empty.txt"), "*.txt", line, false);
    }

    @Test
    public void TestGenerateListOfSubmissionFromDir() throws Exception {
        Set<Submission> submissionList = Submission.submissionListFromDir(new File(basePath), "*.txt", line, true);

        Submission sub1 = Submission.submissionFromDir(test1, "*.txt", line, true);
        Submission sub2 = Submission.submissionFromDir(test2, "*.txt", line, true);
        Submission subEmptyFile = Submission.submissionFromDir(testEmptyFile, "*.txt", line, true);
        Submission subOneFile = Submission.submissionFromDir(testOneFile, "*.txt", line, true);
        Submission subRecursive = Submission.submissionFromDir(testRecursive, "*.txt", line, true);
        Submission subVariedExtensions = Submission.submissionFromDir(testVariedExtensions, "*.txt", line, true);

        List<Submission> expected = Arrays.asList(sub1, sub2, subEmptyFile, subOneFile, subRecursive, subVariedExtensions);

        SubmissionUtils.checkSubmissionCollections(expected, submissionList);
    }
}
