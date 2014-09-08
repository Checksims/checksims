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

public final class Submission {
    private final List<String> lines;
    
    public Submission(BufferedReader b) throws IOException{
        final List<String> linesRead = new ArrayList<>();
        for(String line = b.readLine(); line != null; line = b.readLine()){
            linesRead.add(line);
        }
        this.lines = Collections.unmodifiableList(linesRead); // TODO replace this with ImmutableList
    }
    
    public String getLine(int lineNum){
        return this.lines.get(lineNum);
    }
    
    public Map<LineLocation, Set<LineLocation>> getSimilarLines(
            final Map<Integer, Set<LineLocation>> others) {
        final Map<LineLocation, Set<LineLocation>> matches = new HashMap<>();
        final Iterator<String> it = this.lines.iterator();
        int i = 0;
        while(it.hasNext()){
            Integer hash = Integer.valueOf(it.next().hashCode());
            Set<LineLocation> locs_appearing = others.get(hash);
            if(locs_appearing != null){
                matches.put(new LineLocation(this, i), locs_appearing);
            }
            i++;
        }
        return matches;
    }
    
    public void addToDatabase(final Map<Integer, Set<LineLocation>> database) {
        int i = 0;
        for(String line : this.lines){
            i++;
            final Integer hash = Integer.valueOf(line.hashCode()); 
            Set<LineLocation> locs = database.get(hash);
            if(locs == null){
                locs = new HashSet<>();
                database.put(hash, locs);
            }
            locs.add(new LineLocation(this, i));
        }
    }
}
