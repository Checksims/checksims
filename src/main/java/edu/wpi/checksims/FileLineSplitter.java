package edu.wpi.checksims;

import edu.wpi.checksims.util.Token;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

        for(int i = 0; i < strings.size(); i++) {
            toReturn.add(new Token<>(strings.get(i)));
        }

        return toReturn;
    }

    @Override
    public String toString() {
        return "Singleton FileLineSplitter instance";
    }
}
