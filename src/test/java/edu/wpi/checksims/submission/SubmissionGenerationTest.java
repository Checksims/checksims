package edu.wpi.checksims.submission;

import edu.wpi.checksims.token.TokenType;
import edu.wpi.checksims.token.tokenizer.FileTokenizer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Test creation of submissions from files
 */
public class SubmissionGenerationTest {
    private static File test1;
    private static File test2;
    private static File testEmpty;
    private static File testEmptyFile;
    private static File testOneFile;
    private static File testRecursive;
    private static File testVariedExtensions;
    private static FileTokenizer line;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @BeforeClass
    public static void setUp() {
        final String basePath = SubmissionGenerationTest.class.getResource("").getPath();

        test1 = new File(basePath + "/test1");
        test2 = new File(basePath + "/test2");
        testEmpty = new File(basePath + "/testEmpty");
        testEmptyFile = new File(basePath + "/testEmptyFile");
        testOneFile = new File(basePath + "/testOneFile");
        testRecursive = new File(basePath + "/testRecursive");
        testVariedExtensions = new File(basePath + "/testVariedExtensions");

        line = FileTokenizer.getTokenizer(TokenType.LINE);
    }

    @Test
    public void TestFileListingJustTxt() throws Exception {
        List<File> filesInOne = Submission.getAllMatchingFiles(test1, "*.txt", false);
        assertNotNull(filesInOne);
        assertFalse(filesInOne.isEmpty());
        assertTrue(filesInOne.size() == 3);
        List<String> fileNamesOne = filesInOne.stream().map(File::getName).collect(Collectors.toList());
        assertTrue(fileNamesOne.contains("test.txt"));
        assertTrue(fileNamesOne.contains("test2.txt"));
        assertTrue(fileNamesOne.contains("test3.txt"));

        List<File> filesInTwo = Submission.getAllMatchingFiles(test2, "*.txt", false);
        assertNotNull(filesInTwo);
        assertFalse(filesInTwo.isEmpty());
        assertTrue(filesInTwo.size() == 2);
        List<String> fileNamesTwo = filesInTwo.stream().map(File::getName).collect(Collectors.toList());
        assertTrue(fileNamesTwo.contains("test.txt"));
        assertTrue(fileNamesTwo.contains("test2.txt"));

        List<File> filesInEmpty = Submission.getAllMatchingFiles(testEmpty, "*.txt", false);
        assertNotNull(filesInEmpty);
        assertTrue(filesInEmpty.isEmpty());

        List<File> filesInOneFile = Submission.getAllMatchingFiles(testOneFile, "*.txt", false);
        assertNotNull(filesInOneFile);
        assertEquals(filesInOneFile.size(), 1);
        assertEquals(filesInOneFile.get(0).getName(), "test.txt");

        List<File> filesInEmptyFile = Submission.getAllMatchingFiles(testEmptyFile, "*.txt", false);
        assertNotNull(filesInEmptyFile);
        assertFalse(filesInEmptyFile.isEmpty());
        assertTrue(filesInEmptyFile.size() == 1);
        assertEquals(filesInEmptyFile.get(0).getName(), "empty.txt");
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
    public void TestGetFilesDifferentBlobs() throws Exception {
        List<File> filesTxt = Submission.getAllMatchingFiles(testVariedExtensions, "*.txt", false);
        assertNotNull(filesTxt);
        assertEquals(filesTxt.size(), 1);
        assertEquals(filesTxt.get(0).getName(), "test.txt");

        List<File> filesC = Submission.getAllMatchingFiles(testVariedExtensions, "*.c", false);
        assertNotNull(filesC);
        assertEquals(filesC.size(), 1);
        assertEquals(filesC.get(0).getName(), "test.c");

        List<File> filesCH = Submission.getAllMatchingFiles(testVariedExtensions, "*.{c,h}", false);
        assertNotNull(filesCH);
        assertEquals(filesCH.size(), 2);
        List<String> namesCH = filesCH.stream().map(File::getName).collect(Collectors.toList());
        assertTrue(namesCH.contains("test.c"));
        assertTrue(namesCH.contains("test.h"));
    }

    @Test
    public void TestNullPointerThrowsRuntimeException() throws Exception {
        expectedEx.expect(RuntimeException.class);

        Submission.getAllMatchingFiles(null, "*.txt", false);
    }

    @Test
    public void TestRecursiveTraversal() throws Exception {
        List<File> filesNonRecursive = Submission.getAllMatchingFiles(testRecursive, "*.txt", false);
        assertNotNull(filesNonRecursive);
        assertEquals(filesNonRecursive.size(), 1);
        assertEquals(filesNonRecursive.get(0).getName(), "test1.txt");

        List<File> filesRecursive = Submission.getAllMatchingFiles(testRecursive, "*.txt", true);
        assertNotNull(filesRecursive);
        assertEquals(filesRecursive.size(), 2);
        List<String> namesRecursive = filesRecursive.stream().map(File::getName).collect(Collectors.toList());
        assertTrue(namesRecursive.contains("test1.txt"));
        assertTrue(namesRecursive.contains("test2.txt"));
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

        assertNotNull(empty);
        assertEquals(empty.getName(), "testEmptyFile");
        assertEquals(empty.getNumTokens(), 0);
        assertTrue(empty.getContentAsString().isEmpty());
    }

    @Test
    public void TestGenerateEmptySubmissionMultiFile() throws Exception {
        Submission emptyMulti = Submission.submissionFromDir(test2, "*.txt", line, false);

        assertNotNull(emptyMulti);
        assertEquals(emptyMulti.getName(), "test2");
        assertEquals(emptyMulti.getNumTokens(), 0);
        assertTrue(emptyMulti.getContentAsString().isEmpty());
    }

    @Test
    public void TestGenerateSingleFileSubmissionNonEmpty() throws Exception {
        Submission oneFile = Submission.submissionFromDir(testOneFile, "*.txt", line, false);

        assertNotNull(oneFile);
        assertEquals(oneFile.getName(), "testOneFile");
        assertEquals(oneFile.getNumTokens(), 1);
        assertEquals(oneFile.getContentAsString(), "Hello world.\n"); // Expect an added newline
        assertEquals(oneFile.getContentAsTokens(), line.splitFile("Hello world."));
    }

    @Test
    public void TestGenerateMultiFileSubmissionNonEmpty() throws Exception {
        Submission test = Submission.submissionFromDir(test1, "*.txt", line, false);

        assertNotNull(test);
        assertEquals(test.getName(), "test1");
        assertEquals(test.getNumTokens(), 3);
        List<String> tokensMapped = test.getContentAsTokens().stream().map((token) -> token.getTokenAsString()).collect(Collectors.toList());
        assertTrue(tokensMapped.contains("Test 1"));
        assertTrue(tokensMapped.contains("Test 2"));
        assertTrue(tokensMapped.contains("Test 3"));
        assertEquals(test.getContentAsString(), test.getContentAsTokens().join(false));
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
        File baseDir = new File(SubmissionGenerationTest.class.getResource("").getPath());

        List<Submission> submissionList = Submission.submissionListFromDir(baseDir, "*.txt", line, true);
        Submission sub1 = Submission.submissionFromDir(test1, "*.txt", line, true);
        Submission sub2 = Submission.submissionFromDir(test2, "*.txt", line, true);
        Submission subEmptyFile = Submission.submissionFromDir(testEmptyFile, "*.txt", line, true);
        Submission subOneFile = Submission.submissionFromDir(testOneFile, "*.txt", line, true);
        Submission subRecursive = Submission.submissionFromDir(testRecursive, "*.txt", line, true);
        Submission subVariedExtensions = Submission.submissionFromDir(testVariedExtensions, "*.txt", line, true);

        assertNotNull(submissionList);
        assertEquals(submissionList.size(), 6);
        assertTrue(submissionList.contains(sub1));
        assertTrue(submissionList.contains(sub2));
        assertTrue(submissionList.contains(subEmptyFile));
        assertTrue(submissionList.contains(subOneFile));
        assertTrue(submissionList.contains(subRecursive));
        assertTrue(submissionList.contains(subVariedExtensions));
    }
}
