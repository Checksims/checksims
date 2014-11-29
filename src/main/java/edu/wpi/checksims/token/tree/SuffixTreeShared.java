package edu.wpi.checksims.token.tree;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import edu.wpi.checksims.ChecksimException;
import edu.wpi.checksims.token.ConcreteToken;
import edu.wpi.checksims.token.Token;
import edu.wpi.checksims.token.TokenType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Shared code relating to children in a suffix tree
 */
public abstract class SuffixTreeShared {
    private final Map<Token, SuffixTreeNode> children;

    public SuffixTreeShared() {
        children = new HashMap<>();
    }

    public abstract TokenType getTokenType();

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public boolean hasChild(Token t) {
        return children.containsKey(t);
    }

    public SuffixTreeNode getChild(Token t) {
        return children.get(t);
    }

    public Map<Token, SuffixTreeNode> getChildren() {
        return ImmutableMap.copyOf(children);
    }

    public List<SuffixTreeNode> getChildrenAsList() {
        return ImmutableList.copyOf(children.values());
    }

    public SuffixTreeNode addChild(ConcreteToken t) throws ChecksimException {
        if(!t.getType().equals(getTokenType())) {
            throw new ChecksimException("Token type mismatch adding token to suffix tree: expected " + getTokenType() + ", found " + t.getType());
        }

        if(hasChild(t)) {
            return getChild(t);
        }

        SuffixTreeNode newNode = new SuffixTreeNode(t);

        children.put(t, newNode);

        return newNode;
    }

    public void addChildTree(SuffixTreeNode tree) throws ChecksimException {
        if(!tree.getTokenType().equals(getTokenType())) {
            throw new ChecksimException("Token type mismatch adding token to suffix tree: expected " + getTokenType() + ", found " + tree.getTokenType());
        }

        // Child does not need to be merged
        if(!hasChild(tree.getContent())) {
            children.put(tree.getContent(), tree);
            return;
        }

        // Child does need to be merged
        SuffixTreeNode matchingChild = children.get(tree.getContent());

        tree.getOccurrencesA().forEach(matchingChild::addOccurrenceA);
        tree.getOccurrencesB().forEach(matchingChild::addOccurrenceB);

        // Merge children
        // Cannot be a forEach - need to pass the ChecksimException on
        for(SuffixTreeNode child : tree.getChildrenAsList()) {
            matchingChild.addChildTree(child);
        }
    }
}
