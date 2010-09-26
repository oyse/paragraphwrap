package org.dadacoalition.paragraphwrap.wrapper;

public abstract class AbstractWrapper implements IWrapper {

    public static final String NEWLINE_REGEX = "\\n|\\r\\n|\\r";
    
    protected int maxWidth;
    
    public AbstractWrapper( int maxWidth ){
        this.maxWidth = maxWidth;
    }

}
