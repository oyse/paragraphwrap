package org.dadacoalition.paragraphwrap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParagraphWrapUtils {

    public static Pattern blankLine = Pattern.compile("^\\p{Space}+$");

    /**
     * @param line The line to check.
     * @return true if the line only consists of whitespace. false otherwise.
     */
    public static boolean isBlankLine( String line ){

        Matcher m = blankLine.matcher(line);
        if( m.matches() ){
            return true;
        } else {
            return false;
        }
    }
    
}
