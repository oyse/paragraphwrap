package org.dadacoalition.paragraphwrap;

/**
 * Exception class that is used for all exceptions that happen during the execution the ParagraphWrap
 * handler.
 * 
 * The idea is that all checked exceptions will be caught at the lowest level and if they cannot be
 * handled at the level a new ParagraphWrapExeception will be thrown and caugt at the outermost
 * level of the handler.
 * @author oysteto
 *
 */
public class ParagraphWrapException extends Exception {

    public ParagraphWrapException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParagraphWrapException(String message) {
        super(message);
    }
    
    
    private static final long serialVersionUID = 1L;
    
}
