package edu.wpi.checksims.algorithm.output;

import com.google.common.collect.ImmutableList;
import edu.wpi.checksims.ChecksimException;
import org.apache.commons.collections4.list.PredicatedList;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Get valid
 */
public class OutputRegistry {
    private final List<SimilarityMatrixPrinter> outputStrategies;

    private static OutputRegistry instance;

    private OutputRegistry() {
        // Ensure that output strategy names are globally unique
        List<String> outputStrategyNames = new LinkedList<>();
        List<SimilarityMatrixPrinter> outputStrategies = PredicatedList.predicatedList(new LinkedList<>(), (strat) -> {
            if(outputStrategyNames.contains(strat.getName())) {
                return false;
            } else {
                outputStrategyNames.add(strat.getName());
                return true;
            }
        });

        // Reflect on edu.wpi.checksims.algorithm.output to find all valid output strategies
        Reflections outputPackage = new Reflections("edu.wpi.checksims.algorithm.output");
        Set<Class<? extends SimilarityMatrixPrinter>> allOutputPrinters = outputPackage.getSubTypesOf(SimilarityMatrixPrinter.class);

        allOutputPrinters.stream().forEach((strategy) -> {
            // TODO log this
            System.out.println("Initializing output strategy " + strategy.getName());
            try {
                Method getInstance = strategy.getMethod("getInstance");

                Class<SimilarityMatrixPrinter> printerClass = SimilarityMatrixPrinter.class;
                if(!printerClass.isAssignableFrom(getInstance.getReturnType())) {
                    throw new RuntimeException("Output strategy " + strategy.getName() + " implements getInstance, but it does not return a SimilarityMatrixPrinter!");
                }

                // Invoke the method to produce a SimilarityMatrixPrinter
                // Then add it to the output strategies list.
                SimilarityMatrixPrinter printer = (SimilarityMatrixPrinter)getInstance.invoke(null);
                outputStrategies.add(printer);
            } catch (NoSuchMethodException e) {
                // TODO better logging + handling
                throw new RuntimeException("Output strategy " + strategy.getName() + " does not have a static getInstance method!");
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException("Error invoking getInstance method of " + strategy.getName() + ": " + e.getMessage());
            }
        });

        if(outputStrategies.isEmpty()) {
            throw new RuntimeException("No providers for SimilarityMatrixPrinter present!");
        }

        // The algorithms list should not be changed after initialization
        this.outputStrategies = ImmutableList.copyOf(outputStrategies);
    }

    public static OutputRegistry getInstance() {
        if(instance == null) {
            instance = new OutputRegistry();
        }

        return instance;
    }

    public SimilarityMatrixPrinter getOutputStrategy(String name) throws ChecksimException {
        String lowerName = name.toLowerCase();
        List<SimilarityMatrixPrinter> matchingStrategies = outputStrategies.stream().filter((strategy) -> strategy.getName().equals(lowerName)).collect(Collectors.toList());

        if(matchingStrategies.isEmpty()) {
            throw new ChecksimException("No output strategy found with name " + name);
        } else if(matchingStrategies.size() > 1) {
            throw new ChecksimException("Collision in output strategy names found!");
        }

        return matchingStrategies.get(0);
    }

    public List<String> getAllOutputStrategyName() {
        return outputStrategies.stream().map((strategy) -> strategy.getName()).collect(Collectors.toList());
    }

    public SimilarityMatrixPrinter getDefaultStrategy() {
        return outputStrategies.get(0);
    }

    public String getDefaultStrategyName() {
        return getDefaultStrategy().getName();
    }
}
