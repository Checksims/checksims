package edu.wpi.checksims.algorithm.preprocessor;

import edu.wpi.checksims.Submission;
import edu.wpi.checksims.util.Token;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests the CharacterLowercasePreprocessor class
 */
public class TestCharacterLowercasePreprocessor {
    private static Submission<Character> empty;
    private static Submission<Character> oneTokenLower;
    private static Submission<Character> oneTokenUpper;
    private static Submission<Character> twoTokensLower;
    private static Submission<Character> twoTokensUpper;
    private static CharacterLowercasePreprocessor instance;

    @BeforeClass
    public static void setUp() {
        List<Token<Character>> emptyList = new LinkedList<>();
        empty = new Submission<>("empty", emptyList);

        List<Token<Character>> oneTokenLowerList = new LinkedList<>();
        oneTokenLowerList.add(new Token<>('a'));
        oneTokenLower = new Submission<>("oneTokenLower", oneTokenLowerList);

        List<Token<Character>> oneTokenUpperList = new LinkedList<>();
        oneTokenUpperList.add(new Token<>('A'));
        oneTokenUpper = new Submission<>("oneTokenUpper", oneTokenUpperList);

        List<Token<Character>> twoTokensLowerList = new LinkedList<>();
        twoTokensLowerList.add(new Token<>('a'));
        twoTokensLowerList.add(new Token<>('b'));
        twoTokensLower = new Submission<>("twoTokensLower", twoTokensLowerList);

        List<Token<Character>> twoTokensUpperList = new LinkedList<>();
        twoTokensUpperList.add(new Token<>('A'));
        twoTokensUpperList.add(new Token<>('B'));
        twoTokensUpper = new Submission<>("twoTokensUpper", twoTokensUpperList);

        instance = CharacterLowercasePreprocessor.getInstance();
    }

    @Test
    public void testEmptyReturnsEmpty() {
        Submission<Character> results = instance.process(empty);

        assertNotNull(results);
        assertEquals(results, empty);
        assertTrue(results.getTokenList().isEmpty());
        assertEquals(results.getNumTokens(), 0);
    }

    @Test
    public void testOneLowerTokenReturnsSameSubmission() {
        Submission<Character> results = instance.process(oneTokenLower);

        assertNotNull(results);
        assertEquals(results, oneTokenLower);
        assertEquals(results.getNumTokens(), 1);
        assertEquals(results.getTokenList(), oneTokenLower.getTokenList());
    }

    @Test
    public void testOneUpperReturnsLowercasedSubmission() {
        Submission<Character> results = instance.process(oneTokenUpper);

        assertNotNull(results);
        assertEquals(results, new Submission<>(oneTokenUpper.getName(), oneTokenLower.getTokenList()));
        assertEquals(results.getNumTokens(), 1);
        assertEquals(results.getTokenList(), oneTokenLower.getTokenList());
    }

    @Test
    public void testTwoLowerReturnsSameSubmission() {
        Submission<Character> results = instance.process(twoTokensLower);

        assertNotNull(results);
        assertEquals(results, twoTokensLower);
        assertEquals(results.getNumTokens(), 2);
        assertEquals(results.getTokenList(), twoTokensLower.getTokenList());
    }

    @Test
    public void testTwoUpperReturnsLowercasedSubmission() {
        Submission<Character> results = instance.process(twoTokensUpper);

        assertNotNull(results);
        assertEquals(results, new Submission<>(twoTokensUpper.getName(), twoTokensLower.getTokenList()));
        assertEquals(results.getNumTokens(), 2);
        assertEquals(results.getTokenList(), twoTokensLower.getTokenList());
    }
}
