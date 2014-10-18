package edu.wpi.checksims.algorithm.preprocessor;

import edu.wpi.checksims.Submission;
import edu.wpi.checksims.util.Token;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test the SubmissionLowercasePreprocessor class
 */
public class TestStringLowercasePreprocessor {
    private static Submission<String> empty;
    private static Submission<String> oneTokenLower;
    private static Submission<String> oneTokenUpper;
    private static Submission<String> twoTokensLower;
    private static Submission<String> twoTokensUpper;
    private static StringLowercasePreprocessor instance;

    @BeforeClass
    public static void setUp() {
        List<Token<String>> emptyTokens = new LinkedList<>();
        empty = new Submission<>("empty", emptyTokens);

        List<Token<String>> oneTokenListLower = new LinkedList<>();
        oneTokenListLower.add(new Token<>("hello"));
        oneTokenLower = new Submission<>("oneTokenLower", oneTokenListLower);

        List<Token<String>> oneTokenListUpper = new LinkedList<>();
        oneTokenListUpper.add(new Token<>("HeLlo"));
        oneTokenUpper = new Submission<>("oneTokenUpper", oneTokenListUpper);

        List<Token<String>> twoTokenListLower = new LinkedList<>();
        twoTokenListLower.add(new Token<>("hello"));
        twoTokenListLower.add(new Token<>("world"));
        twoTokensLower = new Submission<>("twoTokensLower", twoTokenListLower);

        List<Token<String>> twoTokenListUpper = new LinkedList<>();
        twoTokenListUpper.add(new Token<>("HeLlO"));
        twoTokenListUpper.add(new Token<>("WORLD"));
        twoTokensUpper = new Submission<>("twoTokensUpper", twoTokenListUpper);

        instance = StringLowercasePreprocessor.getInstance();
    }

    @Test
    public void testEmptyReturnsEmpty() {
        Submission<String> result = instance.process(empty);

        assertNotNull(result);
        assertEquals(result, empty);
        assertTrue(result.getTokenList().isEmpty());
        assertEquals(result.getNumTokens(), 0);
    }

    @Test
    public void testOneLowercaseReturnsSameSubmission() {
        Submission<String> result = instance.process(oneTokenLower);

        assertNotNull(result);
        assertEquals(result, oneTokenLower);
        assertEquals(result.getNumTokens(), 1);
        assertEquals(result.getTokenList(), oneTokenLower.getTokenList());
    }

    @Test
    public void testOneUppercaseReturnsLowercasedSubmission() {
        Submission<String> result = instance.process(oneTokenUpper);

        assertNotNull(result);
        assertEquals(result, new Submission<>(oneTokenUpper.getName(), oneTokenLower.getTokenList()));
        assertEquals(result.getNumTokens(), 1);
        assertEquals(result.getTokenList(), oneTokenLower.getTokenList());
    }

    @Test
    public void testTwoLowercaseReturnsSameSubmission() {
        Submission<String> result = instance.process(twoTokensLower);

        assertNotNull(result);
        assertEquals(result, twoTokensLower);
        assertEquals(result.getNumTokens(), 2);
        assertEquals(result.getTokenList(), twoTokensLower.getTokenList());
    }

    @Test
    public void testTwoUppercaseReturnsLowercasedSubmission() {
        Submission<String> result = instance.process(twoTokensUpper);

        assertNotNull(result);
        assertEquals(result, new Submission<>(twoTokensUpper.getName(), twoTokensLower.getTokenList()));
        assertEquals(result.getNumTokens(), 2);
        assertEquals(result.getTokenList(), twoTokensLower.getTokenList());
    }
}
