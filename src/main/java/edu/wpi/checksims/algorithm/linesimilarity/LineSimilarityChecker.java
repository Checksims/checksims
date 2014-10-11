package edu.wpi.checksims.algorithm.linesimilarity;

import edu.wpi.checksims.ChecksimException;
import edu.wpi.checksims.Submission;
import edu.wpi.checksims.algorithm.AlgorithmResults;
import edu.wpi.checksims.algorithm.PlagiarismDetector;
import edu.wpi.checksims.util.Token;
import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Implements a line-by-line similarity checker
 */
public class LineSimilarityChecker implements PlagiarismDetector<String> {
    private static LineSimilarityChecker instance;

    class SubmissionLine {
        public final int lineNum;
        public final Submission<String> submission;

        SubmissionLine(int lineNum, Submission<String> submission) {
            this.lineNum = lineNum;
            this.submission = submission;
        }

        @Override
        public String toString() {
            return "Line " + lineNum + " from submission with name " + submission.getName();
        }
    }

    private LineSimilarityChecker() {}

    // Singleton
    public static LineSimilarityChecker getInstance() {
        if(instance == null) {
            instance = new LineSimilarityChecker();
        }

        return instance;
    }

    /**
     * Detect plagiarism using line similarity comparator
     *
     * @param a First submission to check
     * @param b Second submission to check
     * @return Number of identical lines in both submissions
     */
    @Override
    public AlgorithmResults<String> detectPlagiarism(Submission<String> a, Submission<String> b) throws ChecksimException {
        List<Token<String>> linesA = a.getTokenList();
        List<Token<String>> linesB = b.getTokenList();

        MessageDigest hasher;

        // Get a hashing instance
        try {
            hasher = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new ChecksimException("Error instantiating SHA-512 hash algorithm, your JVM may not support it!");
        }

        // Create a line database map
        // Per-method basis to ensure we have no mutable state in the class
        Map<String, List<SubmissionLine>> lineDatabase = new HashMap<>();

        // Hash all lines in A, and put them in the lines database
        for(int i = 0; i < linesA.size(); i++) {
            Token<String> token = linesA.get(i);

            String hash = Hex.encodeHexString(hasher.digest(token.getToken().getBytes()));

            SubmissionLine line = new SubmissionLine(i, a);

            if(lineDatabase.get(hash) == null) {
                lineDatabase.put(hash, new LinkedList<>());
            }

            lineDatabase.get(hash).add(line);
        }

        // Hash all lines in B, and put them in the lines database
        for(int j = 0; j < linesB.size(); j++) {
            Token<String> token = linesB.get(j);

            String hash = Hex.encodeHexString(hasher.digest(token.getToken().getBytes()));

            SubmissionLine line = new SubmissionLine(j, a);

            if(lineDatabase.get(hash) == null) {
                lineDatabase.put(hash, new LinkedList<>());
            }

            lineDatabase.get(hash).add(line);
        }

        // Number of matched lines contained in both
        int identicalLinesA = 0;
        int identicalLinesB = 0;

        // Check all the keys
        for(String key : lineDatabase.keySet()) {
            // If more than 1 line has the hash...
            if(lineDatabase.get(key).size() != 1) {
                int numLinesA = 0;
                int numLinesB = 0;

                // Count the number of that line in each submission
                for(SubmissionLine s : lineDatabase.get(key)) {
                    if(s.submission.equals(a)) {
                        numLinesA++;
                    } else if(s.submission.equals(b)) {
                        numLinesB++;
                    } else {
                        throw new RuntimeException("Unreachable code!");
                    }
                }

                if(numLinesA == 0 || numLinesB == 0) {
                    // Only one of the submissions includes the line - no plagiarism here
                    continue;
                }

                identicalLinesA += numLinesA;
                identicalLinesB += numLinesB;
            }
        }

        return new AlgorithmResults<>(a, b, identicalLinesA, identicalLinesB);
    }

    @Override
    public String toString() {
        return "Sole instance of the Line Similarity Counter algorithm";
    }
}
