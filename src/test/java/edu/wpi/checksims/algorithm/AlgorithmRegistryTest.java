package edu.wpi.checksims.algorithm;

import edu.wpi.checksims.ChecksimsException;
import edu.wpi.checksims.algorithm.linesimilarity.LineSimilarityChecker;
import edu.wpi.checksims.algorithm.smithwaterman.SmithWaterman;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Tests for the Algorithm Registry
 *
 * These are a tad iffy, because the registry itself only scans once
 * We're just hoping it scans the right things during that once
 */
public class AlgorithmRegistryTest {
    @Test
    public void TestIncludesLineCompare() throws ChecksimsException {
        Collection<String> algorithms = AlgorithmRegistry.getInstance().getSupportedImplementationNames();
        String lineCompareName = LineSimilarityChecker.getInstance().getName();

        assertNotNull(algorithms);
        assertTrue(algorithms.contains(lineCompareName));

        // This should not throw an exception
        SimilarityDetector lineCompare = AlgorithmRegistry.getInstance().getImplementationInstance(lineCompareName);
        assertEquals(lineCompare.getName(), lineCompareName);
    }

    @Test
    public void TestIncludeSmithWaterman() throws ChecksimsException {
        Collection<String> algorithms = AlgorithmRegistry.getInstance().getSupportedImplementationNames();
        String smithWatermanName = SmithWaterman.getInstance().getName();

        assertNotNull(algorithms);
        assertTrue(algorithms.contains(smithWatermanName));

        // This should not throw an exception
        SimilarityDetector smithWaterman = AlgorithmRegistry.getInstance().getImplementationInstance(smithWatermanName);
        assertEquals(smithWaterman.getName(), smithWatermanName);
    }

    @Test
    public void TestDefaultAlgorithmIsLineCompare() {
        SimilarityDetector algorithm = AlgorithmRegistry.getInstance().getDefaultImplementation();
        String algorithmName = AlgorithmRegistry.getInstance().getDefaultImplementationName();
        String lineCompareName = LineSimilarityChecker.getInstance().getName();

        assertNotNull(algorithm);
        assertNotNull(algorithmName);
        assertEquals(algorithm.getName(), algorithmName);
        assertEquals(algorithmName, lineCompareName);
    }

    @Test(expected = ChecksimsException.class)
    public void TestExceptionOnNonexistantAlgorithm() throws ChecksimsException {
        AlgorithmRegistry.getInstance().getImplementationInstance("does not exist");
    }
}
