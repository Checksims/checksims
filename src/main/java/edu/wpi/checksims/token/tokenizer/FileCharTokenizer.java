package edu.wpi.checksims.token.tokenizer;

import edu.wpi.checksims.token.ConcreteToken;
import edu.wpi.checksims.token.TokenList;
import edu.wpi.checksims.token.TokenType;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Split a file into a list of character tokens.
 */
public class FileCharTokenizer implements FileTokenizer {
    private static FileCharTokenizer instance;

    private FileCharTokenizer() {}

    public static FileCharTokenizer getInstance() {
        if(instance == null) {
            instance = new FileCharTokenizer();
        }

        return instance;
    }

    @Override
    public TokenList splitFile(List<String> strings) {
        TokenList toReturn = new TokenList(this.getType());

        for(String s : strings) {
            char[] chars = s.toCharArray();

            Arrays.stream(ArrayUtils.toObject(chars))
                    .map((character) -> new ConcreteToken(character, TokenType.CHARACTER))
                    .forEachOrdered(toReturn::add);
        }

        return toReturn;
    }

    @Override
    public TokenType getType() {
        return TokenType.CHARACTER;
    }

    @Override
    public String toString() {
        return "Singleton instance of FileCharSplitter";
    }
}
