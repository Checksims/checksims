package lineCompare;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LineCompareDemo {

    public static void main(String[] args) throws IOException {
        final List<Submission> submissions = new LinkedList<>();
        final Map<Integer, Set<LineLocation>> database = new HashMap<>();
        
        for (String arg : args){
            submissions.add(readSubmission(arg));
        }
        
        final Iterator<Submission> it = submissions.iterator();
        final Submission toCheck = it.next();
        while(it.hasNext()){
            it.next().addToDatabase(database);
        }
        
        Map<LineLocation, Set<LineLocation>> similarities = toCheck.getSimilarLines(database);
        System.out.println(similarities.size() + Messages.getString("LineCompareDemo.0")); //$NON-NLS-1$
    }

    private static Submission readSubmission(String arg) throws IOException {
        final Path path = Paths.get(arg);
        return new Submission(Files.newBufferedReader(path, StandardCharsets.UTF_8));
    }

}
