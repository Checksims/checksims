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

package edu.wpi.checksims.submission;

import edu.wpi.checksims.token.TokenList;
import edu.wpi.checksims.token.TokenType;
import edu.wpi.checksims.token.tokenizer.FileTokenizer;
import edu.wpi.checksims.util.file.FileLineReader;
import org.apache.commons.collections4.list.SetUniqueList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Interface for Submissions
 *
 * Also contains factory methods for submissions
 */
public interface Submission {
    /**
     * @return List of tokens forming the body of this submission
     */
    TokenList getContentAsTokens();

    /**
     * @return String consisting of the body of the submission
     */
    String getContentAsString();

    /**
     * @return Name of this submission
     */
    String getName();

    /**
     * @return Number of tokens in this submission's token list
     */
    int getNumTokens();

    /**
     * @return Type of token contained in this submission
     */
    TokenType getTokenType();

    /**
     * Generate a list of all student submissions from a directory
     *
     * The directory is assumed to hold a number of subdirectories, each containing one student or group's submission
     * The student/group directories may contain subdirectories with files
     *
     * @param directory Directory containing student submission directories
     * @param glob Match pattern used to identify files to include in submission
     * @param splitter Tokenizes files to produce Token Lists for a submission
     * @return Set of submissions including all unique nonempty submissions in the given directory
     * @throws java.io.IOException Thrown on error interacting with file or filesystem
     */
    public static List<Submission> submissionListFromDir(File directory, String glob, FileTokenizer splitter, boolean recursive) throws IOException {
        List<Submission> submissions = SetUniqueList.setUniqueList(new LinkedList<>());
        Logger local = LoggerFactory.getLogger(Submission.class);

        if(!directory.exists() || !directory.isDirectory()) {
            throw new IOException("Directory " + directory.getName() + " does not exist or is not a directory!");
        }

        // List all the subdirectories we find
        File[] contents = directory.listFiles(File::isDirectory);

        for(File f : contents) {
            Submission s = submissionFromDir(f, glob, splitter, recursive);

            if(s != null) {
                submissions.add(s);
                local.debug("Created submission with name " + s.getName());
            } else {
                local.warn("Could not create submission from directory " + f.getName() + " - no files matching pattern found!");
            }
        }

        return submissions;
    }

    /**
     * Get a single submission from a directory
     *
     * @param directory Directory containing the student's submission
     * @param glob Match pattern used to identify files to include in submission
     * @param splitter Tokenizes files to produce Token List in this submission
     * @return Single submission from all files matching the glob in given directory
     * @throws IOException Thrown on error interacting with file
     */
    public static Submission submissionFromDir(File directory, String glob, FileTokenizer splitter, boolean recursive) throws IOException {
        String dirName = directory.getName();

        if(!directory.exists() || !directory.isDirectory()) {
            throw new IOException("Directory " + dirName + " does not exist or is not a directory!");
        }

        // TODO consider verbose logging of which files we're adding to the submission?

        List<File> files = getAllMatchingFiles(directory, glob, recursive);

        return submissionFromFiles(dirName, files, splitter);
    }

    /**
     * Recursively find all files matching in a directory
     *
     * @param directory Directory to search in
     * @param glob Match pattern used to identify files to include
     * @return List of all matching files in this directory and subdirectories
     */
    static List<File> getAllMatchingFiles(File directory, String glob, boolean recursive) {
        List<File> allFiles = new LinkedList<>();
        Logger logs = LoggerFactory.getLogger(Submission.class);

        if(recursive) {
            logs.trace("Recursively traversing directory " + directory.getName());
        }

        // Add this directory
        Collections.addAll(allFiles, getMatchingFilesFromDir(directory, glob));

        // Get subdirectories
        File[] subdirs = directory.listFiles(File::isDirectory);

        // Recursively call on all subdirectories if specified
        if(recursive) {
            Arrays.stream(subdirs).forEach((f) -> allFiles.addAll(getAllMatchingFiles(f, glob, true)));
        }

        return allFiles;
    }

    /**
     * Identify all files matching in a single directory
     *
     * @param directory Directory to find files within
     * @param glob Match pattern used to identify files to include
     * @return Array of files which match in this single directory
     */
    static File[] getMatchingFilesFromDir(File directory, String glob) {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + glob);

        return directory.listFiles((f) -> matcher.matches(Paths.get(f.getAbsolutePath()).getFileName()));
    }

    /**
     * Turn a list of files and a name into a Submission
     *
     * @param name Name of the new submission
     * @param files List of files to include in submission
     * @param splitter Tokenizer for files in the submission
     * @return A new submission including a list containing a tokenization list consisting of the appended tokenization lists of every file included, or null if no files given
     * @throws IOException Thrown on error reading from file
     */
    public static Submission submissionFromFiles(String name, List<File> files, FileTokenizer splitter) throws IOException {
        Logger logs = LoggerFactory.getLogger(Submission.class);

        if(files.size() == 0) {
            return null;
        }

        TokenList tokenList = new TokenList(splitter.getType());

        StringBuilder fileContent = new StringBuilder();

        // Could do this with a .stream().forEach(...) but we'd have to handle the IOException inside
        for(File f : files) {
            fileContent.append(FileLineReader.readFile(f));
        }

        String contentString = fileContent.toString();

        // Split the content
        tokenList.addAll(splitter.splitFile(contentString));

        if(tokenList.size() > 7500) {
            logs.warn("Warning: Submission " + name + " has very large token count (" + tokenList.size() + ")");
        }

        return new ConcreteSubmission(name, contentString, tokenList);
    }
}
