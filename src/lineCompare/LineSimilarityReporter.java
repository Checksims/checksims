package lineCompare;

import java.util.Map;
import java.util.Set;

public interface LineSimilarityReporter {
    /** Report the provided software similarities to the user. Promises not to
     * change the similarities argument, but you should pass an unmodifiable
     * map just to make sure.
     * 
     * TODO create a class to abstract away some of these generics
     * 
     * @param similarities the matrix of similar lines to report;
     */
    void report(LineSimilarityMatrix similarities);
}
