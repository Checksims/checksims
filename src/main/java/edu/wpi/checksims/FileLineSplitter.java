package edu.wpi.checksims;

import edu.wpi.checksims.util.Token;

import java.util.LinkedList;
import java.util.List;

/**
 * Splits a file on a line-by-line basis
 */
public class FileLineSplitter implements FileSplitter<String> {
    private static FileLineSplitter instance;

    private FileLineSplitter() {}

    public static FileLineSplitter getInstance() {
        if(instance == null) {
            instance = new FileLineSplitter();
        }

        return instance;
    }

    @Override
    public List<Token<String>> splitFile(List<String> strings) {
        List<Token<String>> toReturn = new LinkedList<>();

        strings.stream()
                .map((str) -> new Token<>(str))
                .forEachOrdered((token) -> toReturn.add(token));

        return toReturn;
    }

    @Override
    public String toString() {
        return "Singleton FileLineSplitter instance";
    }
}
