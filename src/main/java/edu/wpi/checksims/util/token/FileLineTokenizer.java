package edu.wpi.checksims.util.token;

import java.util.List;

/**
 * Splits a file on a line-by-line basis
 */
public class FileLineTokenizer implements FileTokenizer {
    private static FileLineTokenizer instance;

    private FileLineTokenizer() {}

    public static FileLineTokenizer getInstance() {
        if(instance == null) {
            instance = new FileLineTokenizer();
        }

        return instance;
    }

    @Override
    public TokenList splitFile(List<String> strings) {
        TokenList toReturn = new TokenList(this.getType());

        strings.stream()
                .map(LineToken::new)
                .forEachOrdered(toReturn::add);

        return toReturn;
    }

    @Override
    public TokenType getType() {
        return TokenType.LINE;
    }

    @Override
    public String toString() {
        return "Singleton FileLineSplitter instance";
    }
}
