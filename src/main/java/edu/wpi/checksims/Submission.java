package edu.wpi.checksims;

import edu.wpi.checksims.util.token.Token;
import edu.wpi.checksims.util.token.TokenList;
import edu.wpi.checksims.util.file.FileLineReader;
import edu.wpi.checksims.util.token.FileTokenizer;
import org.apache.commons.collections4.list.SetUniqueList;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class Submission {
    private final TokenList tokenList;
    private final String name;

    public Submission(String name, TokenList tokens) {
        this.name = name;
        this.tokenList = TokenList.immutableCopy(tokens);
    }

    public TokenList getTokenList() {
        return tokenList;
    }

    public String getName() {
        return name;
    }

    public int getNumTokens() {
        return tokenList.size();
    }

    @Override
    public String toString() {
        return "A submission with name " + name + " and " + getNumTokens() + " tokens";
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Submission)) {
            return false;
        }

        Submission otherSubmission = (Submission)other;

        return otherSubmission.getName().equals(this.name) && otherSubmission.getNumTokens() == this.getNumTokens() && otherSubmission.getTokenList().equals(this.tokenList);
    }

    @Override
    public int hashCode() {
        int hash = name.hashCode();

        return tokenList.stream().mapToInt(Token::hashCode).reduce(hash, (x,y) -> (x + y));
    }

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
     * @throws IOException Thrown on error interacting with file or filesystem
     */
    public static List<Submission> submissionsFromDir(File directory, String glob, FileTokenizer splitter) throws IOException {
        List<Submission> submissions = SetUniqueList.setUniqueList(new LinkedList<>());

        if(!directory.exists() || !directory.isDirectory()) {
            throw new IOException("Directory " + directory.getName() + " does not exist or is not a directory!");
        }

        // List all the subdirectories we find
        File[] contents = directory.listFiles(File::isDirectory);

        for(File f : contents) {
            Submission s = submissionFromDir(f, glob, splitter);

            if(s != null) {
                submissions.add(s);
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
    public static Submission submissionFromDir(File directory, String glob, FileTokenizer splitter) throws IOException {
        String dirName = directory.getName();

        if(!directory.exists() || !directory.isDirectory()) {
            throw new IOException("Directory " + dirName + " does not exist or is not a directory!");
        }

        // TODO consider verbose logging of which files we're adding to the submission?

        List<File> files = getAllMatchingFiles(directory, glob);

        return submissionFromFiles(dirName, files, splitter);
    }

    /**
     * Recursively find all files matching in a directory
     *
     * @param directory Directory to search in
     * @param glob Match pattern used to identify files to include
     * @return List of all matching files in this directory and subdirectories
     */
    private static List<File> getAllMatchingFiles(File directory, String glob) {
        List<File> allFiles = new LinkedList<>();

        // Add this directory
        Collections.addAll(allFiles, getMatchingFilesFromDir(directory, glob));

        // Get subdirectories
        File[] subdirs = directory.listFiles(File::isDirectory);

        // Recursively call on all subdirectories
        Arrays.stream(subdirs).forEach((f) -> allFiles.addAll(getAllMatchingFiles(f, glob)));

        return allFiles;
    }

    /**
     * Identify all files matching in a single directory
     *
     * @param directory Directory to find files within
     * @param glob Match pattern used to identify files to include
     * @return Array of files which match in this single directory
     */
    private static File[] getMatchingFilesFromDir(File directory, String glob) {
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
        if(files.size() == 0) {
            return null;
        }

        TokenList tokenList = new TokenList(splitter.getType());

        // Could do this with a .stream().forEach(...) but we'd have to handle the IOException inside
        for(File f : files) {
            tokenList.addAll(splitter.splitFile(FileLineReader.readFile(f)));
        }

        return new Submission(name, tokenList);
    }
}
