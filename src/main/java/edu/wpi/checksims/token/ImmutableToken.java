package edu.wpi.checksims.token;

import org.apache.commons.lang.NotImplementedException;

/**
 * An immutable view of a token
 */
public class ImmutableToken extends AbstractTokenDecorator {
    public ImmutableToken(Token wrappedToken) {
        super(wrappedToken);
    }

    @Override
    public void setValid(boolean isValid) {
        throw new NotImplementedException("Cannot modify immutable token!");
    }
}
