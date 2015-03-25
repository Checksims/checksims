package edu.wpi.checksims.util.file;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Tests for the File Line Reader
 */
public class FileLineReaderTest {
    private static File text1;
    private static File empty;
    private static File sampleC;

    private static final String text1contents = "Test test test\n";

    private static final String sampleCContents = "#include<\"stdio.h\">\n\n/* Prints Hello, World! */\nint main(int argc, char** argv)\n{\n    printf(\"Hello, world!\\n\");\n\n    // Return 0 to make compiler happy\n    return 0;\n}\n";

    @BeforeClass
    public static void setUp() {
        text1 = new File(FileLineReaderTest.class.getResource("text1.txt").getPath());
        empty = new File(FileLineReaderTest.class.getResource("empty.txt").getPath());
        sampleC = new File(FileLineReaderTest.class.getResource("sample.c").getPath());
    }

    @Test(expected = IOException.class)
    public void TestReadNonexistantFile() throws Exception {
        FileLineReader.readFile(new File("does_not_exist.notexist"));
    }

    @Test
    public void TestReadEmptyFile() throws Exception {
        String result = FileLineReader.readFile(empty);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void TestReadSingleLineFile() throws Exception {
        String result = FileLineReader.readFile(text1);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(result, text1contents);
    }

    @Test
    public void TestReadInSimpleProgram() throws Exception {
        String result = FileLineReader.readFile(sampleC);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(result, sampleCContents);
    }
}
