package org.dadacoalition.paragraphwrap.wrapper;

import org.dadacoalition.paragraphwrap.ParagraphWrapException;

/**
 * The default implementation for text wrapping. Used when not a specific wrapper exists for the
 * current file type. 
 * @author oysteto
 *
 */
public class DefaultWrapper extends AbstractWrapper {
    
    public DefaultWrapper( int maxWidth ){
        super(maxWidth);
    }
    
    /* (non-Javadoc)
     * @see org.dadacoalition.paragraphwrap.wrapper.IWrapper#wrapText(java.lang.String)
     */
    public String wrapText(String textToWrap, String lineDelimiter) throws ParagraphWrapException {
       
        String wrappedText = "";
        String[] paragraphs = textToWrap.split("\\n\\n|\\r\\r|\\r\\n\\r\\n");

        boolean first = true;
        for( String paragraph : paragraphs ){
            
            if( first ){
                first = false;
            } else {
                wrappedText += lineDelimiter + lineDelimiter;
            }

            wrappedText += wrapParagraph(paragraph, lineDelimiter);
            
        }        
        
        return wrappedText;
    }
    
    /**
     * Wrap the text in a single paragraph
     * @param paragraph The paragraph to be wrapped
     * @param lineDelimiter The line delimiter that should be used when concating all the wrapped lines.
     * @return
     * @throws ParagraphWrapException 
     */
    private String wrapParagraph( String paragraph, String lineDelimiter ) throws ParagraphWrapException{

        
        DefaultPrefixChecker dpc = new DefaultPrefixChecker();
        String prefix = dpc.getPrefix(paragraph);
        
        // when we have prefix the prefix must first be removed before added again later.
        if( prefix != null ){
            String[] lines = paragraph.split(NEWLINE_REGEX);
            paragraph = "";
            for( String line : lines ){
                paragraph += line.substring(prefix.length()) + lineDelimiter; 
            }            
        } else {
            prefix = "";
        }


        //remove all newlines so that the paragraph can be treated as one long string
        paragraph = paragraph.replaceAll(NEWLINE_REGEX, " ");

        int prefixLength = prefix.length();
        String wrappedText = prefix;        
        char[] characters = paragraph.toCharArray();
        String currentWord = "";
        String previousSpace = "";
        int currentColumn = prefixLength;
        for ( int i = 0; i < characters.length; i++ ){
            
            if( currentColumn == maxWidth ){
                wrappedText += lineDelimiter + prefix;
                currentColumn = currentWord.length() + prefixLength;
                previousSpace = "";
            }
            
            String character = "" + characters[i]; 
            if( !character.matches("\\p{Space}") ){
                currentWord += character;
            } else {
                wrappedText += previousSpace + currentWord;
                currentWord = "";
                previousSpace = character;
            }
            currentColumn++;
        }
        
        if( currentWord != "" ){
            wrappedText += previousSpace + currentWord;
        }
        
        return wrappedText;        
        
    }
    
    

}
