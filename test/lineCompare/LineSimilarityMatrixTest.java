package lineCompare;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

@SuppressWarnings("nls")
public class LineSimilarityMatrixTest {

    // set up submissions
    private static final String[][] lines1 =
        {   {"line1", "line2", "line3"},
            {"line2", "line3"},
            {"line1", "line4"}};

    /* We are expecting the following number of similar lines:
     *      sub0 sub1 sub2
     * sub0    3    2    1
     * sub1    2    2    0
     * sub2    1    0    2
     */
    
    private static Set<Submission> makeTestSubmissions(String[][] lines){
        final Set<Submission> submissions = new HashSet<>();
        for(int i = 0; i < lines.length; i++){
            submissions.add(new Submission(lines[i], "sub"+i));
        }
        return(Collections.unmodifiableSet(submissions));
    }
    
    @SuppressWarnings("static-method")
    @Test
    public void testGetSubmissions(){
        final Set<Submission> submissions = makeTestSubmissions(lines1);
        final LineSimilarityMatrix matrix = LineSimilarityMatrix.compute(submissions);
        final List<Submission> received = Collections.unmodifiableList(matrix.getSubmissions());
        assertEquals(submissions.size(), received.size());
        assertTrue(submissions.containsAll(received));
    }
    
    @SuppressWarnings("static-method")
    @Test
    public void testToString_1(){
        final Set<Submission> submissions = makeTestSubmissions(lines1);
        final LineSimilarityMatrix matrix = LineSimilarityMatrix.compute(submissions);
        assertEquals("LineSimilarityMatrix, 3 submissions", matrix.toString());
    }
    
    @SuppressWarnings("static-method")
    @Test
    public void testToString_2(){
        final String[][] args = {};
        final LineSimilarityMatrix matrix =
                LineSimilarityMatrix.compute(makeTestSubmissions(args));
        assertEquals("LineSimilarityMatrix, 0 submissions", matrix.toString());
    }
    
    @SuppressWarnings("static-method")
    @Test
    public void testVisitAllEntries(){
        final Set<Submission> submissions = makeTestSubmissions(lines1);
        final LineSimilarityMatrix matrix = LineSimilarityMatrix.compute(submissions);
        final LineSimilarityMatrix.EntryVisitor checker =
                mock(LineSimilarityMatrix.EntryVisitor.class);
        final LineSimilarityMatrix.EntryVisitor visitor =
                new LineSimilarityMatrix.EntryVisitor() {
            @Override
            public void visit(Submission sub, Submission other,
                    Map<LineLocation, Set<LineLocation>> similarLines) {
                checker.visit(sub, other, similarLines);
            }
        };
        matrix.visitAllEntries(visitor);
        for(Submission s1 : submissions){
            for(Submission s2 : submissions){
                final Map<Integer, Set<LineLocation>> database = new HashMap<>();
                s2.addToDatabase(database);
                verify(checker).visit(s1, s2, s1.getSimilarLines(database));
            }
        }
    }
}
