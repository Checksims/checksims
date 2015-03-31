package edu.wpi.checksims.algorithm.commoncode;

import com.google.common.collect.Iterables;
import edu.wpi.checksims.algorithm.SimilarityDetector;
import edu.wpi.checksims.algorithm.linesimilarity.LineSimilarityChecker;
import edu.wpi.checksims.submission.ConcreteSubmission;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.token.TokenList;
import edu.wpi.checksims.token.TokenType;
import edu.wpi.checksims.token.tokenizer.FileTokenizer;
import edu.wpi.checksims.util.threading.CommonCodeRemovalWorker;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Tests for the Common Code Line Removal Handler
 */
public class CommonCodeLineRemovalHandlerTest {
    private static Submission empty;
    private static Submission abc;
    private static Submission abcde;
    private static SimilarityDetector lineCompare;

    @BeforeClass
    public static void setUp() throws Exception {
        FileTokenizer tokenizer = FileTokenizer.getTokenizer(TokenType.CHARACTER);

        empty = new ConcreteSubmission("Empty", "", new TokenList(TokenType.CHARACTER));
        abc = new ConcreteSubmission("ABC", "A\nB\nC\n", tokenizer.splitFile("A\nB\nC\n"));
        abcde = new ConcreteSubmission("ABCDE", "A\nB\nC\nD\nE\n", tokenizer.splitFile("A\nB\nC\nD\nE\n"));

        lineCompare = LineSimilarityChecker.getInstance();
    }

    @Test
    public void TestCommonCodeRemovalJustWorker() throws Exception {
        CommonCodeRemovalWorker worker = new CommonCodeRemovalWorker(lineCompare, empty, abc);

        Submission result = worker.call();

        assertNotNull(result);
        assertEquals(result.getTokenType(), abc.getTokenType());
        assertEquals(result.getName(), abc.getName());
        assertEquals(result.getContentAsString(), abc.getContentAsString());
        assertEquals(result.getContentAsTokens(), abc.getContentAsTokens());
        assertEquals(result, abc);
    }

    @Test
    public void TestRemoveCommonCodePreservesTokenType() {
        Collection<Submission> removeFrom = Arrays.asList(abc);
        CommonCodeLineRemovalHandler handler = new CommonCodeLineRemovalHandler(empty);
        Collection<Submission> results = handler.handleCommonCode(removeFrom);

        Submission fromResults = Iterables.get(results, 0);

        assertNotNull(results);
        assertEquals(results.size(), 1);
        assertEquals(fromResults.getTokenType(), abc.getTokenType());
        assertEquals(fromResults.getName(), abc.getName());
        assertEquals(fromResults.getContentAsString(), abc.getContentAsString());
        assertEquals(fromResults.getContentAsTokens(), abc.getContentAsTokens());
        assertEquals(fromResults, abc);
    }

    @Test
    public void TestRemoveEmptyCommonCode() {
        Collection<Submission> removeFrom = Arrays.asList(abc, abcde);
        CommonCodeLineRemovalHandler handler = new CommonCodeLineRemovalHandler(empty);
        Collection<Submission> results = handler.handleCommonCode(removeFrom);

        assertNotNull(results);
        System.out.println(results.toString());
        assertEquals(results.size(), 2);
        assertTrue(results.contains(abc));
        assertTrue(results.contains(abcde));
    }

    @Test
    public void TestRemoveCommonCodeFromEmpty() {
        Collection<Submission> removeFrom = Arrays.asList(empty);
        CommonCodeLineRemovalHandler handler = new CommonCodeLineRemovalHandler(abc);
        Collection<Submission> results = handler.handleCommonCode(removeFrom);

        assertNotNull(results);
        assertEquals(results.size(), 1);
        assertTrue(results.contains(empty));
    }
}
