/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright (c) 2014-2015 Matthew Heon and Dolan Murvihill
 */

package edu.wpi.checksims.algorithm.linesimilarity;

import edu.wpi.checksims.ChecksimException;
import edu.wpi.checksims.algorithm.AlgorithmResults;
import edu.wpi.checksims.algorithm.SimilarityDetector;
import edu.wpi.checksims.submission.Submission;
import edu.wpi.checksims.token.Token;
import edu.wpi.checksims.token.TokenList;
import edu.wpi.checksims.token.TokenType;
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
public class LineSimilarityChecker implements SimilarityDetector {
    private static LineSimilarityChecker instance;

    /**
     * Internal class for record-keeping - used to record a line at a specific location in a submission
     */
    class SubmissionLine {
        public final int lineNum;
        public final Submission submission;

        SubmissionLine(int lineNum, Submission submission) {
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

    @Override
    public String getName() {
        return "linecompare";
    }

    @Override
    public TokenType getDefaultTokenType() {
        return TokenType.LINE;
    }

    /**
     * Detect plagiarism using line similarity comparator
     *
     * @param a First submission to check
     * @param b Second submission to check
     * @return Number of identical lines in both submissions
     */
    @Override
    public AlgorithmResults detectSimilarity(Submission a, Submission b) throws ChecksimException {
        TokenList linesA = a.getContentAsTokens();
        TokenList linesB = b.getContentAsTokens();
        TokenList finalA = TokenList.cloneTokenList(linesA);
        TokenList finalB = TokenList.cloneTokenList(linesB);

        if(!a.getTokenType().equals(b.getTokenType())) {
            throw new ChecksimException("Token list type mismatch: submission " + a.getName() + " has type " +
                    linesA.type.toString() + ", while submission " + b.getName() + " has type " + linesB.type.toString());
        } else if(a.equals(b)) {
            finalA.stream().forEach((token) -> token.setValid(false));
            finalB.stream().forEach((token) -> token.setValid(false));
            return new AlgorithmResults(a, b, a.getNumTokens(), b.getNumTokens(), finalA, finalB);
        }

        MessageDigest hasher;

        // Get a hashing instance
        try {
            hasher = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new ChecksimException("Error instantiating SHA-512 hash algorithm: " + e.getMessage());
        }

        // Create a line database map
        // Per-method basis to ensure we have no mutable state in the class
        Map<String, List<SubmissionLine>> lineDatabase = new HashMap<>();

        // Hash all lines in A, and put them in the lines database
        addLinesToMap(linesA, lineDatabase, a, hasher);

        // Hash all lines in B, and put them in the lines database
        addLinesToMap(linesB, lineDatabase, b, hasher);

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

                // Set matches invalid
                for(SubmissionLine s : lineDatabase.get(key)) {
                    if(s.submission.equals(a)) {
                        finalA.get(s.lineNum).setValid(false);
                    } else if(s.submission.equals(b)) {
                        finalB.get(s.lineNum).setValid(false);
                    } else {
                        throw new RuntimeException("Unreachable code!");
                    }
                }

                identicalLinesA += numLinesA;
                identicalLinesB += numLinesB;
            }
        }

        return new AlgorithmResults(a, b, identicalLinesA, identicalLinesB, finalA, finalB);
    }

    void addLinesToMap(TokenList lines, Map<String, List<SubmissionLine>> lineDatabase, Submission submitter, MessageDigest hasher) {
        for(int i = 0; i < lines.size(); i++) {
            Token token = lines.get(i);

            String hash = Hex.encodeHexString(hasher.digest(token.getTokenAsString().getBytes()));

            if(lineDatabase.get(hash) == null) {
                lineDatabase.put(hash, new LinkedList<>());
            }

            SubmissionLine line = new SubmissionLine(i, submitter);
            lineDatabase.get(hash).add(line);
        }
    }

    @Override
    public String toString() {
        return "Sole instance of the Line Similarity Counter algorithm";
    }
}
