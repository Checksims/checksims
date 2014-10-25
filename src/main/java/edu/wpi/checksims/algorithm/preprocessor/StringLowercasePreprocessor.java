package edu.wpi.checksims.algorithm.preprocessor;

import edu.wpi.checksims.Submission;
import edu.wpi.checksims.util.Token;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
        Supplier<List<Token<String>>> linkedListSupplier = LinkedList::new;

        List<Token<String>> newTokens = submission.getTokenList().stream().map((token) -> new Token<>(token.getToken().toLowerCase())).collect(Collectors.toCollection(linkedListSupplier));

        return new Submission<>(submission.getName(), newTokens);
    }

    @Override
    public String toString() {
        return "Singleton instance of StringLowercasePreprocessor";
    }
}
