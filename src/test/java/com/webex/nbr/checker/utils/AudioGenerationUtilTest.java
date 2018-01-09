package com.webex.nbr.checker.utils;

import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * Created by linzhou on 03/01/2018.
 */
public class AudioGenerationUtilTest {
    @Test
    public void testGeneratePCM() {
        try {
            AudioGenerationUtil.generatePCM("/tmp/test.pcm", 10000, 16000, 16);
        } catch (Exception e) {
            fail();
        }
    }
}
