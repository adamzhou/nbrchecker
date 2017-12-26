package com.webex.nbr.checker;

import com.webex.nbr.checker.dto.recordingmisc.CameraMP4Info;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.*;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by linzhou on 18/12/2017.
 */
public class NbrRawDataUtilTest {
    @Test
    public void testParseCameraMP4Info() {
        CameraMP4Info cameraMP4Info = NbrRawDataUtil.parseCameraMP4Info("camera_1330_385702.mp4");
        assertTrue(cameraMP4Info != null);
        assertEquals(1330, cameraMP4Info.getOffset().longValue());
        assertEquals(385702, cameraMP4Info.getDuration().longValue());
    }

    @Test
    public void testGetTimestampFromCameraFile() {
        String cameraFilename = "wbxmcsr_66.163.58.17_81170415328693534_2918384125_1513119414_21_268435457_1513119415085.idx";
        String expectedTimestamp = "1513119414";
        assertEquals(expectedTimestamp, NbrRawDataUtil.getTimestampFromCameraFile(cameraFilename));
    }

    @Test
    public void testRecordingCameraDataFilter() {
        //prepare the test files
        String testRoot = "/tmp/testRecordingCameraDataFilter";
        String testDir = testRoot + File.separator + UUID.randomUUID();
        File testDirFile = new File(testDir);
        testDirFile.mkdirs();
        writeTestFile(new File(testDirFile + "/wbxmcsr_66.163.58.17_81170415328693534_2918384125_1513119414_21_268435457_1513119415085.dat"));
        writeTestFile(new File(testDirFile + "/wbxmcsr_66.163.58.17_81170415328693534_2918384125_1513119414_21_268435457_1513119415085.idx"));

        //check directory
        FilenameFilter recordingCameraDataFilter = NbrRawDataUtil.getRecordingCameraDataFilter();
        File[] listOfTestFiles = testDirFile.listFiles(recordingCameraDataFilter);
        if (listOfTestFiles != null) {
            for (File file : listOfTestFiles) {
                System.out.println(file.getAbsoluteFile());
            }
        }
        try {
            FileUtils.deleteDirectory(new File(testRoot));
        } catch (IOException e) {
            System.out.println("Failed to delete test directory: " + testRoot);
        }
    }

    private void writeTestFile(File file) {
        if (file == null) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("test");
        } catch (IOException e) {
            System.out.println("Failed to write test file! e=" + e.getMessage());
        }
    }
}
