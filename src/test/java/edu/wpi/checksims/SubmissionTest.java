package edu.wpi.checksims;

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
import org.mockito.stubbing.OngoingStubbing;

@SuppressWarnings({"nls", "static-method"})
public class SubmissionTest {
    
    @SuppressWarnings("unused")
    @Test(expected=NullPointerException.class)
    public void testArrayConstructor_nullName(){
        final String[] lines = {};
        new Submission(lines, null);
    }
   
    @Test(expected=IndexOutOfBoundsException.class)
    public void testArrayConstructor_noLines(){
        final String[] lines = {};
        final Submission submission = new Submission(lines, "testsub");
        assertEquals("testsub", submission.getName());
        assertEquals(0, submission.getNumLines());
        submission.getLine(1);
    }
    
    @Test(expected=IndexOutOfBoundsException.class)
    public void testArrayConstructor_oneLine(){
        final String[] lines = {"test1"};
        final Submission submission = new Submission(lines, "testsub");
        assertEquals("testsub", submission.getName());
        assertEquals(1, submission.getNumLines());
        assertEquals("test1", submission.getLine(1));
        submission.getLine(2);
    }
    @Test(expected=IndexOutOfBoundsException.class)
    public void testArrayConstructor_moreLines(){
        final String[] lines = {"test1", "test2"};
        final Submission submission = new Submission(lines, "testsub");
        assertEquals("testsub", submission.getName());
        assertEquals(2, submission.getNumLines());
        assertEquals("test1", submission.getLine(1));
        assertEquals("test2", submission.getLine(2));
        submission.getLine(3);
    }
    
    @Test
    public void testToString() throws IOException{
        final Submission submission = makeTestSubmission(3);
        assertEquals("unnamed submission", submission.toString());
    }
    
    @Test
    public void testToString_differentName(){
        final String[] args = {"line1"};
        final Submission submission = new Submission(args, "testname");
        assertEquals("testname", submission.toString());
    }
    
    private static BufferedReader stubTestReader(int numLines) throws IOException{
        final BufferedReader reader = mock(BufferedReader.class);
        OngoingStubbing<String> stubbing = when(reader.readLine());
        for(int i = 0; i < numLines; i++){
            stubbing = stubbing.thenReturn("line"+(i+1));
        }
        stubbing.thenReturn(null);
        return reader;
    }
    
    @SuppressWarnings("resource")
    @Test
    public void testGetName() throws IOException{
        final BufferedReader reader = stubTestReader(0);
        final Submission submission = new Submission(reader, "testSubmission");
        assertEquals("testSubmission", submission.getName());
    }
    
    @SuppressWarnings("resource")
    @Test
    public void testGetName_noNameProvided() throws IOException{
        final BufferedReader reader = stubTestReader(0);
        final Submission submission = new Submission(reader);
        assertEquals("unnamed submission", submission.getName());
    }
    
    @SuppressWarnings("resource")
    @Test(expected=NullPointerException.class)
    public void testGetName_nullName() throws IOException {
        final BufferedReader reader = stubTestReader(0);
        final Submission submission = new Submission(reader, null);
        submission.getName();
    }
    
    @Test
    public void testNumLines_zeroLines() throws IOException {
        assertEquals(0, makeTestSubmission(0).getNumLines());
    }
    
    @Test
    public void testNumLines_moreLines() throws IOException {
        assertEquals(2, makeTestSubmission(2).getNumLines());
    }
    
    @Test
    public void testGetLine() throws IOException{
        final Submission submission = makeTestSubmission(3);
        assertEquals("line1", submission.getLine(1));
        assertEquals("line3", submission.getLine(3));
    }
    
    @Test(expected=IndexOutOfBoundsException.class)
    public void testGetLine_argumentTooHigh() throws IOException {
       makeTestSubmission(3).getLine(4);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testGetLine_argumentZeroOrLess() throws IOException {
        final Submission submission = makeTestSubmission(3);
        submission.getLine(0);
    }
    
    @Test
    public void addToDatabase() throws IOException{
        final Map<Integer, Set<LineLocation>> database = new HashMap<>();
        verifyLinesAdded(database);
    }
    
    @Test
    public void addToDatabase_lineAlreadyPresent() throws IOException{
        final Submission submission = makeTestSubmission(3);
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
        
        final Submission testee = makeTestSubmission(3);
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
    private static Submission makeTestSubmission(int numLines)
            throws IOException{
        final BufferedReader reader = stubTestReader(numLines);
        final Submission submission = new Submission(reader);
        verify(reader, times(numLines + 1)).readLine();
        verifyNoMoreInteractions(reader);
        return submission;
    }
    
    private static void verifyLinesAdded(
            final Map<Integer, Set<LineLocation>> database)
                    throws IOException {
        final Map<Integer, Set<LineLocation>> dbBefore =
                copyLineLocationDatabase(database);
        final int numLines = 3;
        final Submission testee = makeTestSubmission(numLines);
        testee.addToDatabase(database);
        
        int expNumEntries = dbBefore.size();
        for(int i = 0; i < numLines; ++i){
            final Integer key = Integer.valueOf(("line"+(i+1)).hashCode());
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
