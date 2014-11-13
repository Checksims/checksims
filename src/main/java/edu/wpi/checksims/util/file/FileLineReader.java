package edu.wpi.checksims.util.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Read a file into a list of strings
 */
public class FileLineReader {
    private FileLineReader() {}

    public static List<String> readFile(File f) throws IOException {
        if(!f.exists() || !f.isFile()) {
            throw new IOException("File " + f.getName() + " does not exist or is not a file!");
        }

        List<String> lines = new LinkedList<>();

        BufferedReader b = new BufferedReader(new FileReader(f));

        String lineRead = b.readLine();

        while(lineRead != null) {
            if(!lineRead.isEmpty()) {
                lines.add(lineRead);
            }

            lineRead = b.readLine();
        }

        b.close();

        return lines;
    }
}
