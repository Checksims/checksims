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
    public List<Token<Character>> splitFile(File f) throws IOException {
        if(!f.exists() || !f.isFile()) {
            throw new IOException("File " + f.getName() + " does not exist or is not a file!");
        }

        List<Token<Character>> tokens = new LinkedList<>();

        BufferedReader b = new BufferedReader(new FileReader(f));

        String lineRead = b.readLine();

        while(lineRead != null) {
            if(!lineRead.isEmpty()) {
                // Split into characters
                char[] chars = lineRead.toCharArray();

                for(char c : chars) {
                    tokens.add(new Token<>(c));
                }
            }

            lineRead = b.readLine();
        }

        b.close();

        return tokens;
    }

    @Override
    public String toString() {
        return "Singleton instance of FileCharSplitter";
    }
}
