package lineCompare;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
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

import org.junit.Test;

@SuppressWarnings({"nls", "static-method"})
public class SubmissionTest {
    private static final String[] defaultLines = {"line1", "line2", "line3"};
    
    @Test
    public void testToString() throws IOException{
        final Submission submission = makeDefaultTestSubmission();
        assertEquals("[Submission, 3 lines]", submission.toString());
    }
    
    @Test
    public void testGetLine() throws IOException{
        final Submission submission = makeDefaultTestSubmission();
        assertEquals("line1", submission.getLine(1));
        assertEquals("line3", submission.getLine(3));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testGetLine_argumentZeroOrLess() throws IOException {
        final Submission submission = makeDefaultTestSubmission();
        submission.getLine(0);
    }
    
    @Test
    public void addToDatabase() throws IOException{
        final Map<Integer, Set<LineLocation>> database = new HashMap<>();
        verifyLinesAdded(database);
    }
    
    @Test
    public void addToDatabase_lineAlreadyPresent() throws IOException{
        final Submission submission = makeDefaultTestSubmission();
        final Map<Integer, Set<LineLocation>> database = new HashMap<>();
        final int lineNum = 40;
        final Set<LineLocation> set = new HashSet<>();
        set.add(new LineLocation(submission, lineNum));
        database.put(Integer.valueOf("line1".hashCode()), set);
        verifyLinesAdded(database);
    }
    
    @Test
    public void getSimilarLines() throws IOException{
        final Map<Integer, Set<LineLocation>> database = new HashMap<>();
        final Submission sub1 = mock(Submission.class);
        final Submission sub2 = mock(Submission.class);
        // line 1 is contained in both other submissions and should appear once
        // in the output
        final Set<LineLocation> set1 = new HashSet<>();
        set1.add(new LineLocation(sub1, 10));
        set1.add(new LineLocation(sub2, 12));
        database.put(Integer.valueOf("line1".hashCode()), set1);
        // line 2 is contained in one other submission and should also appear
        // once in the output
        final Set<LineLocation> set2 = new HashSet<>();
        set2.add(new LineLocation(sub2, 18));
        database.put(Integer.valueOf("line2".hashCode()), set2);
        // line 3 is contained in neither submission and should not appear in
        // the output
        
        // line 4 is contained in one submission, but not the testee, and so
        // should not appear in the output
        final Set<LineLocation> set3 = new HashSet<>();
        set3.add(new LineLocation(sub1, 25));
        database.put(Integer.valueOf("line4".hashCode()), set3);
        
        final Submission testee = makeDefaultTestSubmission();
        Map<LineLocation, Set<LineLocation>> simLines =
                testee.getSimilarLines(database);
        
        assertEquals(2, simLines.size());
        assertEquals(set1, simLines.get(new LineLocation(testee, 1)));
        assertEquals(set2, simLines.get(new LineLocation(testee, 2)));
    
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
    
    @SuppressWarnings("resource")
    private static Submission makeDefaultTestSubmission() throws IOException{
        final BufferedReader reader = mock(BufferedReader.class);
        when(reader.readLine())
            .thenReturn(defaultLines[0])
            .thenReturn(defaultLines[1])
            .thenReturn(defaultLines[2])
            .thenReturn(null);
        final Submission submission = new Submission(reader);
        verify(reader, times(4)).readLine();
        verifyNoMoreInteractions(reader);
        return submission;
    }
    
    private static void verifyLinesAdded(
            final Map<Integer, Set<LineLocation>> database)
                    throws IOException {
        final Map<Integer, Set<LineLocation>> dbBefore =
                copyLineLocationDatabase(database);
        
        final Submission testee = makeDefaultTestSubmission();
        testee.addToDatabase(database);
        
        int expNumEntries = dbBefore.size();
        for(int i = 0; i < defaultLines.length; ++i){
            final Integer key = Integer.valueOf(defaultLines[i].hashCode());
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
    }
}
