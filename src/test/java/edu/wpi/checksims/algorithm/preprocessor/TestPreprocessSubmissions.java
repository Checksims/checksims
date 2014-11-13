package edu.wpi.checksims.algorithm.preprocessor;

import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.util.token.LineToken;
import edu.wpi.checksims.util.token.TokenList;
import edu.wpi.checksims.util.token.TokenType;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for PreprocessSubmissions class
 */
public class TestPreprocessSubmissions {
    private static List<Submission> empty;
    private static List<Submission> oneSubmission;
    private static List<Submission> twoSubmissions;

    @BeforeClass
    public static void setUp() {
        TokenList tokensA = new TokenList(TokenType.LINE);
        tokensA.add(new LineToken("Submission A"));
        Submission a = new Submission("A", tokensA);

        TokenList tokensB = new TokenList(TokenType.LINE);
        tokensB.add(new LineToken("Submission B"));
        Submission b = new Submission("B", tokensB);

        empty = new LinkedList<>();

        oneSubmission = new LinkedList<>();
        oneSubmission.add(a);

        twoSubmissions = new LinkedList<>();
        twoSubmissions.add(a);
        twoSubmissions.add(b);
    }

    @Test
    public void testEmptyReturnsEmpty() {
        List<Submission> results = PreprocessSubmissions.process((s) -> s, empty);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testOneSubmissionIdentity() {
        List<Submission> results = PreprocessSubmissions.process((s) -> s, oneSubmission);

        assertNotNull(results);
        assertEquals(results.size(), 1);
        assertEquals(results, oneSubmission);
        assertEquals(results.get(0), oneSubmission.get(0));
    }

    @Test
    public void testOneSubmissionRename() {
        List<Submission> results = PreprocessSubmissions.process((s) -> new Submission("renamed", s.getTokenList()), oneSubmission);

        Submission expected = new Submission("renamed", oneSubmission.get(0).getTokenList());

        assertNotNull(results);
        assertEquals(results.size(), 1);
        assertEquals(results.get(0), expected);
    }

    @Test
    public void testTwoSubmissionIdentity() {
        List<Submission> results = PreprocessSubmissions.process((s) -> s, twoSubmissions);

        assertNotNull(results);
        assertEquals(results.size(), 2);
        assertTrue(results.contains(twoSubmissions.get(0)));
        assertTrue(results.contains(twoSubmissions.get(1)));
    }

    @Test
    public void testTwoSubmissionRename() {
        List<Submission> results = PreprocessSubmissions.process((s) -> new Submission("renamed " + s.getName(), s.getTokenList()), twoSubmissions);

        List<Submission> expected = new LinkedList<>();
        expected.add(new Submission("renamed " + twoSubmissions.get(0).getName(), twoSubmissions.get(0).getTokenList()));
        expected.add(new Submission("renamed " + twoSubmissions.get(1).getName(), twoSubmissions.get(1).getTokenList()));

        assertNotNull(results);
        assertEquals(results.size(), 2);
        assertTrue(results.contains(expected.get(0)));
        assertTrue(results.contains(expected.get(1)));
    }
}
