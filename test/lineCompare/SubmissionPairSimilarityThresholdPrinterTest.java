package lineCompare;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

@SuppressWarnings("nls")
public class SubmissionPairSimilarityThresholdPrinterTest {

    @SuppressWarnings("static-method")
    @Test
    public void testReport_1() {
        final double TEST_THRESHOLD = .5;
        final LineSimilarityReporter reporter =
                new SubmissionPairSimilarityThresholdPrinter(TEST_THRESHOLD);

        // set up parameters
        final String[][] submissionLines =
            {{"01", "02", "03", "04", "05"},
             {"01", "02", "04", "05", "06", "07", "08", "09", "10", "11"},
             {"01", "02", "03"}};
        
        try(final SystemStreamGrabber streams = new SystemStreamGrabber()){
            reporter.report(makeMatrix(submissionLines));
            verify(streams.out).println("SUBMISSION\t\tMATCHES\t\tPCT");
            verify(streams.out).println("test1\t\ttest2\t\t80%");
            verify(streams.out).println("test1\t\ttest3\t\t60%");
            verify(streams.out).println("test3\t\ttest2\t\t67%");
            verify(streams.out).println("test3\t\ttest1\t\t100%");
            verifyNoMoreInteractions(streams.out);
            verifyZeroInteractions(streams.err);
            verifyZeroInteractions(streams.in);
        }
    }
    
    @SuppressWarnings("static-method")
    @Test
    public void testReport_2(){
        final double TEST_THRESHOLD = .5;
        final LineSimilarityReporter reporter =
                new SubmissionPairSimilarityThresholdPrinter(TEST_THRESHOLD);
        final String[][] submissionLines = 
            {   {"1", "2", "3"},
                {"1", "2", "4", "5", "6"}};
        final LineSimilarityMatrix  matrix = makeMatrix(submissionLines);
        
        try(final SystemStreamGrabber streams = new SystemStreamGrabber()){
            reporter.report(matrix);
            verify(streams.out).println("SUBMISSION\t\tMATCHES\t\tPCT");
            verify(streams.out).println("test1\t\ttest2\t\t67%");
            verifyNoMoreInteractions(streams.out);
            verifyZeroInteractions(streams.err);
            verifyZeroInteractions(streams.in);
        }
    }
    
    private static LineSimilarityMatrix makeMatrix(String[][] lines){
        final Set<Submission> submissions = new HashSet<>();
        for(int i = 0; i < lines.length; i++){
            submissions.add(new Submission(lines[i], "test"+(i+1)));
        }
        final LineSimilarityMatrix matrix = LineSimilarityMatrix.compute(
                Collections.unmodifiableSet(submissions));
        return matrix;
    }
}
