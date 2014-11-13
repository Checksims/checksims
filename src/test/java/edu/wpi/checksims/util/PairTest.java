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
public class PairTest {
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
        Pair<String> ab = new Pair<>("a", "b");
        Pair<String> ba = new Pair<>("b", "a");
        Pair<String> bc = new Pair<>("b", "c");
        Pair<String> cd = new Pair<>("c", "d");

        assertTrue(ab.equalsIgnoreOrder(ab));
        assertTrue(ab.equalsIgnoreOrder(ba));
        assertFalse(ab.equalsIgnoreOrder(bc));
        assertFalse(ab.equalsIgnoreOrder(cd));
    }

    @Test
    public void testEmptyListReturnsEmpty() {
        Set<Pair<String>> results = Pair.generatePairsFromList(empty);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testOneElementListReturnsEmpty() {
        Set<Pair<String>> results = Pair.generatePairsFromList(oneElement);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testTwoElementListReturnsOnePair() {
        Set<Pair<String>> results = Pair.generatePairsFromList(twoElements);

        Pair<String> expected = new Pair<>("1", "2");

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(results.size(), 1);
        assertTrue(results.stream().allMatch((element) -> element.equalsIgnoreOrder(expected)));
    }

    @Test
    public void testThreeElementListReturnsThreePairs() {
        Set<Pair<String>> results = Pair.generatePairsFromList(threeElements);

        Set<Pair<String>> expected = new HashSet<>();
        expected.add(new Pair<>("1", "2"));
        expected.add(new Pair<>("2", "3"));
        expected.add(new Pair<>("1", "3"));

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(results.size(), 3);
        assertTrue(setsOfPairsContainsSameElements(results, expected));
    }

    @Test
    public void testFourElementListReturnsSixPairs() {
        Set<Pair<String>> results = Pair.generatePairsFromList(fourElements);

        Set<Pair<String>> expected = new HashSet<>();
        expected.add(new Pair<>("1", "2"));
        expected.add(new Pair<>("1", "3"));
        expected.add(new Pair<>("1", "4"));
        expected.add(new Pair<>("2", "3"));
        expected.add(new Pair<>("2", "4"));
        expected.add(new Pair<>("3", "4"));

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(results.size(), 6);
        assertTrue(setsOfPairsContainsSameElements(results, expected));
    }

    @Test
    public void testSetsOfPairsContainsSameElements() {
        Pair<String> ab = new Pair<>("a", "b");
        Pair<String> ba = new Pair<>("b", "a");
        Pair<String> cd = new Pair<>("c", "d");
        Pair<String> dc = new Pair<>("d", "c");
        Pair<String> ef = new Pair<>("e", "f");
        Pair<String> fg = new Pair<>("g", "h");

        Set<Pair<String>> empty = new HashSet<>();

        Set<Pair<String>> oneElt = new HashSet<>();
        oneElt.add(ab);

        Set<Pair<String>> one = new HashSet<>();
        one.add(ab);
        one.add(cd);

        Set<Pair<String>> two = new HashSet<>();
        two.add(ba);
        two.add(dc);

        Set<Pair<String>> three = new HashSet<>();
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

    private static <T> boolean setsOfPairsContainsSameElements(Set<Pair<T>> one, Set<Pair<T>> two) {
        if(one.size() != two.size()) {
            return false;
        }

        // If all elements in set one are present in set 2, and the sets are the same length
        // we can assume the sets contain equivalent elements, as duplicate elements are not permitted
        return one.stream().allMatch((element1) -> two.stream().anyMatch(element1::equalsIgnoreOrder));
    }
}
