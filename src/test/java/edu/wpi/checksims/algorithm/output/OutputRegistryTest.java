package edu.wpi.checksims.algorithm.output;

import edu.wpi.checksims.ChecksimsException;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Tests for the output registry
 */
public class OutputRegistryTest {
    @Test
    public void TestOutputRegistryContainsThreshold() throws ChecksimsException {
        String thresholdName = SimilarityMatrixThresholdPrinter.getInstance().getName();
        Collection<String> supportedPrinters = OutputRegistry.getInstance().getSupportedImplementationNames();

        assertNotNull(supportedPrinters);
        assertTrue(supportedPrinters.contains(thresholdName));

        // This should not throw an exception
        SimilarityMatrixPrinter strategy = OutputRegistry.getInstance().getImplementationInstance(thresholdName);
        assertEquals(strategy.getName(), thresholdName);
    }

    @Test
    public void TestOutputRegistryContainsCSV() throws ChecksimsException {
        String csvName = SimilarityMatrixAsCSVPrinter.getInstance().getName();
        Collection<String> supportedPrinters = OutputRegistry.getInstance().getSupportedImplementationNames();

        assertNotNull(supportedPrinters);
        assertTrue(supportedPrinters.contains(csvName));

        // This should not throw an exception
        SimilarityMatrixPrinter strategy = OutputRegistry.getInstance().getImplementationInstance(csvName);
        assertEquals(strategy.getName(), csvName);
    }

    @Test
    public void TestOutputRegistryContainsHTML() throws ChecksimsException {
        String htmlName = SimilarityMatrixAsHTMLPrinter.getInstance().getName();
        Collection<String> supportedPrinters = OutputRegistry.getInstance().getSupportedImplementationNames();

        assertNotNull(supportedPrinters);
        assertTrue(supportedPrinters.contains(htmlName));

        // This should not throw an exception
        SimilarityMatrixPrinter strategy = OutputRegistry.getInstance().getImplementationInstance(htmlName);
        assertEquals(strategy.getName(), htmlName);
    }

    @Test
    public void TestDefaultStrategyIsThreshold() {
        String thresholdName = SimilarityMatrixThresholdPrinter.getInstance().getName();
        String defaultName = OutputRegistry.getInstance().getDefaultImplementationName();
        SimilarityMatrixPrinter defaultPrinter = OutputRegistry.getInstance().getDefaultImplementation();

        assertNotNull(defaultPrinter);
        assertEquals(thresholdName, defaultName);
        assertEquals(thresholdName, defaultPrinter.getName());
    }

    @Test(expected = ChecksimsException.class)
    public void TestInvalidNameThrowsException() throws ChecksimsException {
        OutputRegistry.getInstance().getImplementationInstance("does not exist");
    }
}
