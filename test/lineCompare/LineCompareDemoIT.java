package lineCompare;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.io.InputStream;
import java.io.PrintStream;
import java.security.Permission;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings({"nls", "static-method"})
public class LineCompareDemoIT {
    private final PrintStream sysErr = System.err;
    private final InputStream sysIn = System.in;
    private final PrintStream sysOut = System.out;
    private PrintStream err;
    private PrintStream out;
    private InputStream in;
    
    private class ExpectedExitException extends SecurityException{
        private static final long serialVersionUID = 1L;
        public ExpectedExitException(){}
    }
   
    @Before
    public void mockStreams(){
        this.err = mock(PrintStream.class);
        this.out = mock(PrintStream.class);
        this.in = mock(InputStream.class);
        System.setErr(this.err);
        System.setOut(this.out);
        System.setIn(this.in);
    }
    
    @Before
    public void setSecurityManager(){
        System.setSecurityManager(new SecurityManager(){
            @Override
            public void checkPermission(Permission perm){
                // allow
            }
            @Override
            public void checkPermission(Permission perm, Object context){
                // allow
            }
            @Override
            public void checkExit(int status){
                super.checkExit(status);
                throw new ExpectedExitException();
            }
        });
    }
    
    @After
    public void unsetSecurityManager(){
        System.setSecurityManager(null);
    }
    
    @After
    public void restoreStreams(){
        System.setErr(this.sysErr);
        System.setOut(this.sysOut);
        System.setIn(this.sysIn);
    }
    
    // this is only here for code coverage
    @SuppressWarnings("unused")
    @Test
    public void allocTest(){
        new LineCompareDemo();
    }
    
    @Test
    public void testMain_tenMatches() {
        final String[] args = {"bin/test0", "bin/test1"};
        LineCompareDemo.main(args);
        verify(this.out).println("10 lines similar to others");
        verifyNoMoreInteractions(this.out);
        verifyZeroInteractions(this.err);
        verifyZeroInteractions(this.in);
    }
    
    @Test
    public void testMain_fiveMatches(){
        final String[] args = {"bin/test0", "bin/test2"};
        LineCompareDemo.main(args);
        verify(this.out).println("5 lines similar to others");
        verifyNoMoreInteractions(this.out);
        verifyZeroInteractions(this.err);
        verifyZeroInteractions(this.in);
    }
    
    @Test(expected=ExpectedExitException.class)
    public void testMain_invalidFile(){
        final String[] args = {"bin/test0", "bin/thereisnofilehere"};
        LineCompareDemo.main(args);
        verify(this.err).println("No such file: bin/thereisnofilehere");
        verifyNoMoreInteractions(this.out);
        verifyZeroInteractions(this.err);
        verifyZeroInteractions(this.in);
    }
}
