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

    @Override
    public boolean equals(Object o){
        return (o instanceof LineLocation) &&
                this.equals((LineLocation)o);
    }

    public boolean equals(LineLocation o){
        return (this.submission.equals(o.submission)) &&
                (this.lineNum == o.lineNum);
    }

    @Override
    public int hashCode(){
        return this.submission.hashCode() ^ this.lineNum;
    }

    @Override
    public String toString(){
        return String.format(Messages.getString("LineLocation.0"),//$NON-NLS-1$
                Integer.valueOf(this.lineNum), this.submission);
    }
}
