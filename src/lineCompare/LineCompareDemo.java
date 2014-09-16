package lineCompare;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LineCompareDemo {

    public static void main(String[] args) {
        final List<Submission> submissions = new LinkedList<>();
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
        
        final Iterator<Submission> it = submissions.iterator();
        final Submission toCheck = it.next();
        while(it.hasNext()){
            it.next().addToDatabase(database);
        }
        
        Map<LineLocation, Set<LineLocation>> similarities =
                toCheck.getSimilarLines(database);
        System.out.println(similarities.size() +
                Messages.getString("LineCompareDemo.0")); //$NON-NLS-1$
    }

    private static Submission readSubmission(String arg) throws IOException {
        final Path path = Paths.get(arg);
        try(BufferedReader reader =
                Files.newBufferedReader(path, StandardCharsets.UTF_8)){
            return new Submission(reader);
        }
    }
}
