package org.dadacoalition.paragraphwrap.wrapper;

import org.dadacoalition.paragraphwrap.ParagraphWrapException;

/**
 * An abstract base class for all PrefixCheckers
 * 
 * Prefix checkers are used to check if a paragraph has a prefix and if it has
 * one what the prefix is.
 * 
 * The paragraph prefix is used to ensure that any new lines added are properly
 * indented and that they have comment characters etc.
 * 
 * @author oysteto
 * 
 */
public abstract class AbstractPrefixChecker {

    /**
     * Get the prefix for a paragraph.
     * 
     * @param paragraph
     *            The paragraph to check
     * @return Returns the prefix used for the paragraph. Returns null if no
     *         prefix is present
     * @throws ParagraphWrapException 
     */
    public abstract String getPrefix(String paragraph) throws ParagraphWrapException;

}
