package edu.wpi.checksims.algorithm.preprocessor;

import edu.wpi.checksims.submission.ConcreteSubmission;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.token.TokenType;
import edu.wpi.checksims.token.tokenizer.FileTokenizer;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the Whitespace Deduplication preprocessor
 */
public class WhitespaceDeduplicationPreprocessorTest {
    private static WhitespaceDeduplicationPreprocessor preprocessor;
    private static Submission abcCharacter;
    private static Submission abcWhitespace;
    private static Submission abcLine;
    private static Submission characterWhitespaceNonDuplicated;
    private static Submission whitespaceWhitespaceNonDuplicated;
    private static Submission lineWhitespaceNonDuplicated;
    private static Submission characterWhitespaceDuplicated;
    private static Submission whitespaceWhitespaceDuplicated;
    private static Submission lineWhitespaceDuplicated;

    private static final String abc = "ABC";
    private static final String abcNoDup = "A\n B\n\tC\n";
    private static final String abcNoDupExpected = "A\n B\n C\n";
    private static final String abcDup = " \t A\n    B\n\n\n  \t\t\t C\n";
    private static final String abcDupExpected = " A\n B\n C\n";

    @BeforeClass
    public static void setUp() {
        FileTokenizer character = FileTokenizer.getTokenizer(TokenType.CHARACTER);
        FileTokenizer line = FileTokenizer.getTokenizer(TokenType.LINE);
        FileTokenizer whitespace = FileTokenizer.getTokenizer(TokenType.WHITESPACE);

        preprocessor = WhitespaceDeduplicationPreprocessor.getInstance();

        abcCharacter = new ConcreteSubmission(abc, abc, character.splitFile(abc));
        abcWhitespace = new ConcreteSubmission(abc, abc, whitespace.splitFile(abc));
        abcLine = new ConcreteSubmission(abc, abc, line.splitFile(abc));

        characterWhitespaceNonDuplicated = new ConcreteSubmission(abcNoDup, abcNoDup, character.splitFile(abcNoDup));
        whitespaceWhitespaceNonDuplicated = new ConcreteSubmission(abcNoDup, abcNoDup, whitespace.splitFile(abcNoDup));
        lineWhitespaceNonDuplicated = new ConcreteSubmission(abcNoDup, abcNoDup, line.splitFile(abcNoDup));

        characterWhitespaceDuplicated = new ConcreteSubmission(abcDup, abcDup, character.splitFile(abcDup));
        whitespaceWhitespaceDuplicated = new ConcreteSubmission(abcDup, abcDup, whitespace.splitFile(abcDup));
        lineWhitespaceDuplicated = new ConcreteSubmission(abcDup, abcDup, line.splitFile(abcDup));
    }

    @Test
    public void TestNoEffectOnNoWhitespaceCharacter() {
        Submission result = preprocessor.process(abcCharacter);
        assertEquals(result, abcCharacter);
    }

    @Test
    public void TestNoEffectNoWhitespaceWhitespace() {
        Submission result = preprocessor.process(abcWhitespace);
        assertEquals(result, abcWhitespace);
    }

    @Test
    public void TestNoEffectNoWhitespaceLine() {
        Submission result = preprocessor.process(abcLine);
        assertEquals(result, abcLine);
    }

    @Test
    public void TestNoEffectNonDuplicatedCharacter() {
        FileTokenizer character = FileTokenizer.getTokenizer(TokenType.CHARACTER);
        Submission result = preprocessor.process(characterWhitespaceNonDuplicated);
        Submission expected = new ConcreteSubmission(characterWhitespaceNonDuplicated.getName(), abcNoDupExpected, character.splitFile(abcNoDupExpected));
        assertEquals(result, expected);
    }

    @Test
    public void TestNoEffectNonDuplicatedWhitespace() {
        Submission result = preprocessor.process(whitespaceWhitespaceNonDuplicated);
        Submission expected = new ConcreteSubmission(whitespaceWhitespaceNonDuplicated.getName(), abcNoDupExpected, whitespaceWhitespaceNonDuplicated.getContentAsTokens());
        assertEquals(result, expected);
    }

    @Test
    public void TestNoEffectNonDuplicatedLine() {
        FileTokenizer line = FileTokenizer.getTokenizer(TokenType.LINE);
        Submission result = preprocessor.process(lineWhitespaceNonDuplicated);
        Submission expected = new ConcreteSubmission(lineWhitespaceNonDuplicated.getName(), abcNoDupExpected, line.splitFile(abcNoDupExpected));
        assertEquals(result, expected);
    }

    @Test
    public void TestDedupCharacter() {
        FileTokenizer character = FileTokenizer.getTokenizer(TokenType.CHARACTER);
        Submission result = preprocessor.process(characterWhitespaceDuplicated);
        Submission expected = new ConcreteSubmission(characterWhitespaceDuplicated.getName(), abcDupExpected, character.splitFile(abcDupExpected));
        assertEquals(expected, result);
    }

    @Test
    public void TestDedupWhitespace() {
        Submission result = preprocessor.process(whitespaceWhitespaceDuplicated);
        Submission expected = new ConcreteSubmission(whitespaceWhitespaceDuplicated.getName(), abcDupExpected, whitespaceWhitespaceDuplicated.getContentAsTokens());
        assertEquals(expected, result);
    }

    @Test
    public void TestDedupLine() {
        FileTokenizer line = FileTokenizer.getTokenizer(TokenType.LINE);
        Submission result = preprocessor.process(lineWhitespaceDuplicated);
        Submission expected = new ConcreteSubmission(lineWhitespaceDuplicated.getName(), abcDupExpected, line.splitFile(abcDupExpected));
        assertEquals(expected, result);
    }
}
