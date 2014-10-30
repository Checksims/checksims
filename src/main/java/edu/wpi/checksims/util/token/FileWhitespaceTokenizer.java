package edu.wpi.checksims.util.token;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Split a file into tokens based on spaces
 */
public class FileWhitespaceTokenizer implements FileTokenizer {
    private static FileWhitespaceTokenizer instance;

    private FileWhitespaceTokenizer() {}

    public static FileWhitespaceTokenizer getInstance() {
        if(instance == null) {
            instance = new FileWhitespaceTokenizer();
        }

        return instance;
    }

    @Override
    public TokenList splitFile(List<String> strings) {
        TokenList toReturn = new TokenList(this.getType());

        for(String s : strings) {
            String[] split = s.split("\\s+");

            Arrays.stream(split)
                    .filter((str) -> !str.isEmpty())
                    .map((str) -> new WhitespaceToken(str))
                    .forEachOrdered((token) -> toReturn.add(token));
        }

        return toReturn;
    }

    @Override
    public TokenType getType() {
        return TokenType.WHITESPACE;
    }

    @Override
    public String toString() {
        return "Singleton instance of FileSpaceSplitter";
    }
}
