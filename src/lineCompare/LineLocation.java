package lineCompare;

public final class LineLocation {
    private final Submission submission;
    private final int lineNum;
    
    public LineLocation(Submission sub, int lnum){
        this.submission = sub;
        this.lineNum = lnum;
    }
    public Submission getSubmission(){
        return this.submission;
    }
    public int getLineNum(){
        return this.lineNum;
    }
}
