package com.webex.nbr.checker.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * Created by linzhou on 28/12/2017.
 */
public class AudioGenerationUtil {
    public static void generatePCM(String filename, int durationInMS, int freqHZ, int sampleSize) throws Exception {
        if (StringUtils.isBlank(filename) || durationInMS <= 0 || freqHZ <= 0 || sampleSize <= 0) {
            System.out.println("Failed to generatePCM because of the wrong parameter!");
            throw new Exception("wrong input");
        }

        File file = new File(filename);
        int unitForSampleSize;
        switch (sampleSize) {
            case 8:
                unitForSampleSize = 1;
                break;
            case 16:
                unitForSampleSize = 2;
                break;
            case 24:
                unitForSampleSize = 3;
                break;
            case 32:
                unitForSampleSize = 4;
                break;
            default:
                System.out.println("sampleSize is invalid: " + sampleSize);
                throw new Exception("invalid sampleSize");
        }
        int length = durationInMS * (freqHZ/1000) * sampleSize * unitForSampleSize;
        byte[] data = new byte[length];
        for (int i = 0; i < length; i++) {
            data[i] = '0';
        }
        FileUtils.writeByteArrayToFile(file, data);
    }
}
