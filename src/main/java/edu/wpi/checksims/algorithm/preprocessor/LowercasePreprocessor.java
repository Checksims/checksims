package edu.wpi.checksims.algorithm.preprocessor;

import edu.wpi.checksims.Submission;
import edu.wpi.checksims.util.token.TokenList;

import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Lowercases tokens to prevent case from interfering with comparisons
 */
public class LowercasePreprocessor implements SubmissionPreprocessor {
    private static LowercasePreprocessor instance;

    private LowercasePreprocessor() {}

    public static LowercasePreprocessor getInstance() {
        if(instance == null) {
            instance = new LowercasePreprocessor();
        }

        return instance;
    }

    @Override
    public Submission process(Submission submission) {
        Supplier<TokenList> tokenListSupplier = () -> new TokenList(submission.getTokenList().type);

        return new Submission(submission.getName(), submission.getTokenList().stream().map((token) -> token.lowerCase()).collect(Collectors.toCollection(tokenListSupplier)));
    }
}
