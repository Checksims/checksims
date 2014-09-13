package lineCompare;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SubmissionTest {
    @Mock private BufferedReader reader;
    @Mock private Submission submission;
    
    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void addToDatabase() throws IOException{
        final Map<Integer, Set<LineLocation>> database = new HashMap<>();
        verifyLinesAdded(database);
    }
    
    @SuppressWarnings("nls")
    @Test
    public void addToDatabase_lineAlreadyPresent() throws IOException{
        final Map<Integer, Set<LineLocation>> database = new HashMap<>();
        final int lineNum = 40;
        final Set<LineLocation> set = new HashSet<>();
        set.add(new LineLocation(this.submission, lineNum));
        database.put(Integer.valueOf("line1".hashCode()), set);
        verifyLinesAdded(database);
    }
    
    private static Map<Integer, Set<LineLocation>> copyLineLocationDatabase(
            final Map<Integer, Set<LineLocation>> database){
        final Map<Integer, Set<LineLocation>> databaseBefore = new HashMap<>();
        for(Map.Entry<Integer, Set<LineLocation>> entry : database.entrySet()){
            final Set<LineLocation> setBefore = new HashSet<>();
            setBefore.addAll(entry.getValue());
            databaseBefore.put(entry.getKey(),
                    Collections.unmodifiableSet(setBefore));
        }
        return Collections.unmodifiableMap(databaseBefore);
    }
    
    @SuppressWarnings("nls")
    private void verifyLinesAdded(
            final Map<Integer, Set<LineLocation>> database)
                    throws IOException {
        final Map<Integer, Set<LineLocation>> dbBefore =
                copyLineLocationDatabase(database);
        final String[] lines = {"line1", "line2", "line3"};
        when(this.reader.readLine())
            .thenReturn(lines[0])
            .thenReturn(lines[1])
            .thenReturn(lines[2])
            .thenReturn(null);
        final Submission testee = new Submission(this.reader);
        
        testee.addToDatabase(database);
        
        int expNumEntries = dbBefore.size();
        for(int i = 0; i < lines.length; ++i){
            final Integer key = Integer.valueOf(lines[i].hashCode());
            if(!(dbBefore.containsKey(key))){
                expNumEntries++;
            }
            final Set<LineLocation> setBefore = dbBefore.get(key);
            final int linesBefore = (setBefore == null) ? 0 : setBefore.size(); 
            final Set<LineLocation> set = database.get(key);
            
            assertEquals(1 + linesBefore, set.size());
            final LineLocation toCmp = new LineLocation(testee, i+1);
            assertTrue(set.contains(toCmp));
            if(setBefore != null){
                for(LineLocation l : setBefore){
                    assertTrue(set.contains(l));
                }
            }
        }
        assertEquals(expNumEntries, database.size());
        verify(this.reader, times(4)).readLine();
        verifyNoMoreInteractions(this.reader);
    }
}
