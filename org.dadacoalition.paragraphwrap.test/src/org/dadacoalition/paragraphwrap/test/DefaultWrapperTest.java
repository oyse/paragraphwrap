package org.dadacoalition.paragraphwrap.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.dadacoalition.paragraphwrap.wrapper.DefaultWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class DefaultWrapperTest {

    private static final String TEST_FILES_DIR = "default-wrapper-files/";
    private static final int MAX_LENGTH = 40;
    
    private DefaultWrapper wrapper;
    
    private String textToWrap;
    private String expectedText;
    private String testname;
    
    public DefaultWrapperTest( String testname, String textToWrap, String expectedText ){
        this.testname = testname;
        this.textToWrap = textToWrap;
        this.expectedText = expectedText;
        
        wrapper = new DefaultWrapper( MAX_LENGTH );
    }
    
    @Parameters
    public static Collection<Object[]> readTests() {

        String[] testNames = {
                "singleline-no-wrap",
                "singleline-wrap",
                "multiline-no-longlines",
                "multiline-wrap",
                "multiple-paragraphs"
        };

        Collection<Object[]> testCases = new ArrayList<Object[]>();
        for (String name : testNames) {
            String wrapFile = name + ".txt";
            String expectedFile = name + "-expected.txt";

            String textToWrap = TestUtils.readFile(TEST_FILES_DIR + wrapFile);
            String expectedText = TestUtils.readFile(TEST_FILES_DIR + expectedFile);
            
            Object[] testcase = { name, textToWrap, expectedText };
            testCases.add(testcase);
        }

        return testCases;

    }
    
    @Test
    public void checkEquality(){
        
        String result = wrapper.wrapText(this.textToWrap, "\n");
        
        assertEquals( testname, this.expectedText, result );
        
        
    }
    
}
