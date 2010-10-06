package org.dadacoalition.paragraphwrap.wrapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dadacoalition.paragraphwrap.ParagraphWrapException;

/**
 * Default prefix checker that will recognise lines starting with any mix of
 * spaces '--', '#' and '//' as a prefix.
 * 
 * @author oysteto
 * 
 */
public class DefaultPrefixChecker extends AbstractPrefixChecker {
    
    public static final String NEWLINE_REGEX = "\\n|\\r\\n|\\r";
    
    private Pattern prefixPattern = Pattern.compile( "^((\\s|#|//|--)+).*?");
    
    @Override
    public String getPrefix(String paragraph) throws ParagraphWrapException {

        String[] lines = paragraph.split( NEWLINE_REGEX );
        
        if( 0 == lines.length ){
            throw new ParagraphWrapException( "Paragraph has zero lines. Should not be possible." );
        }
        
        // first line sets the prefix
        String prefix;
        Matcher firstLineMatcher = prefixPattern.matcher( lines[0] );
        if( firstLineMatcher.matches() ){
            prefix = firstLineMatcher.group(1);
        } else {            
            return null;
        }
        
        // checks line 1 again, but no harm done
        for( String line : lines ){
            if( !line.startsWith(prefix) ){
                return null;
            }
        }
        
        return prefix;
    }

}
