package edu.wpi.checksims.submission;

import edu.wpi.checksims.token.TokenList;
import edu.wpi.checksims.token.TokenType;

public final class ConcreteSubmission implements Submission {
    private final TokenList tokenList;
    private final String name;

    public ConcreteSubmission(String name, TokenList tokens) {
        this.name = name;
        this.tokenList = TokenList.immutableCopy(tokens);
    }

    @Override
    public TokenList getTokenList() {
        return tokenList;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getNumTokens() {
        return tokenList.size();
    }

    @Override
    public TokenType getTokenType() {
        return tokenList.type;
    }

    @Override
    public String toString() {
        return "A submission with name " + name + " and " + getNumTokens() + " tokens";
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof ConcreteSubmission)) {
            return false;
        }

        Submission otherSubmission = (Submission)other;

        return otherSubmission.getName().equals(this.name) && otherSubmission.getNumTokens() == this.getNumTokens() && otherSubmission.getTokenList().equals(this.tokenList);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
