package edu.wpi.checksims.util.token;

import java.util.List;

/**
 * Interface to read in a file and return it as a list of tokens of a certain type
 */
public interface FileTokenizer {
    public TokenList splitFile(List<String> strings);

    public TokenType getType();

    public static FileTokenizer getTokenizer(TokenType type) {
        switch(type) {
            case CHARACTER:
                return FileCharTokenizer.getInstance();
            case LINE:
                return FileLineTokenizer.getInstance();
            case WHITESPACE:
                return FileWhitespaceTokenizer.getInstance();
            default:
                // TODO handle more gracefully
                throw new RuntimeException("Unhandled tokenization requested!");
        }
    }
}
