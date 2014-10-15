package edu.wpi.checksims.util.file;

import edu.wpi.checksims.util.Token;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Split a file into tokens based on spaces
 */
public class FileWhitespaceSplitter implements FileSplitter<String> {
    private static FileWhitespaceSplitter instance;

    private FileWhitespaceSplitter() {}

    public static FileWhitespaceSplitter getInstance() {
        if(instance == null) {
            instance = new FileWhitespaceSplitter();
        }

        return instance;
    }

    @Override
    public List<Token<String>> splitFile(List<String> strings) {
        List<Token<String>> toReturn = new LinkedList<>();

        for(String s : strings) {
            String[] split = s.split("\\s+");

            Arrays.stream(split)
                    .filter((str) -> !str.isEmpty())
                    .map((str) -> new Token<>(str))
                    .forEachOrdered((token) -> toReturn.add(token));
        }

        return toReturn;
    }

    @Override
    public String toString() {
        return "Singleton instance of FileSpaceSplitter";
    }
}
