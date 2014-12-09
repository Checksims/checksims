package edu.wpi.checksims.algorithm;

import edu.wpi.checksims.ChecksimException;
import edu.wpi.checksims.submission.ConcreteSubmission;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.token.Token;
import edu.wpi.checksims.token.TokenList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Remove common code from submissions
 */
public class CommonCodeRemover {
    private static final Logger logs = LoggerFactory.getLogger(CommonCodeRemover.class);

    private CommonCodeRemover() {}

    public static List<Submission> removeCommonCodeFromSubmissionsInList(List<Submission> removeFrom, Submission common, SimilarityDetector algorithm) {
        if(removeFrom.isEmpty()) {
            return removeFrom;
        }

        AtomicInteger submissionsProcessed = new AtomicInteger(0);
        long startTime = System.currentTimeMillis();

        List<Submission> toReturn = removeFrom.stream().parallel().map((submission) -> {
            try {
                logs.info("Removing common code from submission " + submissionsProcessed.incrementAndGet() + "/" + removeFrom.size());

                return removeCommonCodeFromSubmission(submission, common, algorithm);
            } catch(ChecksimException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        logs.info("Common code removal took " + elapsedTime + "ms");

        return toReturn;
    }

    public static Submission removeCommonCodeFromSubmission(Submission in, Submission common, SimilarityDetector algorithm) throws ChecksimException {
        logs.trace("Performing common code removal on submission " + in.getName());

        AlgorithmResults results = algorithm.detectSimilarity(in, common);

        // The results contains two TokenLists, representing the final state of the submissions after similarity detection
        // All common code should be marked invalid for the input submission's final list
        TokenList listWithCommonInvalid = results.finalListA;

        // Construct a new list without the invalid tokens
        Supplier<TokenList> tokenListSupplier = () -> new TokenList(listWithCommonInvalid.type);
        TokenList finalList = listWithCommonInvalid.stream().filter(Token::isValid).collect(Collectors.toCollection(tokenListSupplier));

        DecimalFormat d = new DecimalFormat("###.00");
        logs.trace("Submission " + in.getName() + " contained " + d.format(100 * results.percentMatchedA()) + "% common code");

        return new ConcreteSubmission(in.getName(), finalList);
    }
}
