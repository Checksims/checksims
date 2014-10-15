package edu.wpi.checksims.util.file;

import edu.wpi.checksims.util.Token;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Split a file into a list of character tokens.
 */
public class FileCharSplitter implements FileSplitter<Character> {
    private static FileCharSplitter instance;

    private FileCharSplitter() {}

    public static FileCharSplitter getInstance() {
        if(instance == null) {
            instance = new FileCharSplitter();
        }

        return instance;
    }

    @Override
    public List<Token<Character>> splitFile(List<String> strings) {
        List<Token<Character>> toReturn = new LinkedList<>();

        for(String s : strings) {
            char[] chars = s.toCharArray();

            Arrays.stream(ArrayUtils.toObject(chars))
                    .map((c) -> new Token<>(c))
                    .forEachOrdered((token) -> toReturn.add(token));
        }

        return toReturn;
    }

    @Override
    public String toString() {
        return "Singleton instance of FileCharSplitter";
    }
}
