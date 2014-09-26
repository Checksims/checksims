package lineCompare;

import java.util.Map;
import java.util.Set;

public class SubmissionPairSimilarityThresholdPrinter implements
        LineSimilarityReporter {
    private final double threshold;

    public SubmissionPairSimilarityThresholdPrinter(double threshold) {
        this.threshold = threshold;
//        if(threshold < 0. || threshold >= 1.){
//            throw new UnsupportedOperationException("threshold must be between 0 and 1");
//        }
    }

    @Override
    public void report(LineSimilarityMatrix similarities) {
        System.out.println(Messages.getString(
                "SubmissionPairSimilarityThresholdPrinter.0")); //$NON-NLS-1$
        final double th = this.threshold;
        final LineSimilarityMatrix.EntryVisitor visitor =
                new LineSimilarityMatrix.EntryVisitor() {
                    @Override
                    public void visit(Submission sub, Submission other,
                            Map<LineLocation, Set<LineLocation>> similarLines) {
                        final double proportion =
                                (double)similarLines.size() / (double)sub.getNumLines(); 
                        if(!sub.equals(other) && proportion > th){
                            System.out.println(String.format(Messages.getString("SubmissionPairSimilarityThresholdPrinter.1"), //$NON-NLS-1$
                                    sub, other, Double.valueOf(proportion*100)));
                        }
                        
                    }
                };
                similarities.visitAllEntries(visitor);
    }
}
