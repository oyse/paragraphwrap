package org.dadacoalition.paragraphwrap.handlers;

import org.dadacoalition.paragraphwrap.ParagraphWrapException;
import org.dadacoalition.paragraphwrap.ParagraphWrapLog;
import org.dadacoalition.paragraphwrap.ParagraphWrapUtils;
import org.dadacoalition.paragraphwrap.preferences.PreferenceConstants;
import org.dadacoalition.paragraphwrap.wrapper.DefaultWrapper;
import org.dadacoalition.paragraphwrap.wrapper.IWrapper;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class WrapParagraph extends AbstractHandler {
       
    /**
     * the command has been executed, so extract the needed information
     * from the application context.
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {

        ISelection selection = HandlerUtil.getCurrentSelection(event);

        if (!(selection instanceof TextSelection)) {
            ParagraphWrapLog.debug("No selection");
            return null;
        }

        try {

            AbstractTextEditor textEditor = getTextEditor(event);
            
            if( null == textEditor ){
                throw new ParagraphWrapException("No text editor found");
            }
            
            IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
            if( null == document ){
                throw new ParagraphWrapException("Text editor has no document.");
            }
            
            TextSelection ts = (TextSelection) selection;

            int startline = getStartLine( document, ts, textEditor );
            int endline = getEndLine( document, ts, textEditor );
            
            if( startline > endline ){
                throw new ParagraphWrapException("startline larger than endline");
            }
            
            String lineDelimiter = getLineDelimiter( document, startline );
            String selectedText = getText( document, startline, endline );
            
            IWrapper wrapper = getWrapper();
            String wrappedText = wrapper.wrapText(selectedText, lineDelimiter);
        
            replaceText( document, startline, endline, wrappedText );
        
        } catch ( ParagraphWrapException e ){
            ParagraphWrapLog.logException(e);
        }

        return null;
    }

    /**
     * Determine which line delimiter to when wrapping text. The following rule is used:
     * 1. Check the first line for a line delimiter and if it exists use that.
     * 2. If not check the Workspace preferences for what line separator to use.
     * 3. If none of the other ways work use the Java system property.
     * @param document The current document.
     * @param startline The index of the first line that is selected.
     * @return The String containing the line delimiter to use.
     * @throws ParagraphWrapException 
     */
    private String getLineDelimiter(IDocument document, int startline) throws ParagraphWrapException {

        String lineDelimiter = null;
        try {
            lineDelimiter = document.getLineDelimiter(startline);
        } catch (BadLocationException e) {
            throw new ParagraphWrapException("Start line refered to a bad location", e);
        }
        
        if( null == lineDelimiter ){

            ParagraphWrapLog.debug("First line did not have any line delimiter");

            IPreferencesService prefs = Platform.getPreferencesService();
            lineDelimiter = prefs.getString("org.eclipse.core.runtime", Platform.PREF_LINE_SEPARATOR, "", null);

            if( lineDelimiter.equals("")){
                ParagraphWrapLog.debug("Did not find line delimiter in prefs. Using platform property" );
                lineDelimiter = System.getProperty("line.separator");
            }

        }

        String lineDelimiterCode = "";
        for( int i = 0; i < lineDelimiter.length(); i++ ){
            lineDelimiterCode += "char(" + lineDelimiter.codePointAt(i) + ")";
        }
        ParagraphWrapLog.debug("Line delimiter: '" + lineDelimiterCode + "'");
                
        return lineDelimiter;
    }

    /**
     * Replace the text starting on the startline and ending on the endlline with the wrappedText
     * @param document The current document.
     * @param startline The first line to replace
     * @param endline The last line to replace.
     * @param wrappedText The new text to replace the old text with.
     */
    private void replaceText(IDocument document, int startline, int endline, String wrappedText) {

        try {
            int offset = document.getLineOffset(startline);
            int textlength = totalLineLength( document, startline, endline );
            document.replace(offset, textlength, wrappedText);
            
        } catch (BadLocationException e) {
            ParagraphWrapLog.logException(e);
        }
        
    }

    /**
     * Get the first line to wrap based on the current selection or the caret position if no
     * selection is done.
     * @param document The current document
     * @param ts The current text selection
     * @param editor The current editor
     * @return The 0 based index of the first line in the document to wrap.
     * @throws ParagraphWrapException 
     */
    private int getStartLine(IDocument document, TextSelection ts, AbstractTextEditor editor) throws ParagraphWrapException {

        int startline;
        if( textSelected(ts) ) {
            startline = ts.getStartLine();
        } else {

            int currLine = currentCaretLine(editor, document);
            
            // iterate backwards over lines looking for a completely blank line that indicate
            // the start of a paragraph
            while( currLine > 0 ){
                if( isBlankLine( currLine, document ) ){
                    
                    //The previous read line is not part of the selected text
                    //so increment the counter.
                    currLine++; 
                    break;
                } else {
                    currLine--;
                }
            }
            startline = currLine;            
        }
        
        ParagraphWrapLog.debug("Start line: " + startline);
        return startline;
        
    }
    
    /**
     * Get the last line to wrap based either on the current selection or the caret position if
     * no selection is done.
     * @param document The current document
     * @param ts The current text selection
     * @param editor The current editor
     * @return The 0 based index of the last line in the document to wrap.
     * @throws ParagraphWrapException 
     */
    private int getEndLine(IDocument document, TextSelection ts, AbstractTextEditor editor) throws ParagraphWrapException{
        
        int endline;
        if( textSelected(ts) ){
            endline = ts.getEndLine();
        } else {

            int currLine = currentCaretLine(editor, document);
            int maxLine = document.getNumberOfLines() - 1;
            
            //iterate over all the lines looking for a completely blank line that indicate
            //the end of the current paragraph.
            while( currLine < maxLine ){
                
                if( isBlankLine( currLine, document ) ){
                    //the current line is blank and should not be part of the selected text so
                    //we rewind the counter.
                    currLine--;
                    break;
                } else {
                    currLine++;    
                }
                
            }
            endline = currLine;            
        }
        
        ParagraphWrapLog.debug("End line: " + endline );
        return endline;
    }
    
    /**
     * Check whether a line in the document consist of nothing but whitespace.
     * @param line The 0 based index of the line in the document to check.
     * @param document The current document.
     * @return true if the line is blank. false otherwise.
     * @throws ParagraphWrapException
     */
    private boolean isBlankLine( int line, IDocument document ) throws ParagraphWrapException{
        
        boolean isBlank = false;
        try {
            int offset = document.getLineOffset(line);
            int length = document.getLineLength(line);
            
            String lineText = document.get(offset, length);           
            isBlank = ParagraphWrapUtils.isBlankLine(lineText);
            ParagraphWrapLog.debug("isBlankLine: '" + lineText + "' Result: " + isBlank );            
            
        } catch (BadLocationException e) {
            throw new ParagraphWrapException("The line: " + line + " was a bad location", e);
        }

        return isBlank;
    }
    
    /**
     * Get the selected text.
     * @param document
     * @param startline
     * @param endline
     * @return
     * @throws ParagraphWrapException
     */
    private String getText(IDocument document, int startline, int endline) throws ParagraphWrapException {
        
        String selectedText = "";
        try {

            int startOffset = document.getLineOffset(startline);
            int textlength = totalLineLength( document, startline, endline );
            ParagraphWrapLog.debug( "Offset: " + startOffset + " Length: " + textlength );            
            
            selectedText = document.get(startOffset, textlength);       
            ParagraphWrapLog.debug("Selected text: " + selectedText );
            
        } catch ( BadLocationException e ){
            throw new ParagraphWrapException("Selecting text gave bad location", e);
        }
                
        return selectedText;

    }
    
    /**
     * The the total length of the line start with startline and ending with endline. The 
     * linedelimiter of the endline is not counted as part of the length.
     * 
     * This number is later used to get all the text starting on startline and ending with endline.
     * @param document The current document
     * @param startline The 0 based index of the first line to include in the count.
     * @param endline The 0 based index of the last line to include in the count.
     * @return The number of characters in the lines.
     * @throws BadLocationException
     */
    private int totalLineLength( IDocument document, int startline, int endline ) throws BadLocationException{

        int textlength = 0;
        for( int line = startline; line <= endline; line++ ){
            textlength += document.getLineLength(line);
        }
        
        //the line delimiter of the last line is not part of the selected text to prevent
        //the need of adding it at a later stage.
        String lineDelimiter = document.getLineDelimiter(endline);
        if ( lineDelimiter != null ){
           textlength -= lineDelimiter.length(); 
        }
        
        return textlength;

    }
    

    /**
     * Checks whether the user has actually selected any text or the cursor is
     * just standing on a specific line 
     * @param ts The current selection
     * @return true if some text is selected. false otherwise.
     */
    private boolean textSelected(TextSelection ts) {
        
        if( 0 == ts.getLength() ){
            return false;
        } else {
            return true;
        }
        
    }

    /**
     * Get the current text editor. The TextEditor is needed to get access to IDocument holding
     * the current document.
     * @param event The event that caused the execution of the handler.
     * @return 
     */
    private AbstractTextEditor getTextEditor(ExecutionEvent event) {

        IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
        if (!(editorPart instanceof AbstractTextEditor) && !(editorPart instanceof MultiPageEditorPart)) {
            return null;
        }

        AbstractTextEditor textEditor = null;

        // It seems like a multipage editor can have several text editors. We
        // just pick the first one
        // since it does not seem to be a way to retrieve the active one.
        if (editorPart instanceof MultiPageEditorPart) {
            MultiPageEditorPart mpe = (MultiPageEditorPart) editorPart;
            IEditorPart[] editors = mpe.findEditors(mpe.getEditorInput());
            for (IEditorPart editor : editors) {
                if (editor instanceof AbstractTextEditor) {
                    textEditor = (AbstractTextEditor) editor;
                    ParagraphWrapLog.debug("Found text editor for the MultiPageEditorPart");
                    break;
                }
            }

            if (null == textEditor) {
                ParagraphWrapLog.logger.warning("The MultiPageEditorPart did not have a AbstractTextEditor.");
                return null;
            }
        } else {
            textEditor = (AbstractTextEditor) editorPart;
        }

        return textEditor;
    }
    
    /**
     * Get the current line in the document of the caret position.
     * 
     * The caret position is used when no text has been selected. Using TextSelection does not
     * work correctly in this case since the position will not be updated when the caret moves.
     * @param editor 
     * @param document
     * @return
     * @throws ParagraphWrapException 
     */
    private int currentCaretLine( AbstractTextEditor editor, IDocument document ) throws ParagraphWrapException {
        
        StyledText s = (StyledText) editor.getAdapter(Control.class);
        
        if( s == null ){
            throw new ParagraphWrapException("Could not get the styled text control. Cannot get caret line");
        }
        
        int caretOffset = s.getCaretOffset();
        int caretLine = 0;
        try {
            caretLine = document.getLineOfOffset(caretOffset);
        } catch (BadLocationException e) {
            throw new ParagraphWrapException("Caret offset referred to bad line", e);
        }
        
        ParagraphWrapLog.debug("Current caret line: " + caretLine);
        return caretLine;
        
        
    }
    
    private IWrapper getWrapper(){

        int columnWidth = getColumnWidth();        
        return new DefaultWrapper(columnWidth); 
    }

    /**
     * Get the column width to use as maximum width based on the current preferences.
     * @return The column width
     */
    private int getColumnWidth(){

        IPreferencesService prefs = Platform.getPreferencesService();
        boolean usePrintMargin = prefs.getBoolean("org.dadacoalition.paragraphwrap", PreferenceConstants.USE_PRINT_MARGIN, false, null);
        
        int columnWidth; 
        if( usePrintMargin ){
            columnWidth = prefs.getInt("org.eclipse.ui.editors", "printMarginColumn", 80, null);
        } else {
            columnWidth = prefs.getInt("org.dadacoalition.paragraphwrap", PreferenceConstants.COLUMN_WIDTH, 80, null);
        }
        
        return columnWidth;
        
    }
    
 

}
