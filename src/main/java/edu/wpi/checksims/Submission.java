package edu.wpi.checksims;

import edu.wpi.checksims.util.Token;
import edu.wpi.checksims.util.TokenListCloner;
import edu.wpi.checksims.util.file.FileLineReader;
import edu.wpi.checksims.util.file.FileSplitter;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Submission<T extends Comparable<T>> {
    private final List<Token<T>> tokenList;
    private final String name;

    public Submission(String name, List<Token<T>> tokens) {
        this.name = name;
        this.tokenList = tokens;
    }

    public List<Token<T>> getTokenList() {
        return TokenListCloner.cloneList(tokenList);
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

    // TODO should compare token lists as well
    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Submission)) {
            return false;
        }

        Submission<T> otherSubmission = (Submission<T>)other;

        return otherSubmission.getName().equals(this.name) && otherSubmission.getNumTokens() == this.getNumTokens();
    }

    // TODO once we have a proper equals and HashCode convert this to return Set<Submission>
    public static <T2 extends Comparable<T2>> List<Submission<T2>> submissionsFromDir(File directory, String glob, FileSplitter<T2> splitter) throws IOException {
        List<Submission<T2>> submissions = new LinkedList<>();

        if(!directory.exists() || !directory.isDirectory()) {
            throw new IOException("Directory " + directory.getName() + " does not exist or is not a directory!");
        }

        // List all the subdirectories we find
        File[] contents = directory.listFiles(File::isDirectory);

        for(File f : contents) {
            Submission<T2> s = submissionFromDir(f, glob, splitter);

            if(s != null) {
                submissions.add(s);
            }
        }

        return submissions;
    }

    public static <T2 extends Comparable<T2>> Submission<T2> submissionFromDir(File directory, String glob, FileSplitter<T2> splitter) throws IOException {
        List<File> files = new LinkedList<>();
        String dirName = directory.getName();

        if(!directory.exists() || !directory.isDirectory()) {
            throw new IOException("Directory " + dirName + " does not exist or is not a directory!");
        }

        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + glob);

        File[] contents = directory.listFiles((f) -> matcher.matches(Paths.get(f.getAbsolutePath()).getFileName()));

        // TODO consider verbose logging of which files we're adding to the submission?

        Collections.addAll(files, contents);

        return submissionFromFiles(dirName, files, splitter);
    }

    public static <T2 extends Comparable<T2>> Submission<T2> submissionFromFiles(String name, List<File> files, FileSplitter<T2> splitter) throws IOException {
        if(files.size() == 0) {
            return null;
        }

        List<Token<T2>> tokenList = new LinkedList<>();

        for(File f : files) {
            tokenList.addAll(splitter.splitFile(FileLineReader.readFile(f)));
        }

        return new Submission<>(name, tokenList);
    }
}
