package org.dadacoalition.paragraphwrap.wrapper;

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
    public String wrapText(String textToWrap, String lineDelimiter) {
       
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
    
    private String wrapParagraph( String paragraph, String lineDelimiter ){

        String wrappedText = "";
        
        //remove all newlines so that the paragraph can be treated as one long string
        paragraph = paragraph.replaceAll(NEWLINE_REGEX, " ");
        
        char[] characters = paragraph.toCharArray();
        String currentWord = "";
        String previousSpace = "";
        int currentColumn = 0;
        for ( int i = 0; i < characters.length; i++ ){
            
            if( currentColumn == maxWidth ){
                wrappedText += lineDelimiter;
                currentColumn = currentWord.length();
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
        wrappedText += previousSpace + currentWord;
        
        return wrappedText;        
        
    }

}
