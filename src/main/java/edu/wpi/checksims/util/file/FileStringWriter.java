package edu.wpi.checksims.util.file;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Write string to file
 */
public class FileStringWriter {
    private FileStringWriter() {}

    public static void writeStringToFile(File f, String str) throws IOException {
        PrintWriter writer = new PrintWriter(f.getAbsolutePath() + f.getName());

        writer.println(str);

        writer.close();
    }
}
