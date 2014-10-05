package edu.wpi.checksims;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LineSimilarityMatrixPrinter implements LineSimilarityReporter {

    @Override
    public void report (LineSimilarityMatrix similarities){
        final List<Submission> submissions = similarities.getSubmissions();
        final StringBuilder buffer = new StringBuilder();
        final int columnWidth = 15;
       
        // initialize empty similarity count matrix
        final Map<Submission, Map<Submission, Double>> simPctMatrix =
                new HashMap<>();
        for(final Submission s1 : submissions){
            final Map<Submission, Double> simPctRow = new HashMap<>();
            for(final Submission s2 : submissions){
                simPctRow.put(s2, 0.);
            }
            simPctMatrix.put(s1, simPctRow);
        }
        
        final LineSimilarityMatrix.EntryVisitor similarLineCounter = (sub, other, similarLines) -> simPctMatrix.get(sub).put(
                other,
                calculatePercentSimilar(sub, similarLines));
        
        similarities.visitAllEntries(similarLineCounter);
        
        buffer.append(makeTableHeader(columnWidth, submissions));
        // build a line for each submission
        for(final Submission sub : submissions) {
            buffer.append(makeTableLine(sub, submissions, simPctMatrix.get(sub), columnWidth));
        }
        
        System.out.print(buffer.toString());
    }
    
    /**
     * Return the percentage of the lines in "sub" that also appear in "other"
     * @param sub submission to check
     * @param allSims where each line in sub appears in other
     * @return a double between 0.00 and 1.00
     */
    private static double calculatePercentSimilar(
            Submission sub,
            Map<LineLocation, Set<LineLocation>> allSims){
        return((double)allSims.size() / (double)sub.getNumLines());
    }

    private static String makeEntry(Double entry, int width) { // yay, accounting puns!
        return String.format("%"+width+".2f", entry);
    }

    private static String makeTableHeader(final int columnWidth,
            final List<Submission> submissions) {
        final StringBuilder headerBuffer = new StringBuilder();
        // empty top/left cell
        for(int i = 0; i < columnWidth; i++){
            headerBuffer.append(" ");
        }
        for(final Submission sub : submissions){
            headerBuffer.append(String.format("%"+columnWidth+"s", sub.getName()));
        }
        headerBuffer.append("\n");
        return headerBuffer.toString();
    }
    
    // TODO separate computation from formatting
    private static String makeTableLine(
            Submission sub,
            List<Submission> allSubs,
            Map<Submission, Double> map,
            int columnWidth){
        StringBuilder buffer = new StringBuilder();
        buffer.append(makeLineHeader(columnWidth, sub));
        // build the line with the new similarity counts
        for(final Submission otherSub : allSubs){
            String entryToAdd = (sub.equals(otherSub)) ?
                    String.format("%"+columnWidth+"s", "*") :
                    makeEntry(map.get(otherSub), columnWidth);
            buffer.append(entryToAdd);
        }
        buffer.append("\n");
        return buffer.toString();
    }

    private static Object makeLineHeader(int columnWidth, Submission sub) {
        return(String.format("%"+columnWidth+"s", sub.getName()));
    }

    // TODO need much better naming here
    private static Map<Submission, Integer> getSimilarityCounts(
            final List<Submission> allSubmissions, 
            final Map<LineLocation, Set<LineLocation>> simlist) {
        final Map<Submission, Integer> similarityCounts = new HashMap<>();
        
        // initialize the map so all submissions have zero similar lines
        for(final Submission sub : allSubmissions){
            similarityCounts.put(sub, 0);
        }
        
        // for each line that was similar to another,
        for(final Set<LineLocation> similarities : simlist.values()){
            // add one to the value of each submission the line came from
            final Set<Submission> uniqueSubmissions = uniqueSubmissionsInLineLocationSet(similarities);
            for(final Submission eqSub : uniqueSubmissions){
                final int newCount = similarityCounts.get(eqSub) +1;
                similarityCounts.put(eqSub, newCount);
            }
        }
        
        return Collections.unmodifiableMap(similarityCounts);
    }

    private static Set<Submission> uniqueSubmissionsInLineLocationSet(
            Set<LineLocation> similarities) {
        final Set<Submission> uniqueSubmissions = new HashSet<>();
        for(LineLocation l : similarities){
            uniqueSubmissions.add(l.getSubmission());
        }
        return Collections.unmodifiableSet(uniqueSubmissions);
    }
}