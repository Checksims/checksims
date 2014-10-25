package edu.wpi.checksims.algorithm.preprocessor;

import edu.wpi.checksims.Submission;
import edu.wpi.checksims.util.Token;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Lowercases characters in a Submission<Character>
 */
public class CharacterLowercasePreprocessor implements SubmissionPreprocessor<Character> {
    private static CharacterLowercasePreprocessor instance;

    private CharacterLowercasePreprocessor() {}

    public static CharacterLowercasePreprocessor getInstance() {
        if(instance == null) {
            instance = new CharacterLowercasePreprocessor();
        }

        return instance;
    }


    @Override
    public Submission<Character> process(Submission<Character> submission) {
        Supplier<List<Token<Character>>> listSupplier = LinkedList::new;

        List<Token<Character>> newTokens = submission.getTokenList().stream().map((token) -> new Token<>(Character.toLowerCase(token.getToken()))).collect(Collectors.toCollection(listSupplier));

        return new Submission<>(submission.getName(), newTokens);
    }

    @Override
    public String toString() {
        return "Singleton instance of CharacterLowercasePreprocessor";
    }
}
