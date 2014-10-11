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
    public List<Token<String>> splitFile(File f) throws IOException {
        if(!f.exists() || !f.isFile()) {
            throw new IOException("File " + f.getName() + " does not exist or is not a file!");
        }

        List<Token<String>> lines = new LinkedList<>();

        BufferedReader b = new BufferedReader(new FileReader(f));

        String lineRead = b.readLine();

        while(lineRead != null) {
            if(!lineRead.isEmpty()) {
                lines.add(new Token<>(lineRead));
            }

            lineRead = b.readLine();
        }

        b.close();

        return lines;
    }

    @Override
    public String toString() {
        return "Singleton FileLineSplitter instance";
    }
}
