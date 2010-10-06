package org.dadacoalition.paragraphwrap.test;

import static org.junit.Assert.*;

import org.dadacoalition.paragraphwrap.ParagraphWrapException;
import org.dadacoalition.paragraphwrap.wrapper.DefaultPrefixChecker;
import org.junit.Test;

public class DefaultPrefixCheckerTest {

    @Test
    public void getPrefix() {

        DefaultPrefixChecker dpc = new DefaultPrefixChecker();

        try {

            String paragraph1 = "This is paragraph without any prefix";
            String prefix1 = dpc.getPrefix(paragraph1);
            assertNull("Single line, no prefix", prefix1);

            String paragraph2 = "This is a multiline paragraph \nstill no prefix. \nNo Sir";
            String prefix2 = dpc.getPrefix(paragraph2);
            assertNull("Multi line, no prefix", prefix2);

            String paragraph3 = "    Single line with prefix";
            String prefix3 = dpc.getPrefix(paragraph3);
            String expected3 = "    ";
            assertEquals("Single line with prefix", expected3, prefix3);
            
            String paragraph4 = "    # Single line Perl comment";
            String prefix4 = dpc.getPrefix(paragraph4);
            String expected4 = "    # ";
            assertEquals("Single line with Perl comment prefix", expected4, prefix4);
            
            String paragraph5 = "--Multi line with SQL\n--type of comment\n--on all lines\n";
            String prefix5 = dpc.getPrefix(paragraph5);
            String expected5 = "--";
            assertEquals("Multi line with prefix", expected5, prefix5);
            
            String paragraph6 = "   # Multiline with almost prefix\n # but not quite\n  # equal";
            String prefix6 = dpc.getPrefix(paragraph6);
            assertNull("Multi line with almost prefix, but all lines not equal", prefix6 );

        } catch (ParagraphWrapException e) {
            e.printStackTrace();
        }

    }

}
