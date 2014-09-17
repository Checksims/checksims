package lineCompare;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LineCompareDemo {
    
    private static class LineSimilarityMatrixPrinter
            implements LineSimilarityReporter {
        public LineSimilarityMatrixPrinter() {
            // do nothing
        }

        @Override
        public void report(
                Map<Submission, Map<LineLocation, Set<LineLocation>>> similarities) {
            final Map<Submission, Map<LineLocation, Set<LineLocation>>> sims =
                    Collections.unmodifiableMap(similarities);
            final List<Submission> submissions = new LinkedList<>(sims.keySet());
            final StringBuffer buffer = new StringBuffer();
            final int columnWidth = 15;
            buffer.append(makeTableHeader(columnWidth, submissions));
            
            // build a line for each submission
            for(final Submission sub : submissions) {
                // Get the similarity counts for each other submission
                final Map<Submission, Integer> simCounts =
                        Collections.unmodifiableMap(
                                getSimilarityCounts(submissions, sims.get(sub)));
                buffer.append(makeTableLine(sub, submissions, simCounts, columnWidth));
            }
            
            System.out.print(buffer.toString());
        }

        private static String makeCalculatedEntry(Submission sub, int equalLines, int width) {
            return String.format("%"+width+".2f", (double)equalLines /sub.getNumLines());
        }

        private static String makeTableHeader(final int columnWidth,
                final List<Submission> submissions) {
            final StringBuffer headerBuffer = new StringBuffer();
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
                Map<Submission, Integer> simCounts,
                int columnWidth){
            StringBuffer buffer = new StringBuffer();
            buffer.append(makeLineHeader(columnWidth, sub));
            // build the line with the new similarity counts
            for(final Submission otherSub : allSubs){
                String entryToAdd = (sub.equals(otherSub)) ?
                        String.format("%"+columnWidth+"s", "*") :
                        makeCalculatedEntry(sub, simCounts.get(otherSub).intValue(), columnWidth);
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
                similarityCounts.put(sub, Integer.valueOf(0));
            }
            
            // for each line that was similar to another,
            for(final Set<LineLocation> similarities : simlist.values()){
                // add one to the value of each submission the line came from
                final Set<Submission> uniqueSubmissions = uniqueSubmissionsInLineLocationSet(similarities);
                for(final Submission eqSub : uniqueSubmissions){
                    final int newCount = similarityCounts.get(eqSub).intValue()+1;
                    similarityCounts.put(eqSub, Integer.valueOf(newCount));
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

    public static void main(String[] args) {
        final Set<Submission> submissions = new HashSet<>();
        final Map<Integer, Set<LineLocation>> database = new HashMap<>();
        
        for (String arg : args){
            try{
                submissions.add(readSubmission(arg));
            } catch(NoSuchFileException e){
                System.err.println(String.format(
                        Messages.getString("LineCompareDemo.1"), arg)); //$NON-NLS-1$
                return; // System.exit(1);
            } catch(IOException e) {
                System.err.println(String.format(
                        Messages.getString("LineCompareDemo.3"),        //$NON-NLS-1$
                        arg,
                        e.getMessage()));
                return; //System.exit(1);
            }
        }
        
        // add all submissions to the master database of lines
        for(Submission submission : submissions){
            submission.addToDatabase(database);
        }

        // build a giant mega-collection of all similar lines and all similar assignments.
        // TODO simplify
        final Map<Submission, Map<LineLocation, Set<LineLocation>>>
            allSimilarities = new HashMap<>();
        for(Submission sub : submissions) {
            allSimilarities.put(sub, Collections.unmodifiableMap(sub.getSimilarLines(database)));
        }
        
        new LineSimilarityMatrixPrinter().report(allSimilarities);
        
    }

    private static Submission readSubmission(String arg) throws IOException {
        final Path path = Paths.get(arg);
        try(BufferedReader reader =
                Files.newBufferedReader(path, StandardCharsets.UTF_8)){
            return new Submission(reader, path.getFileName().toString());
        }
    }
}
