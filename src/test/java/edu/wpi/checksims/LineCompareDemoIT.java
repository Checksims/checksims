package edu.wpi.checksims;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.security.Permission;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings({"nls", "static-method"})
public class LineCompareDemoIT {
    private SystemStreamGrabber streams;
    
    private class ExpectedExitException extends SecurityException{
        private static final long serialVersionUID = 1L;
        public ExpectedExitException(){}
    }
   
    @Before
    public void mockStreams(){
        this.streams = new SystemStreamGrabber();
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
        this.streams.close();
    }
    
    // this.streams.is only here for code coverage
    @SuppressWarnings("unused")
    @Test
    public void allocTest(){
        new LineCompareDemo();
    }
    
    //@Test TODO add mode for this
    public void testMain_twoFiles(){
        final String[] args = {"bin/test0", "bin/test1"};
        LineCompareDemo.main(args);
        verify(this.streams.out).print(
                "                         test0          test1\n" +
                "          test0              *           0.10\n" +
                "          test1           0.77              *\n");
        verifyNoMoreInteractions(this.streams.out);
        verifyZeroInteractions(this.streams.err);
        verifyZeroInteractions(this.streams.in);
    }
    
    @Test
    public void testMain_moreFiles(){
        final String[] args = {"bin/test0", "bin/test1", "bin/test2"};
        LineCompareDemo.main(args);
        verify(this.streams.out).print(
                "                         test0          test1          test2\n" +
                "          test0              *           0.10           0.05\n" +
                "          test1           0.77              *           0.00\n" +
                "          test2           1.00            0.00             *\n");
        verifyNoMoreInteractions(this.streams.out);
        verifyZeroInteractions(this.streams.err);
        verifyZeroInteractions(this.streams.in);
    }
    
    // @Test
    public void testMain_tenMatches() {
        final String[] args = {"bin/test0", "bin/test1"};
        LineCompareDemo.main(args);
        verify(this.streams.out).println("10 lines similar to others");
        verifyNoMoreInteractions(this.streams.out);
        verifyZeroInteractions(this.streams.err);
        verifyZeroInteractions(this.streams.in);
    }
    
    // @Test
    public void testMain_fiveMatches(){
        final String[] args = {"bin/test0", "bin/test2"};
        LineCompareDemo.main(args);
        verify(this.streams.out).println("5 lines similar to others");
        verifyNoMoreInteractions(this.streams.out);
        verifyZeroInteractions(this.streams.err);
        verifyZeroInteractions(this.streams.in);
    }
    
    @Test // TODO figure out a way to call System.exit without confusing EclEmma
    public void testMain_invalidFile(){
        final String[] args = {"bin/test0", "bin/thereisnofilehere"};
        LineCompareDemo.main(args);
        verify(this.streams.err).println("No such file: bin/thereisnofilehere");
        verifyNoMoreInteractions(this.streams.out);
        verifyZeroInteractions(this.streams.err);
        verifyZeroInteractions(this.streams.in);
    }
}
