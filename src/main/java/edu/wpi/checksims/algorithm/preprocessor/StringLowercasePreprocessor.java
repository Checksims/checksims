package edu.wpi.checksims.algorithm.preprocessor;

import edu.wpi.checksims.Submission;
import edu.wpi.checksims.util.Token;

import java.util.LinkedList;
import java.util.List;

/**
 * Lowercases strings in a Submission<String>
 */
public class StringLowercasePreprocessor implements SubmissionPreprocessor<String> {
    private static StringLowercasePreprocessor instance;

    private StringLowercasePreprocessor() {}

    public static StringLowercasePreprocessor getInstance() {
        if(instance == null) {
            instance = new StringLowercasePreprocessor();
        }

        return instance;
    }


    /**
     * Lowercase every token in the token list of a Submission<String>, and return a new submission with the same name and new token list
     *
     * @param submission Submission to process
     * @return Submission identical to original one, save all tokens have been lowercased.
     */
    @Override
    public Submission<String> process(Submission<String> submission) {
        List<Token<String>> newTokens = new LinkedList<>();

        submission.getTokenList().stream().forEachOrdered((token) -> newTokens.add(new Token<>(token.getToken().toLowerCase())));

        return new Submission<>(submission.getName(), newTokens);
    }

    @Override
    public String toString() {
        return "Singleton instance of StringLowercasePreprocessor";
    }
}
