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

package edu.wpi.checksims.token.tree;

import com.google.common.collect.ImmutableList;
import edu.wpi.checksims.ChecksimException;
import edu.wpi.checksims.token.Token;
import edu.wpi.checksims.token.TokenList;
import edu.wpi.checksims.token.TokenType;
import org.apache.commons.collections4.list.SetUniqueList;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Node in a suffix tree of tokens
 */
public class SuffixTreeNode extends SuffixTreeShared {
    private final List<Integer> occurrencesA;
    private final List<Integer> occurrencesB;
    private final Token content;
    private final TokenType type;
    private boolean isValid;

    public SuffixTreeNode(Token content) {
        this(content, SetUniqueList.setUniqueList(new LinkedList<>()), SetUniqueList.setUniqueList(new LinkedList<>()));
    }

    private SuffixTreeNode(Token content, List<Integer> occurrencesA, List<Integer> occurrencesB) {
        this.content = content;
        this.type = content.getType();
        this.isValid = content.isValid();
        this.occurrencesA = occurrencesA;
        this.occurrencesB = occurrencesB;
    }

    public Token getContent() {
        return content;
    }

    public TokenType getTokenType() {
        return type;
    }

    public boolean containsOccurrenceA(int toCheck) {
        return occurrencesA.contains(toCheck);
    }

    public boolean containsOccurrenceB(int toCheck) {
        return occurrencesB.contains(toCheck);
    }

    public List<Integer> getOccurrencesA() {
        return ImmutableList.copyOf(occurrencesA);
    }

    public List<Integer> getOccurrencesB() {
        return ImmutableList.copyOf(occurrencesB);
    }

    public void addOccurrenceA(int index) {
        if(index < 0) {
            throw new RuntimeException("Invalid index (below 0) added to occurrences!");
        }

        occurrencesA.add(index);
    }

    public void addOccurrenceB(int index) {
        if(index < 0) {
            throw new RuntimeException("Invalid index (below 0) added to occurrences!");
        }

        occurrencesB.add(index);
    }

    public boolean isShared() {
        return (!occurrencesA.isEmpty()) && (!occurrencesB.isEmpty());
    }

    public boolean isValid() {
        return isValid;
    }

    public int getMaxSharedDepth() {
        if(!this.isShared() || !this.isValid()) {
            return 0;
        }

        if(!this.hasChildren()) {
            return 1;
        }

        List<Integer> childValues = getChildrenAsList().stream().map(SuffixTreeNode::getMaxSharedDepth).collect(Collectors.toList());

        return 1 + Collections.max(childValues);
    }

    public String toString() {
        return "A suffix tree node containing token " + content.getTokenAsString();
    }

    public boolean equals(Object other) {
        if(!(other instanceof SuffixTreeNode)) {
            return false;
        }

        SuffixTreeNode otherNode = (SuffixTreeNode)other;

        return otherNode.getContent().equals(this.getContent()) && otherNode.getOccurrencesA().equals(this.getOccurrencesA()) && otherNode.getOccurrencesB().equals(this.getOccurrencesB());
    }

    public int hashCode() {
        return this.content.hashCode();
    }

    public static SuffixTreeNode cloneNode(SuffixTreeNode other) throws ChecksimException {
        SuffixTreeNode newNode = new SuffixTreeNode(other.getContent());

        newNode.occurrencesA.addAll(other.occurrencesA);
        newNode.occurrencesB.addAll(other.occurrencesB);

        for(SuffixTreeNode child : other.getChildrenAsList()) {
            newNode.addChildTree(SuffixTreeNode.cloneNode(child));
        }

        return newNode;
    }

    public static SuffixTreeNode listToUnarySuffixTree(TokenList list, SubmissionID submission) throws ChecksimException {
        if(list.isEmpty()) {
            throw new ChecksimException("Cannot produce suffix tree from empty token list!");
        }

        BiConsumer<SuffixTreeNode,Integer> addOccurrence;

        switch(submission) {
            case A:
                addOccurrence = SuffixTreeNode::addOccurrenceA;
                break;
            case B:
                addOccurrence = SuffixTreeNode::addOccurrenceB;
                break;
            default:
                throw new ChecksimException("Unrecognized submission ID");
        }

        SuffixTreeNode topNode = null;
        SuffixTreeNode currentNode = null;
        int counter = 0;

        // Iterate through all tokens in the list
        for(Token t : list) {
            SuffixTreeNode newNode = new SuffixTreeNode(t);

            addOccurrence.accept(newNode, counter);

            if(topNode == null) {
                topNode = newNode;
            } else {
                currentNode.addChildTree(newNode);
            }

            currentNode = newNode;

            counter++;
        }

        return topNode;
    }
}
