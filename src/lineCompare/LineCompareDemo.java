package lineCompare;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class LineCompareDemo {
    
    public static void main(String[] args) {
        final Set<Submission> submissions = new HashSet<>();
        final LineSimilarityReporter reporter =
                new SubmissionPairSimilarityThresholdPrinter(0.2);
        
        for (String arg : args){
            try{
                submissions.add(readSubmission(arg));
            } catch(IOException e) {
                System.err.println(String.format(
                        Messages.getString("LineCompareDemo.1"), arg)); //$NON-NLS-1$
                return; // System.exit(1);
            }
        }
        
        LineSimilarityMatrix database = LineSimilarityMatrix.compute(submissions);
        
        reporter.report(database);
        
    }

    private static Submission readSubmission(String arg) throws IOException {
        final Path path = Paths.get(arg);
        try(BufferedReader reader =
                Files.newBufferedReader(path, StandardCharsets.UTF_8)){
            return new Submission(reader, path.getFileName().toString());
        }
    }
}
