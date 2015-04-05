package edu.wpi.checksims.algorithm.preprocessor;

import edu.wpi.checksims.ChecksimsException;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Tests for the preprocessor registry
 */
public class PreprocessorRegistryTest {
    @Test
    public void TestLowerCaseIsIncluded() throws ChecksimsException {
        String lowerCaseName = LowercasePreprocessor.getInstance().getName();
        Collection<String> supported = PreprocessorRegistry.getInstance().getSupportedImplementationNames();

        assertNotNull(supported);
        assertTrue(supported.contains(lowerCaseName));

        // Should not throw an exception
        SubmissionPreprocessor preprocessor = PreprocessorRegistry.getInstance().getImplementationInstance(lowerCaseName);
        assertEquals(preprocessor.getName(), lowerCaseName);
    }

    @Test
    public void TestWhitespaceDedupIsIncluded() throws ChecksimsException {
        String dedupName = WhitespaceDeduplicationPreprocessor.getInstance().getName();
        Collection<String> supported = PreprocessorRegistry.getInstance().getSupportedImplementationNames();

        assertNotNull(supported);
        assertTrue(supported.contains(dedupName));

        SubmissionPreprocessor preprocessor = PreprocessorRegistry.getInstance().getImplementationInstance(dedupName);
        assertEquals(preprocessor.getName(), dedupName);
    }

    @Test(expected = ChecksimsException.class)
    public void TestNonExistantThrowsException() throws ChecksimsException {
        PreprocessorRegistry.getInstance().getImplementationInstance("does not exist");
    }
}
