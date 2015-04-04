package edu.wpi.checksims.algorithm.commoncode;

import com.google.common.collect.Iterables;
import edu.wpi.checksims.submission.ConcreteSubmission;
import edu.wpi.checksims.submission.EmptySubmissionException;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.token.TokenList;
import edu.wpi.checksims.token.TokenType;
import edu.wpi.checksims.token.tokenizer.FileTokenizer;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the Common Code Line Removal Handler
 */
public class CommonCodeLineRemovalHandlerTest {
    private static Submission empty;
    private static Submission abc;
    private static Submission abcde;
    private static Submission def;
    private static FileTokenizer tokenizer;

    @Before
    public void setUp() throws Exception {
        tokenizer = FileTokenizer.getTokenizer(TokenType.CHARACTER);

        empty = new ConcreteSubmission("Empty", "", new TokenList(TokenType.CHARACTER));
        abc = new ConcreteSubmission("ABC", "A\nB\nC\n", tokenizer.splitFile("A\nB\nC\n"));
        abcde = new ConcreteSubmission("ABCDE", "A\nB\nC\nD\nE\n", tokenizer.splitFile("A\nB\nC\nD\nE\n"));
        def = new ConcreteSubmission("DEF", "D\nE\nF\n", tokenizer.splitFile("D\nE\nF\n"));
    }

    @Test(expected = EmptySubmissionException.class)
    public void TestAttemptEmptyCommonCodeRemovalThrowsException() throws Exception {
        new CommonCodeLineRemovalHandler(empty);
    }

    @Test
    public void TestRemoveCommonCodeFromEmpty() throws Exception {
        CommonCodeLineRemovalHandler handler = new CommonCodeLineRemovalHandler(abc);
        Collection<Submission> removeFrom = Arrays.asList(empty);

        Collection<Submission> result = handler.handleCommonCode(removeFrom);

        assertNotNull(result);
        assertEquals(result.size(), 1);
        Submission processed = Iterables.get(result, 0);
        assertEquals(processed.getName(), empty.getName());
        assertEquals(processed.getContentAsString(), empty.getContentAsString());
        assertEquals(processed.getContentAsTokens(), empty.getContentAsTokens());
        assertEquals(processed, empty);
    }

    @Test
    public void TestRemoveIdenticalCommonCodeReturnsEmpty() throws Exception {
        CommonCodeLineRemovalHandler handler = new CommonCodeLineRemovalHandler(abc);
        Collection<Submission> removeFrom = Arrays.asList(abc);
        Submission expected = new ConcreteSubmission(abc.getName(), empty.getContentAsString(), empty.getContentAsTokens());

        Collection<Submission> results = handler.handleCommonCode(removeFrom);

        assertNotNull(results);
        assertEquals(results.size(), 1);
        Submission processed = Iterables.get(results, 0);
        assertEquals(processed.getName(), expected.getName());
        assertEquals(processed.getContentAsString(), expected.getContentAsString());
        assertEquals(processed.getContentAsTokens(), expected.getContentAsTokens());
        assertEquals(processed, expected);
    }

    @Test
    public void TestRemoveCommonCodeNoOverlapReturnsIdentical() throws Exception {
        CommonCodeLineRemovalHandler handler = new CommonCodeLineRemovalHandler(def);
        Collection<Submission> removeFrom = Arrays.asList(abc);

        Collection<Submission> results = handler.handleCommonCode(removeFrom);

        assertNotNull(results);
        assertEquals(results.size(), 1);
        Submission processed = Iterables.get(results, 0);
        assertEquals(processed.getName(), abc.getName());
        assertEquals(processed.getContentAsString(), abc.getContentAsString());
        assertEquals(processed.getContentAsTokens(), abc.getContentAsTokens());
        assertEquals(processed, abc);
    }

    @Test
    public void TestRemoveCommonCodePartialOverlap() throws Exception {
        CommonCodeLineRemovalHandler handler = new CommonCodeLineRemovalHandler(abc);
        Collection<Submission> removeFrom = Arrays.asList(abcde);
        Submission expected = new ConcreteSubmission(abcde.getName(), "D\nE\n", tokenizer.splitFile("D\nE\n"));

        Collection<Submission> results = handler.handleCommonCode(removeFrom);

        assertNotNull(results);
        assertEquals(results.size(), 1);
        Submission processed = Iterables.get(results, 0);
        assertEquals(processed.getName(), expected.getName());
        assertEquals(processed.getContentAsString(), expected.getContentAsString());
        assertEquals(processed.getContentAsTokens(), expected.getContentAsTokens());
        assertEquals(processed, expected);
    }

    @Test
    public void TestRemoveCommonCodeSubsetOfCommon() throws Exception {
        CommonCodeLineRemovalHandler handler = new CommonCodeLineRemovalHandler(abcde);
        Collection<Submission> removeFrom = Arrays.asList(abc);
        Submission expected = new ConcreteSubmission(abc.getName(), empty.getContentAsString(), empty.getContentAsTokens());

        Collection<Submission> results = handler.handleCommonCode(removeFrom);

        assertNotNull(results);
        assertEquals(results.size(), 1);
        Submission processed = Iterables.get(results, 0);
        assertEquals(processed.getName(), expected.getName());
        assertEquals(processed.getContentAsString(), expected.getContentAsString());
        assertEquals(processed.getContentAsTokens(), expected.getContentAsTokens());
        assertEquals(processed, expected);
    }

    @Test
    public void TestRemoveCommonCodeMultipleSubmissions() throws Exception {
        CommonCodeLineRemovalHandler handler = new CommonCodeLineRemovalHandler(abc);
        Collection<Submission> removeFrom = Arrays.asList(abc, abcde, def);
        Submission expected1 = new ConcreteSubmission(abc.getName(), empty.getContentAsString(), empty.getContentAsTokens());
        Submission expected2 = new ConcreteSubmission(abcde.getName(), "D\nE\n", tokenizer.splitFile("D\nE\n"));
        Submission expected3 = def;

        Collection<Submission> results = handler.handleCommonCode(removeFrom);
        assertNotNull(results);
        assertEquals(results.size(), 3);
        assertTrue(results.contains(expected1));
        assertTrue(results.contains(expected2));
        assertTrue(results.contains(expected3));
    }
}
