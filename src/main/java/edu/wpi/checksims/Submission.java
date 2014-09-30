package edu.wpi.checksims;

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
    private final String name;
    
    public Submission(BufferedReader b, String name) throws IOException{
        if(name == null){
            throw new NullPointerException(Messages.getString("Submission.2")); //$NON-NLS-1$
        }
        this.name = name;
        final List<String> linesRead = new ArrayList<>();
        for(String line = b.readLine(); line != null; line = b.readLine()){
            linesRead.add(line);
        }
        this.lines = Collections.unmodifiableList(linesRead);
    }
    
    public Submission(final BufferedReader b) throws IOException {
        this(b, Messages.getString("Submission.3")); //$NON-NLS-1$
    }
    
    public Submission(final String[] lines, final String name) {
        if(name == null){
            throw new NullPointerException(Messages.getString("Submission.4")); //$NON-NLS-1$
        }
        this.name = name;
        List<String> linesRead = new ArrayList<>();
        for(String line : lines){
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
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    public int getNumLines() {
        return this.lines.size();
    }
}
