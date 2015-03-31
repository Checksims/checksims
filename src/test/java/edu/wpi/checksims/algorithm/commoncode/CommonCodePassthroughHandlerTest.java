package edu.wpi.checksims.algorithm.commoncode;

import edu.wpi.checksims.submission.ConcreteSubmission;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.token.TokenType;
import edu.wpi.checksims.token.tokenizer.FileTokenizer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import static org.junit.Assert.*;

/**
 * Tests for the Common Code passthrough handler
 */
public class CommonCodePassthroughHandlerTest {
    private static CommonCodeHandler passthrough;
    private static Collection<Submission> empty;
    private static Collection<Submission> oneSubmission;
    private static Collection<Submission> twoSubmissions;
    private static Collection<Submission> threeSubmissions;

    @BeforeClass
    public static void setUp() {
        passthrough = CommonCodePassthroughHandler.getInstance();

        FileTokenizer tokenizer = FileTokenizer.getTokenizer(TokenType.CHARACTER);

        Submission a = new ConcreteSubmission("A", "A", tokenizer.splitFile("A"));
        Submission b = new ConcreteSubmission("B", "B", tokenizer.splitFile("B"));
        Submission c = new ConcreteSubmission("C", "C", tokenizer.splitFile("C"));

        empty = new LinkedList<>();

        oneSubmission = Arrays.asList(a);

        twoSubmissions = Arrays.asList(a, b);

        threeSubmissions = Arrays.asList(a, b, c);
    }

    @Test
    public void TestCommonCodePassthroughHandlerEmpty() {
        Collection<Submission> result = passthrough.handleCommonCode(empty);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void TestCommonCodePassthroughSingleSubmission() {
        Collection<Submission> result = passthrough.handleCommonCode(oneSubmission);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(result.size(), oneSubmission.size());
        oneSubmission.stream().forEach((submission) -> assertTrue(result.contains(submission)));
    }

    @Test
    public void TestCommonCodePassthroughTwoSubmissions() {
        Collection<Submission> result = passthrough.handleCommonCode(twoSubmissions);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(result.size(), twoSubmissions.size());
        twoSubmissions.stream().forEach((submission) -> assertTrue(result.contains(submission)));
    }

    @Test
    public void TestCommonCodePassthroughThreeSubmissions() {
        Collection<Submission> result = passthrough.handleCommonCode(threeSubmissions);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(result.size(), threeSubmissions.size());
        threeSubmissions.stream().forEach((submission) -> assertTrue(result.contains(submission)));
    }
}
