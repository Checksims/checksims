package lineCompare;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

@SuppressWarnings({"nls", "static-method"})
public class LineLocationTest {
    @Test
    public void testAccessors(){
        final Submission submission = mock(Submission.class);
        final LineLocation testee = new LineLocation(submission, 5);
        assertEquals(submission, testee.getSubmission());
        assertEquals(Integer.valueOf(5), Integer.valueOf(testee.getLineNum()));
    }
    
    @Test
    public void testToString(){
        final Submission sub = mock(Submission.class);
        when(sub.toString()).thenReturn("submission");
        final LineLocation testee = new LineLocation(sub, 27);
        assertEquals("[Line 27 of submission]", testee.toString());
    }
    
    @Test
    public void testEquals_identical(){
        final Submission submission = mock(Submission.class);
        final Object l1 = new LineLocation(submission, 16);
        final Object l2 = new LineLocation(submission, 16);
        assertTrue(l1 != l2);
        assertTrue(l1.equals(l2));
        assertTrue(l2.equals(l1));
    }
    
    @Test
    public void testEquals_differentTypes(){
        final Submission submission = mock(Submission.class);
        final Object o1 = new LineLocation(submission, 134);
        final Object o2 = new Object();
        assertFalse(o1.equals(o2));
        assertFalse(o2.equals(o1));
    }
    
    @Test
    public void testEquals_differentSubmissions(){
        final Submission s1 = mock(Submission.class);
        final Submission s2 = mock(Submission.class);
        final Object o1 = new LineLocation(s1, 18);
        final Object o2 = new LineLocation(s2, 18);
        assertFalse(o1.equals(o2));
        assertFalse(o2.equals(o1));
    }
    
    @Test
    public void testEquals_differentLineNums(){
        final Submission sub = mock(Submission.class);
        final Object o1 = new LineLocation(sub, 18);
        final Object o2 = new LineLocation(sub, 19);
        assertFalse(o1.equals(o2));
        assertFalse(o2.equals(o1));
    }
}
