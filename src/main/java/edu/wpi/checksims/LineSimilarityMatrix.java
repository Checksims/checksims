package edu.wpi.checksims;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Immutable data structure that keeps track of all similarities between all
 * submissions in a single database
 *  
 * @author Dolan Murvihill
 *
 */
public class LineSimilarityMatrix {
        private final List<Submission> submissions;
        private final Map<Integer, Set<LineLocation>> database;
        
        private LineSimilarityMatrix(Set<Submission> submissions){
            this.submissions = new ArrayList<>(submissions);
            Map<Integer, Set<LineLocation>> db = new HashMap<>();
            for(Submission s : this.submissions){
                s.addToDatabase(db);
            }
            this.database = Collections.unmodifiableMap(db);
        }
        
        public static LineSimilarityMatrix compute(Set<Submission> submissions){
            return new LineSimilarityMatrix(submissions);
        }
    
        public interface EntryVisitor{
            /**
             * Perform a computation based on a particular entry of the matrix.
             * All keys in similarLines refer to LineLocations in sub, and sub
             * contains all lines which are identical to at least one line in
             * other. All LineLocations in the values of similarLines refer to
             * lines in other.  
             * @param sub The submission checked for similarity
             * @param other The submission checked against
             * @param similarLines lines in sub, with all similar lines in other
             */
            public void visit(
                    Submission sub,
                    Submission other,
                    Map<LineLocation, Set<LineLocation>> similarLines);
        }
        
        public List<Submission> getSubmissions(){
            return this.submissions;
        }
        
        public void visitAllEntries(EntryVisitor visitor){
            for(Submission s1 : this.submissions){
                for(Submission s2 : this.submissions){
                    final Map<LineLocation, Set<LineLocation>> matches =
                            filterSimilaritiesBySubmission(
                                    s1.getSimilarLines(this.database), s2);
                    visitor.visit(s1, s2, matches);
                }
            }
        }
       
        private static Map<LineLocation, Set<LineLocation>>
                filterSimilaritiesBySubmission(
                        final Map<LineLocation, Set<LineLocation>> similarLines,
                        final Submission sub) {
            final Map<LineLocation, Set<LineLocation>> newLines =
                    new HashMap<>();
            for(final Map.Entry<LineLocation, Set<LineLocation>> entry
                    : similarLines.entrySet()){
                final Set<LineLocation> matchedLines =
                        filterLineLocationSetBySubmission(
                                entry.getValue(), sub);
                if(!matchedLines.isEmpty()){
                    newLines.put(entry.getKey(), matchedLines);
                }
            }
            return Collections.unmodifiableMap(newLines);
        }
        
        private static Set<LineLocation> filterLineLocationSetBySubmission(
                final Set<LineLocation> set, final Submission submission){
            Set<LineLocation> newSet = new HashSet<>();
            for(LineLocation matchLocation : set){
                if(matchLocation.getSubmission().equals(submission)){
                    newSet.add(matchLocation);
                }
            }
            return Collections.unmodifiableSet(newSet);
        }

        @Override
        public String toString(){
            return String.format(
                    Messages.getString("LineSimilarityMatrix.0"), //$NON-NLS-1$
                    this.submissions.size());
        }
}
