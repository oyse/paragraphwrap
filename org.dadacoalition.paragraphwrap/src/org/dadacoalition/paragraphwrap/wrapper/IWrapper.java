package org.dadacoalition.paragraphwrap.wrapper;

import org.dadacoalition.paragraphwrap.ParagraphWrapException;

/**
 * Interface for classes that implement wrapping of text.
 * @author oysteto
 *
 */
public interface IWrapper {

    /**
     * Wrap the supplied text.
     * @param textToWrap The text that should be wrapped.
     * @param lineDelimiter The line delimiter that should be used for new lines.
     * @return The wrapped text.
     * @throws ParagraphWrapException 
     */
    public String wrapText( String textToWrap, String lineDelimiter ) throws ParagraphWrapException;
    
}
