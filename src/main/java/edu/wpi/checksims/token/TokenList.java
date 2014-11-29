package edu.wpi.checksims.token;

import com.google.common.collect.ImmutableList;
import org.apache.commons.collections4.list.PredicatedList;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A list of tokens of a specific type
 */
public class TokenList extends PredicatedList<Token> {
    public final TokenType type;

    private static final long serialVersionUID = 1L;

    public TokenList(TokenType type) {
        super(new LinkedList<>(), (token) -> token.getType().equals(type));
        this.type = type;
    }

    private TokenList(TokenType type, List<Token> baseList) {
        super(baseList, (token) -> token.getType().equals(type));
        this.type = type;
    }

    /**
     * Peforms a shallow copy of a TokenList, returning an immutable version of the initial list
     *
     * @param cloneFrom List to copy
     * @return Immutable copy of cloneFrom
     */
    public static TokenList immutableCopy(TokenList cloneFrom) {
        return new TokenList(cloneFrom.type, ImmutableList.copyOf(cloneFrom));
    }

    /**
     * Perform a deep copy of a TokenList
     *
     * @param cloneFrom List to deep copy
     * @return Cloned copy of the tokenization list
     */
    public static TokenList cloneTokenList(TokenList cloneFrom) {
        Supplier<TokenList> tokenListSupplier = () -> new TokenList(cloneFrom.type);

        return cloneFrom.stream().map(Token::cloneToken).collect(Collectors.toCollection(tokenListSupplier));
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof TokenList)) {
            return false;
        }

        TokenList otherList = (TokenList)other;

        return otherList.type.equals(this.type) && super.equals(otherList);
    }

    @Override
    public String toString() {
        return "Token list of type " + type.toString() + " containing " + super.toString();
    }

    @Override
    public int hashCode() {
        return type.hashCode() ^ super.hashCode();
    }
}
