package edu.wpi.checksims.algorithm.linesimilarity;

import edu.wpi.checksims.ChecksimsException;
import edu.wpi.checksims.algorithm.AlgorithmResults;
import edu.wpi.checksims.algorithm.SimilarityDetector;
import edu.wpi.checksims.submission.ConcreteSubmission;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.token.TokenList;
import edu.wpi.checksims.token.TokenType;
import edu.wpi.checksims.token.tokenizer.FileTokenizer;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for the Line Comparison algorithm
 */
public class LineSimilarityCheckerTest {
    private static Submission empty;
    private static Submission abc;
    private static Submission aabc;
    private static Submission abcde;
    private static Submission def;
    private static SimilarityDetector lineCompare;

    @BeforeClass
    public static void setUp() throws Exception {
        FileTokenizer tokenizer = FileTokenizer.getTokenizer(TokenType.LINE);

        empty = new ConcreteSubmission("Empty", "", new TokenList(TokenType.LINE));
        abc = new ConcreteSubmission("ABC", "A\nB\nC\n", tokenizer.splitFile("A\nB\nC\n"));
        aabc = new ConcreteSubmission("AABC", "A\nA\nB\nC\n", tokenizer.splitFile("A\nA\nB\nC\n"));
        abcde = new ConcreteSubmission("ABCDE", "A\nB\nC\nD\nE\n", tokenizer.splitFile("A\nB\nC\nD\nE\n"));
        def = new ConcreteSubmission("DEF", "D\nE\nF\n", tokenizer.splitFile("D\nE\nF\n"));

        lineCompare = LineSimilarityChecker.getInstance();
    }

    @Test(expected = ChecksimsException.class)
    public void TestErrorOnTokenTypeMismatch() throws ChecksimsException {
        lineCompare.detectSimilarity(empty, new ConcreteSubmission("Error", "", new TokenList(TokenType.CHARACTER)));
    }

    @Test
    public void TestEmptySubmissionIsZeroPercentSimilar() throws ChecksimsException {
        AlgorithmResults results = lineCompare.detectSimilarity(empty, empty);

        assertNotNull(results);
        assertEquals(0, results.identicalTokensA);
        assertEquals(0, results.identicalTokensB);
        assertEquals(0, results.finalListA.size());
        assertEquals(0, results.finalListB.size());
    }

    @Test
    public void TestEmptySubmissionAndNonemptySubmission() throws ChecksimsException {
        AlgorithmResults results = lineCompare.detectSimilarity(empty, abc);

        assertNotNull(results);
        assertEquals(0, results.identicalTokensA);
        assertEquals(0, results.identicalTokensB);

        if(abc.equals(results.a)) {
            assertEquals(results.finalListA.size(), abc.getNumTokens());
            assertEquals(results.finalListA, abc.getContentAsTokens());
        } else {
            assertTrue(abc.equals(results.b));
            assertEquals(results.finalListB.size(), abc.getNumTokens());
            assertEquals(results.finalListB, abc.getContentAsTokens());
        }
    }

    @Test
    public void TestIdenticalSubmissions() throws Exception {
        AlgorithmResults results = lineCompare.detectSimilarity(abc, abc);

        TokenList expected = TokenList.cloneTokenList(abc.getContentAsTokens());
        expected.stream().forEach((token) -> token.setValid(false));

        assertNotNull(results);
        assertEquals(results.identicalTokensB, results.identicalTokensA);
        assertEquals(results.identicalTokensA, abc.getNumTokens());
        assertEquals(results.finalListA, results.finalListB);
        assertEquals(results.finalListA, expected);
    }

    @Test
    public void TestSubmissionStrictSubset() throws ChecksimsException {
        AlgorithmResults results = lineCompare.detectSimilarity(abc, abcde);

        TokenList expectedAbc = TokenList.cloneTokenList(abc.getContentAsTokens());
        expectedAbc.stream().forEach((token) -> token.setValid(false));
        TokenList expectedAbcde = TokenList.cloneTokenList(abcde.getContentAsTokens());
        expectedAbcde.get(0).setValid(false);
        expectedAbcde.get(1).setValid(false);
        expectedAbcde.get(2).setValid(false);

        assertNotNull(results);
        assertEquals(results.identicalTokensA, results.identicalTokensB);
        assertEquals(results.identicalTokensA, abc.getNumTokens());

        if(abc.equals(results.a)) {
            assertEquals(abcde, results.b);

            assertEquals(results.finalListA, expectedAbc);
            assertEquals(results.finalListB, expectedAbcde);
        } else {
            assertEquals(abc, results.b);
            assertEquals(abcde, results.a);

            assertEquals(results.finalListA, expectedAbcde);
            assertEquals(results.finalListB, expectedAbc);
        }
    }

    @Test
    public void TestSubmissionsNoOverlap() throws ChecksimsException {
        AlgorithmResults results = lineCompare.detectSimilarity(abc, def);

        assertNotNull(results);
        assertEquals(results.identicalTokensA, results.identicalTokensB);
        assertEquals(results.identicalTokensA, 0);

        if(abc.equals(results.a)) {
            assertEquals(def, results.b);

            assertEquals(results.finalListB, def.getContentAsTokens());
            assertEquals(results.finalListA, abc.getContentAsTokens());
        } else {
            assertEquals(abc, results.b);
            assertEquals(def, results.a);

            assertEquals(results.finalListA, def.getContentAsTokens());
            assertEquals(results.finalListB, abc.getContentAsTokens());
        }
    }

    @Test
    public void TestSubmissionsSomeOverlap() throws ChecksimsException {
        AlgorithmResults results = lineCompare.detectSimilarity(abcde, def);

        TokenList expectedAbcde = TokenList.cloneTokenList(abcde.getContentAsTokens());
        expectedAbcde.get(3).setValid(false);
        expectedAbcde.get(4).setValid(false);

        TokenList expectedDef = TokenList.cloneTokenList(def.getContentAsTokens());
        expectedDef.get(0).setValid(false);
        expectedDef.get(1).setValid(false);

        assertNotNull(results);
        assertEquals(results.identicalTokensA, results.identicalTokensB);
        assertEquals(results.identicalTokensA, 2);

        if(abcde.equals(results.a)) {
            assertEquals(def, results.b);

            assertEquals(results.finalListA, expectedAbcde);
            assertEquals(results.finalListB, expectedDef);
        } else {
            assertEquals(abcde, results.b);
            assertEquals(def, results.a);

            assertEquals(results.finalListA, expectedDef);
            assertEquals(results.finalListB, expectedAbcde);
        }
    }

    @Test
    public void TestSubmissionsDuplicatedToken() throws ChecksimsException {
        AlgorithmResults results = lineCompare.detectSimilarity(aabc, abc);

        TokenList expectedAbc = TokenList.cloneTokenList(abc.getContentAsTokens());
        expectedAbc.stream().forEach((token) -> token.setValid(false));

        TokenList expectedAabc = TokenList.cloneTokenList(aabc.getContentAsTokens());
        expectedAabc.stream().forEach((token) -> token.setValid(false));

        assertNotNull(results);
        assertFalse(results.identicalTokensA == results.identicalTokensB);

        if(abc.equals(results.a)) {
            assertEquals(results.b, aabc);

            assertEquals(results.identicalTokensA, abc.getNumTokens());
            assertEquals(results.identicalTokensB, aabc.getNumTokens());

            assertEquals(results.finalListA, expectedAbc);
            assertEquals(results.finalListB, expectedAabc);
        } else {
            assertEquals(results.a, aabc);
            assertEquals(results.b, abc);

            assertEquals(results.identicalTokensB, abc.getNumTokens());
            assertEquals(results.identicalTokensA, aabc.getNumTokens());

            assertEquals(results.finalListB, expectedAbc);
            assertEquals(results.finalListA, expectedAabc);
        }
    }

    @Test
    public void TestSubmissionDuplicatedTokenNotInOtherSubmission() throws ChecksimsException {
        AlgorithmResults results = lineCompare.detectSimilarity(aabc, def);

        assertEquals(results.identicalTokensA, 0);
        assertEquals(results.identicalTokensB, 0);

        if(aabc.equals(results.a)) {
            assertEquals(results.b, def);

            assertEquals(results.finalListA, aabc.getContentAsTokens());
            assertEquals(results.finalListB, def.getContentAsTokens());
        } else {
            assertEquals(aabc, results.b);
            assertEquals(def, results.a);

            assertEquals(results.finalListB, aabc.getContentAsTokens());
            assertEquals(results.finalListA, def.getContentAsTokens());
        }
    }
}
