package com.webex.nbr.checker;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by linzhou on 26/12/2017.
 */
public class NbrCheckExecutorTest {
    @Test
    public void testInit() {
        String workDir = "/tmp/webex-nbr-checker";
        try {
            //clean up the temporary directory
            File file = new File(workDir);
            if (file.exists()) {
                FileUtils.forceDelete(file);
            }

            NbrCheckExecutor executor = NbrCheckExecutor.getInstance(workDir);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetNameFromPath() {
        String input1 = "/nbrtoolsimulator/extractor.so";
        String expected1 = "extractor.so";
        String input2 = "extractor.so";
        String expected2 = "extractor.so";
        String input3 = "  ";
        String expected3 = "  ";

        assertEquals(expected1, NbrCheckExecutor.getNameFromPath(input1));
        assertEquals(expected2, NbrCheckExecutor.getNameFromPath(input2));
        assertEquals(expected3, NbrCheckExecutor.getNameFromPath(input3));
    }
}
