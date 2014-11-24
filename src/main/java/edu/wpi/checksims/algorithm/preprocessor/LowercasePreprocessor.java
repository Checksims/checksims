package edu.wpi.checksims.algorithm.preprocessor;

import edu.wpi.checksims.submission.ConcreteSubmission;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.token.Token;
import edu.wpi.checksims.token.TokenList;

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
    public String getName() {
        return "lowercase";
    }

    @Override
    public Submission process(Submission submission) {
        Supplier<TokenList> tokenListSupplier = () -> new TokenList(submission.getTokenList().type);

        return new ConcreteSubmission(submission.getName(), submission.getTokenList().stream().map(Token::lowerCase).collect(Collectors.toCollection(tokenListSupplier)));
    }
}
