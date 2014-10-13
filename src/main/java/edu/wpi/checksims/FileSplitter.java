package edu.wpi.checksims;

import edu.wpi.checksims.util.Token;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Interface to read in a file and return it as a list of tokens of a certain type
 */
public interface FileSplitter<T extends Comparable> {
    public List<Token<T>> splitFile(List<String> strings);
}
