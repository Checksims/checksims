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

import com.google.common.collect.Ordering;
import edu.wpi.checksims.token.TokenList;
import edu.wpi.checksims.token.TokenType;
import edu.wpi.checksims.token.tokenizer.Tokenizer;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Interface for Submissions.
 *
 * Also contains factory methods for submissions
 */
public interface Submission extends Comparable<Submission> {
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
     * Generate a list of all student submissions from a directory.
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
    static Set<Submission> submissionListFromDir(File directory, String glob, Tokenizer splitter, boolean recursive)
            throws IOException {
        checkNotNull(directory);
        checkNotNull(glob);
        checkArgument(!glob.isEmpty(), "Glob pattern cannot be empty!");
        checkNotNull(splitter);

        Set<Submission> submissions = new HashSet<>();
        Logger local = LoggerFactory.getLogger(Submission.class);

        if(!directory.exists()) {
            throw new NoSuchFileException("Does not exist: " + directory.getAbsolutePath());
        } else if(!directory.isDirectory()) {
            throw new NotDirectoryException("Not a directory: " + directory.getAbsolutePath());
        }

        // List all the subdirectories we find
        File[] contents = directory.listFiles(File::isDirectory);

        for(File f : contents) {
            try {
                Submission s = submissionFromDir(f, glob, splitter, recursive);
                submissions.add(s);
                if(s.getContentAsString().isEmpty()) {
                    local.warn("Warning: Submission " + s.getName() + " is empty!");
                } else {
                    local.debug("Created submission with name " + s.getName());
                }
            } catch (NoMatchingFilesException e) {
                local.warn("Could not create submission from directory " + f.getName()
                        + " - no files matching pattern found!");
            }
        }

        return submissions;
    }

    /**
     * Get a single submission from a directory.
     *
     * @param directory Directory containing the student's submission
     * @param glob Match pattern used to identify files to include in submission
     * @param splitter Tokenizes files to produce Token List in this submission
     * @return Single submission from all files matching the glob in given directory
     * @throws IOException Thrown on error interacting with file
     */
    static Submission submissionFromDir(File directory, String glob, Tokenizer splitter, boolean recursive)
            throws IOException, NoMatchingFilesException {
        checkNotNull(directory);
        checkNotNull(glob);
        checkArgument(!glob.isEmpty(), "Glob pattern cannot be empty!");
        checkNotNull(splitter);

        if(!directory.exists()) {
            throw new NoSuchFileException("Does not exist: " + directory.getAbsolutePath());
        } else if(!directory.isDirectory()) {
            throw new NotDirectoryException("Not a directory: " + directory.getAbsolutePath());
        }

        // TODO consider verbose logging of which files we're adding to the submission?

        Set<File> files = getAllMatchingFiles(directory, glob, recursive);

        return submissionFromFiles(directory.getName(), files, splitter);
    }

    /**
     * Recursively find all files matching in a directory.
     *
     * @param directory Directory to search in
     * @param glob Match pattern used to identify files to include
     * @return List of all matching files in this directory and subdirectories
     */
    static Set<File> getAllMatchingFiles(File directory, String glob, boolean recursive)
            throws NoSuchFileException, NotDirectoryException {
        checkNotNull(directory);
        checkNotNull(glob);
        checkArgument(!glob.isEmpty(), "Glob pattern cannot be empty");

        Set<File> allFiles = new HashSet<>();
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
            for(File subdir : subdirs) {
                allFiles.addAll(getAllMatchingFiles(subdir, glob, true));
            }
        }

        return allFiles;
    }

    /**
     * Identify all files matching in a single directory.
     *
     * @param directory Directory to find files within
     * @param glob Match pattern used to identify files to include
     * @return Array of files which match in this single directory
     */
    static File[] getMatchingFilesFromDir(File directory, String glob)
            throws NoSuchFileException,NotDirectoryException {
        checkNotNull(directory);
        checkNotNull(glob);
        checkArgument(!glob.isEmpty(), "Glob pattern cannot be empty");

        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + glob);

        if(!directory.exists()) {
            throw new NoSuchFileException("Does not exist: " + directory.getAbsolutePath());
        } else if(!directory.isDirectory()) {
            throw new NotDirectoryException("Not a directory: " + directory.getAbsolutePath());
        }

        return directory.listFiles((f) -> matcher.matches(Paths.get(f.getAbsolutePath()).getFileName()));
    }

    /**
     * Turn a list of files and a name into a Submission.
     *
     * The contents of a submission are built deterministically by reading in files in alphabetical order and appending
     * their contents.
     *
     * @param name Name of the new submission
     * @param files List of files to include in submission
     * @param splitter Tokenizer for files in the submission
     * @return A new submission formed from the contents of all given files, appended and tokenized
     * @throws IOException Thrown on error reading from file
     * @throws NoMatchingFilesException Thrown if no files are given
     */
    static Submission submissionFromFiles(String name, Set<File> files, Tokenizer splitter)
            throws IOException, NoMatchingFilesException {
        checkNotNull(name);
        checkArgument(!name.isEmpty(), "Submission name cannot be empty");
        checkNotNull(files);
        checkNotNull(splitter);

        Logger logs = LoggerFactory.getLogger(Submission.class);

        if(files.size() == 0) {
            throw new NoMatchingFilesException("No matching files found, cannot create submission!");
        }

        // To ensure submission generation is deterministic, sort files by name, and read them in that order
        List<File> orderedFiles = Ordering.from((File file1, File file2) -> file1.getName().compareTo(file2.getName()))
                .immutableSortedCopy(files);

        TokenList tokenList = new TokenList(splitter.getType());

        StringBuilder fileContent = new StringBuilder();

        // Could do this with a .stream().forEach(...) but we'd have to handle the IOException inside
        for(File f : orderedFiles) {
            String content = FileUtils.readFileToString(f, StandardCharsets.UTF_8);

            fileContent.append(content);

            if(!content.endsWith("\n") && !content.isEmpty()) {
                fileContent.append("\n");
            }
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
