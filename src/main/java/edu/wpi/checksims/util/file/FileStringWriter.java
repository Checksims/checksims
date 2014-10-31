package edu.wpi.checksims.util.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Write string to file
 */
public class FileStringWriter {
    private FileStringWriter() {}

    public static void writeStringToFile(File f, String str) throws IOException {
        PrintWriter writer = new PrintWriter(f.getAbsolutePath());

        Logger logs = LoggerFactory.getLogger(FileStringWriter.class);
        logs.info("Writing output to file " + f.getName());

        writer.println(str);

        writer.close();
    }
}
