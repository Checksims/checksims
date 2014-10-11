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
    public List<Token<String>> splitFile(File f) throws IOException {
        if(!f.exists() || !f.isFile()) {
            throw new IOException("File " + f.getName() + " does not exist or is not a file!");
        }

        List<Token<String>> tokens = new LinkedList<>();

        BufferedReader b = new BufferedReader(new FileReader(f));

        String lineRead = b.readLine();

        while(lineRead != null) {
            if(!lineRead.isEmpty()) {
                // Split based on any number of whitespace characters
                String[] splitBySpace = lineRead.split("\\s+");

                for(String s : splitBySpace) {
                    tokens.add(new Token<>(s));
                }
            }

            lineRead = b.readLine();
        }

        b.close();

        return tokens;
    }

    @Override
    public String toString() {
        return "Singleton instance of FileSpaceSplitter";
    }
}
