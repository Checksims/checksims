package lineCompare;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Submission {
    private final List<String> lines;
    
    public Submission(BufferedReader b) throws IOException{
        final List<String> linesRead = new ArrayList<>();
        for(String line = b.readLine(); line != null; line = b.readLine()){
            linesRead.add(line);
        }
        this.lines = Collections.unmodifiableList(linesRead);
    }
    
    public String getLine(int lineNum){
        if(lineNum < 1){
            throw new IllegalArgumentException(
                    Messages.getString("Submission.1")); //$NON-NLS-1$
        }
        return this.lines.get(lineNum - 1);
    }
    
    public Map<LineLocation, Set<LineLocation>> getSimilarLines(
            final Map<Integer, Set<LineLocation>> others) {
        final Map<LineLocation, Set<LineLocation>> matches = new HashMap<>();
        final Iterator<String> it = this.lines.iterator();
        int i = 0;
        while(it.hasNext()){
            i++;
            Integer hash = Integer.valueOf(it.next().hashCode());
            Set<LineLocation> locs_appearing = others.get(hash);
            if(locs_appearing != null){
                matches.put(new LineLocation(this, i), locs_appearing);
            }
        }
        return matches;
    }
    
    public void addToDatabase(final Map<Integer, Set<LineLocation>> database) {
        int i = 0;
        for(String line : this.lines){
            final Integer hash = Integer.valueOf(line.hashCode()); 
            i++;
            Set<LineLocation> locs = database.get(hash);
            if(locs == null){
                locs = new HashSet<>();
                database.put(hash, locs);
            }
            locs.add(new LineLocation(this, i));
        }
    }
    
    @Override
    public String toString(){
        return String.format(Messages.getString("Submission.0"), //$NON-NLS-1$
                Integer.valueOf(this.lines.size()));
    }
}
