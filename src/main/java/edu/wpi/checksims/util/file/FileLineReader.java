/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright (c) 2014 Matthew Heon and Dolan Murvihill
 */

package edu.wpi.checksims.util.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        Logger logs = LoggerFactory.getLogger(FileLineReader.class);

        if(!f.exists() || !f.isFile()) {
            throw new IOException("File " + f.getName() + " does not exist or is not a file!");
        }

        logs.trace("Reading file " + f.getPath());

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
