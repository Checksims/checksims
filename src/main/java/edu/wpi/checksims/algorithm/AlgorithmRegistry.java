package edu.wpi.checksims.algorithm;

import com.google.common.collect.ImmutableList;
import edu.wpi.checksims.ChecksimException;
import org.apache.commons.collections4.list.PredicatedList;
import org.apache.commons.collections4.list.SetUniqueList;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Registry for all supported plagiarism detection algorithms
 */
public class AlgorithmRegistry {
    private final List<PlagiarismDetector> supportedAlgorithms;

    private static AlgorithmRegistry instance;

    private AlgorithmRegistry() {
        // Ensure that no two algorithms have the same name
        List<String> allAlgNames = new LinkedList<>();
        List<PlagiarismDetector> detectors = PredicatedList.predicatedList(new LinkedList<>(), (detector) -> {
            if(allAlgNames.contains(detector.getName())) {
                return false;
            } else {
                allAlgNames.add(detector.getName());
                return true;
            }
        });

        // Use reflection to obtain a set of all classes implementing the PlagiarismDetector interface in the algorithm package
        Reflections algorithmPackage = new Reflections("edu.wpi.checksims.algorithm");
        Set<Class<? extends PlagiarismDetector>> allDetectors = algorithmPackage.getSubTypesOf(PlagiarismDetector.class);

        // Move through
        allDetectors.stream().forEach((alg) -> {
            // TODO convert to logging
            System.out.println("Initializing algorithm " + alg.getName());
            try {
                // We specify that all plagiarism detectors must have a getInstance() static method
                // This returns a default instance of the algorithm.
                Method getInstance = alg.getMethod("getInstance");

                // The return type of getInstance must be a plagiarism detector
                Class<PlagiarismDetector> plagiarismDetectorClass = PlagiarismDetector.class;
                if(!plagiarismDetectorClass.isAssignableFrom(getInstance.getReturnType())) {
                    throw new RuntimeException("Plagiarism detectors must implement a static getInstance() method returning a subtype of Plagiarism Detector");
                }

                // Invoke getInstance and add the returned detector to the list of available algorithms
                PlagiarismDetector p = (PlagiarismDetector)getInstance.invoke(null);
                detectors.add(p);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("All plagiarism detection algorithms must implement a static getInstance() method");
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException("Error invoking getInstance(): " + e.getMessage());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Tried to load two plagiarism detectors with same name - names must be globally unique!");
            }
        });

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
