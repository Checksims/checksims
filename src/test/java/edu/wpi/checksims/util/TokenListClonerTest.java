package edu.wpi.checksims.util;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Trivial test cases for TokenListCloner
 */
public class TokenListClonerTest {
    private static List<Token<String>> empty;
    private static List<Token<String>> oneToken;
    private static List<Token<String>> twoTokens;

    @BeforeClass
    public static void setUp() {
        empty = new LinkedList<>();

        oneToken = new LinkedList<>();
        oneToken.add(new Token<>("hello"));

        twoTokens = new LinkedList<>();
        twoTokens.add(new Token<>("hello"));
        twoTokens.add(new Token<>("world"));
    }

    @Test
    public void testEmptyListClone() {
        List<Token<String>> results = TokenListCloner.cloneList(empty);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testOneElementListClone() {
        List<Token<String>> results = TokenListCloner.cloneList(oneToken);

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(results.size(), 1);
        assertEquals(results, oneToken);
    }

    @Test
    public void testTwoElementListClone() {
        List<Token<String>> results = TokenListCloner.cloneList(twoTokens);

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(results.size(), 2);
        assertEquals(results, twoTokens);
    }

    @Test
    public void testListCreatedIsClone() {
        List<Token<String>> results = TokenListCloner.cloneList(twoTokens);

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(results.size(), 2);
        assertEquals(results, twoTokens);

        results.get(0).setInvalid();

        assertNotEquals(results, twoTokens);
    }
}
