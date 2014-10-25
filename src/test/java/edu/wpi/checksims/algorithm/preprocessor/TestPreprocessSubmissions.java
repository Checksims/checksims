package edu.wpi.checksims.algorithm.preprocessor;

import edu.wpi.checksims.Submission;
import edu.wpi.checksims.util.Token;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for PreprocessSubmissions class
 */
public class TestPreprocessSubmissions {
    private static List<Submission<String>> empty;
    private static List<Submission<String>> oneSubmission;
    private static List<Submission<String>> twoSubmissions;

    @BeforeClass
    public static void setUp() {
        List<Token<String>> tokensA = new LinkedList<>();
        tokensA.add(new Token<>("Submission A"));
        Submission<String> a = new Submission<>("A", tokensA);

        List<Token<String>> tokensB = new LinkedList<>();
        tokensB.add(new Token<>("Submission B"));
        Submission<String> b = new Submission<>("B", tokensB);

        empty = new LinkedList<>();

        oneSubmission = new LinkedList<>();
        oneSubmission.add(a);

        twoSubmissions = new LinkedList<>();
        twoSubmissions.add(a);
        twoSubmissions.add(b);
    }

    @Test
    public void testEmptyReturnsEmpty() {
        List<Submission<String>> results = PreprocessSubmissions.process((s) -> s, empty);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testOneSubmissionIdentity() {
        List<Submission<String>> results = PreprocessSubmissions.process((s) -> s, oneSubmission);

        assertNotNull(results);
        assertEquals(results.size(), 1);
        assertEquals(results, oneSubmission);
        assertEquals(results.get(0), oneSubmission.get(0));
    }

    @Test
    public void testOneSubmissionRename() {
        List<Submission<String>> results = PreprocessSubmissions.process((s) -> new Submission<String>("renamed", s.getTokenList()), oneSubmission);

        Submission<String> expected = new Submission<>("renamed", oneSubmission.get(0).getTokenList());

        assertNotNull(results);
        assertEquals(results.size(), 1);
        assertEquals(results.get(0), expected);
    }

    @Test
    public void testTwoSubmissionIdentity() {
        List<Submission<String>> results = PreprocessSubmissions.process((s) -> s, twoSubmissions);

        assertNotNull(results);
        assertEquals(results.size(), 2);
        assertTrue(results.contains(twoSubmissions.get(0)));
        assertTrue(results.contains(twoSubmissions.get(1)));
    }

    @Test
    public void testTwoSubmissionRename() {
        List<Submission<String>> results = PreprocessSubmissions.process((s) -> new Submission<String>("renamed " + s.getName(), s.getTokenList()), twoSubmissions);

        List<Submission<String>> expected = new LinkedList<>();
        expected.add(new Submission<>("renamed " + twoSubmissions.get(0).getName(), twoSubmissions.get(0).getTokenList()));
        expected.add(new Submission<>("renamed " + twoSubmissions.get(1).getName(), twoSubmissions.get(1).getTokenList()));

        assertNotNull(results);
        assertEquals(results.size(), 2);
        assertTrue(results.contains(expected.get(0)));
        assertTrue(results.contains(expected.get(1)));
    }
}
