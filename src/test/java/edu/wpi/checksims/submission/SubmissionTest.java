package edu.wpi.checksims.submission;

import edu.wpi.checksims.token.TokenType;
import edu.wpi.checksims.token.tokenizer.FileTokenizer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Tests for Submissions
 */
public class SubmissionTest {
    private Submission a;
    private Submission aTwo;
    private Submission aInval;
    private Submission abc;

    @Before
    public void setUp() {
        FileTokenizer tokenizer = FileTokenizer.getTokenizer(TokenType.CHARACTER);

        a = new ConcreteSubmission("a", "a", tokenizer.splitFile("a"));
        aTwo = new ConcreteSubmission("a", "a", tokenizer.splitFile("a"));
        aInval = new ConcreteSubmission("a", "a", tokenizer.splitFile("a"));
        aInval.getContentAsTokens().get(0).setValid(false);

        abc = new ConcreteSubmission("abc", "abc", tokenizer.splitFile("abc"));
    }

    @Test
    public void TestSubmissionEquality() {
        assertEquals(a, aTwo);
    }

    @Test
    public void TestBasicSubmissionOperations() {
        assertEquals(a.getContentAsString(), a.getContentAsTokens().join(false));
        assertEquals(a.getNumTokens(), a.getContentAsTokens().size());
        assertEquals(a.getTokenType(), a.getContentAsTokens().type);
        assertEquals("a", a.getName());

        assertEquals(abc.getNumTokens(), 3);
    }

    @Test
    public void TestSubmissionEqualityIsValiditySensitive() {
        assertNotEquals(a, aInval);
    }

    @Test
    public void TestValidityIgnoringSubmissionEquality() {
        Submission aIgnoring = new ValidityIgnoringSubmission(a);

        assertEquals(aIgnoring, aInval);
        assertEquals(aIgnoring, a);
    }

    @Test
    public void TestValidityEnforcingSubmissionEquality() {
        Submission aEnforcing = new ValidityEnsuringSubmission(a);
        Submission aInvalEnforcing = new ValidityEnsuringSubmission(aInval);

        assertEquals(aEnforcing, a);
        assertNotEquals(aEnforcing, aInval);
        assertNotEquals(aInvalEnforcing, a);
        assertNotEquals(aInvalEnforcing, aInval);
    }
}
