package edu.wpi.checksims.util;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Tests for the Pair class
 */
public class UnorderedPairTest {
    private static List<String> empty;
    private static List<String> oneElement;
    private static List<String> twoElements;
    private static List<String> threeElements;
    private static List<String> fourElements;

    @BeforeClass
    public static void setUp() {
        empty = new LinkedList<>();

        oneElement = new LinkedList<>();
        oneElement.add("1");

        twoElements = new LinkedList<>();
        twoElements.add("1");
        twoElements.add("2");

        threeElements = new LinkedList<>();
        threeElements.add("1");
        threeElements.add("2");
        threeElements.add("3");

        fourElements = new LinkedList<>();
        fourElements.add("1");
        fourElements.add("2");
        fourElements.add("3");
        fourElements.add("4");
    }

    @Test
    public void testPairUnorderedEquality() {
        UnorderedPair<String> ab = new UnorderedPair<>("a", "b");
        UnorderedPair<String> ba = new UnorderedPair<>("b", "a");
        UnorderedPair<String> bc = new UnorderedPair<>("b", "c");
        UnorderedPair<String> cd = new UnorderedPair<>("c", "d");

        assertTrue(ab.equalsIgnoreOrder(ab));
        assertTrue(ab.equalsIgnoreOrder(ba));
        assertFalse(ab.equalsIgnoreOrder(bc));
        assertFalse(ab.equalsIgnoreOrder(cd));
    }

    @Test
    public void testEmptyListReturnsEmpty() {
        Set<UnorderedPair<String>> results = UnorderedPair.generatePairsFromList(empty);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testOneElementListReturnsEmpty() {
        Set<UnorderedPair<String>> results = UnorderedPair.generatePairsFromList(oneElement);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testTwoElementListReturnsOnePair() {
        Set<UnorderedPair<String>> results = UnorderedPair.generatePairsFromList(twoElements);

        UnorderedPair<String> expected = new UnorderedPair<>("1", "2");

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(results.size(), 1);
        assertTrue(results.stream().allMatch((element) -> element.equalsIgnoreOrder(expected)));
    }

    @Test
    public void testThreeElementListReturnsThreePairs() {
        Set<UnorderedPair<String>> results = UnorderedPair.generatePairsFromList(threeElements);

        Set<UnorderedPair<String>> expected = new HashSet<>();
        expected.add(new UnorderedPair<>("1", "2"));
        expected.add(new UnorderedPair<>("2", "3"));
        expected.add(new UnorderedPair<>("1", "3"));

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(results.size(), 3);
        assertTrue(setsOfPairsContainsSameElements(results, expected));
    }

    @Test
    public void testFourElementListReturnsSixPairs() {
        Set<UnorderedPair<String>> results = UnorderedPair.generatePairsFromList(fourElements);

        Set<UnorderedPair<String>> expected = new HashSet<>();
        expected.add(new UnorderedPair<>("1", "2"));
        expected.add(new UnorderedPair<>("1", "3"));
        expected.add(new UnorderedPair<>("1", "4"));
        expected.add(new UnorderedPair<>("2", "3"));
        expected.add(new UnorderedPair<>("2", "4"));
        expected.add(new UnorderedPair<>("3", "4"));

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(results.size(), 6);
        assertTrue(setsOfPairsContainsSameElements(results, expected));
    }

    @Test
    public void testSetsOfPairsContainsSameElements() {
        UnorderedPair<String> ab = new UnorderedPair<>("a", "b");
        UnorderedPair<String> ba = new UnorderedPair<>("b", "a");
        UnorderedPair<String> cd = new UnorderedPair<>("c", "d");
        UnorderedPair<String> dc = new UnorderedPair<>("d", "c");
        UnorderedPair<String> ef = new UnorderedPair<>("e", "f");
        UnorderedPair<String> fg = new UnorderedPair<>("g", "h");

        Set<UnorderedPair<String>> empty = new HashSet<>();

        Set<UnorderedPair<String>> oneElt = new HashSet<>();
        oneElt.add(ab);

        Set<UnorderedPair<String>> one = new HashSet<>();
        one.add(ab);
        one.add(cd);

        Set<UnorderedPair<String>> two = new HashSet<>();
        two.add(ba);
        two.add(dc);

        Set<UnorderedPair<String>> three = new HashSet<>();
        three.add(ef);
        three.add(fg);

        assertTrue(setsOfPairsContainsSameElements(one, one));
        assertTrue(setsOfPairsContainsSameElements(two, two));
        assertTrue(setsOfPairsContainsSameElements(one, two));
        assertFalse(setsOfPairsContainsSameElements(empty, oneElt));
        assertFalse(setsOfPairsContainsSameElements(oneElt, one));
        assertFalse(setsOfPairsContainsSameElements(one, three));
        assertFalse(setsOfPairsContainsSameElements(two, three));
    }

    private static <T> boolean setsOfPairsContainsSameElements(Set<UnorderedPair<T>> one, Set<UnorderedPair<T>> two) {
        if(one.size() != two.size()) {
            return false;
        }

        // If all elements in set one are present in set 2, and the sets are the same length
        // we can assume the sets contain equivalent elements, as duplicate elements are not permitted
        return one.stream().allMatch((element1) -> two.stream().anyMatch(element1::equalsIgnoreOrder));
    }
}
