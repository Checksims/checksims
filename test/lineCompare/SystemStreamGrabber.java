package lineCompare;

import static org.mockito.Mockito.mock;

import java.io.InputStream;
import java.io.PrintStream;

final class SystemStreamGrabber implements AutoCloseable {
    final PrintStream out = mock(PrintStream.class);
    final PrintStream err = mock(PrintStream.class);
    final InputStream in = mock(InputStream.class);
    
    final PrintStream sysout = System.out;
    final PrintStream syserr = System.err;
    final InputStream sysin  = System.in;
    
    
    public SystemStreamGrabber(){
        System.setOut(this.out);
        System.setErr(this.err);
        System.setIn(this.in);
    }
    
    public PrintStream out(){
        return this.out;
    }
    public PrintStream err(){
        return this.err;
    }
    public InputStream in(){
        return this.in;
    }

    @Override
    public void close() {
        System.setOut(this.out);
        System.setErr(this.err);
        System.setIn(this.in);
    }
}
