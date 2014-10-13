package edu.wpi.checksims;

import edu.wpi.checksims.util.Token;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

        for(int i = 0; i < strings.size(); i++) {
            char[] chars = strings.get(i).toCharArray();

            for(int j = 0; j < chars.length; j++) {
                toReturn.add(new Token<>(chars[j]));
            }
        }

        return toReturn;
    }

    @Override
    public String toString() {
        return "Singleton instance of FileCharSplitter";
    }
}
