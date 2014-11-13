package edu.wpi.checksims.algorithm;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import edu.wpi.checksims.ChecksimException;
import edu.wpi.checksims.util.reflection.ReflectiveInstantiator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Registry for all supported plagiarism detection algorithms
 */
public class AlgorithmRegistry {
    private final List<PlagiarismDetector> supportedAlgorithms;

    private static AlgorithmRegistry instance;

    private AlgorithmRegistry() {
        List<PlagiarismDetector> detectors = ReflectiveInstantiator.reflectiveInstantiator("edu.wpi.checksims.algorithm", PlagiarismDetector.class);

        if(detectors.isEmpty()) {
            throw new RuntimeException("No plagiarism detection algorithms registered! Cannot continue!");
        }

        // Get a list without duplicates
        // If it's a different size, then duplicates existed, which is bad
        // Throw a RuntimeException for that!
        ImmutableList<String> noDups = ImmutableSet.copyOf(detectors.stream().map(PlagiarismDetector::getName).collect(Collectors.toList())).asList();
        if(noDups.size() < detectors.size()) {
            throw new RuntimeException("Some algorithm names were not globally unique!");
        }

        // The final list should never change at runtime
        supportedAlgorithms = ImmutableList.copyOf(detectors);
    }

    public static AlgorithmRegistry getInstance() {
        if(instance == null) {
            instance = new AlgorithmRegistry();
        }

        return instance;
    }

    /**
     * For convenience's sake, the first algorithm added to the list of detectors is considered to be the default
     *
     * @return Default plagiarism detection algorithm
     */
    public PlagiarismDetector getDefaultAlgorithm() {
        return supportedAlgorithms.get(0);
    }

    /**
     * @return Name of default plagiarism detection algorithm
     */
    public String getDefaultAlgorithmName() {
        return getDefaultAlgorithm().getName();
    }

    /**
     * @return List of names of supported algorithms
     */
    public List<String> getSupportedAlgorithmNames() {
        return supportedAlgorithms.stream().map(PlagiarismDetector::getName).collect(Collectors.toList());
    }

    /**
     * Get a specific plagiarism detection algorithm by its CLI name
     *
     * @param name CLI invocation name
     * @return Plagiarism detection algorithm of that name
     * @throws ChecksimException Thrown on no algorithm or more than one algorithm of that name existing
     */
    public PlagiarismDetector getAlgorithmInstance(String name) throws ChecksimException {
        String lowerName = name.toLowerCase(); // Ensure case insensitivity
        List<PlagiarismDetector> detectors = supportedAlgorithms.stream().filter((alg) -> alg.getName().equals(lowerName)).collect(Collectors.toList());

        if(detectors.size() == 0) {
            throw new ChecksimException("No algorithm with name " + name);
        } else if(detectors.size() > 1) {
            throw new ChecksimException("INTERNAL ERROR: two algorithms share the same name!");
        }

        return detectors.get(0);
    }
}
