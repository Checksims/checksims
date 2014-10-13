package edu.wpi.checksims;

import edu.wpi.checksims.util.Token;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

        for(int i = 0; i < strings.size(); i++) {
            String[] split = strings.get(i).split("\\s+");

            for(int j = 0; j < split.length; j++) {
                if(!split[j].isEmpty()) {
                    toReturn.add(new Token<>(split[j]));
                }
            }
        }

        return toReturn;
    }

    @Override
    public String toString() {
        return "Singleton instance of FileSpaceSplitter";
    }
}
