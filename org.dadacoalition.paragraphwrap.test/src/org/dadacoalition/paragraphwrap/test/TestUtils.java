package org.dadacoalition.paragraphwrap.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TestUtils {
    
    /**
     * @param filename The name of the file to read.
     * @return The contents of the file as a string.
     */
    public static String readFile( String filename ) {
        
        Scanner scanner;
        try {
            scanner = new Scanner( new File(filename) );
        } catch (FileNotFoundException e) {     
            throw new RuntimeException(e);
        }
        
        String content = "";
        boolean first = true;
        while( scanner.hasNextLine() ){
            if( first ){
                first = false;
            } else {
                content += "\n";
            }
            content += scanner.nextLine();
        }
        
        return content;     
    }   
}
