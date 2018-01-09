package com.webex.nbr.checker.utils;

import com.webex.nbr.checker.dto.recordingmisc.CameraMP4Info;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.*;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by linzhou on 18/12/2017.
 */
public class NbrRawDataUtilTest {
    @Test
    public void testParseCameraMP4Info() {
        String filePath = "/tmp/adam/camera_1330_385702.mp4";
        CameraMP4Info cameraMP4Info = NbrRawDataUtil.parseCameraMP4Info(filePath);
        assertTrue(cameraMP4Info != null);
        assertEquals(1330, cameraMP4Info.getOffset().longValue());
        assertEquals(385702, cameraMP4Info.getDuration().longValue());
        assertTrue(filePath.equals(cameraMP4Info.getFilePath()));
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
        writeTestFile(new File(testDirFile + "/wbxmcsr_173.36.202.50_82016146365089351_173.36.203.87_1513932033_21_268435457_1513932033877.dat"));
        writeTestFile(new File(testDirFile + "/wbxmcsr_173.36.202.50_82016146365089351_173.36.203.87_1513932033_21_268435457_1513932033877.idx"));

        //check directory
        FilenameFilter recordingCameraDataFilter = NbrRawDataUtil.getRecordingCameraDataFilter();
        File[] listOfTestFiles = testDirFile.listFiles(recordingCameraDataFilter);

        try {
            FileUtils.deleteDirectory(new File(testRoot));
        } catch (IOException e) {
            System.out.println("Failed to delete test directory: " + testRoot);
        }

        if (listOfTestFiles != null) {
            for (File file : listOfTestFiles) {
                System.out.println(file.getAbsoluteFile());
            }
            assertEquals(4, listOfTestFiles.length);
        } else {
            fail("Did not find file!");
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

    @Test
    public void testGetOffsetFromAVCFile() {
        String avcFile = "camera_1328_18738.avc";
        long expectedOffset = 1328;
        assertEquals(expectedOffset, NbrRawDataUtil.getOffsetFromAVCFile(avcFile));
    }

    @Test
    public void testAvcFileToOff() {
        String avcFile = "camera_1328_18738.avc";
        String expectedOffFile = "camera_1328_18738.off";
        assertTrue(expectedOffFile.equals(NbrRawDataUtil.avcFileToOff(avcFile)));
    }

    @Test
    public void testReplaceDurationInAvcOffFilename() {
        String originalFilename = "camera_1328_18738.avc";
        long newDuration = 66666;
        String expectedNewFilename = "camera_1328_66666.avc";
        try {
            assertTrue(expectedNewFilename.equals(NbrRawDataUtil.replaceDurationInAvcOffFilename(originalFilename, newDuration)));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetDurationFromMP4File() {
        String mp4File1 = "/root/camera_1111_1000.mp4";
        long expected1 = 1000;
        String mp4File2 = "camera_222_3333.mp4";
        long expected2 = 3333;
        String mp4File3 = "camera.mp4";
        String mp4File4 = "";

        try {
            assertEquals(expected1, NbrRawDataUtil.getDurationFromMP4File(mp4File1));
        } catch (Exception e) {
            fail();
        }
        try {
            assertEquals(expected2, NbrRawDataUtil.getDurationFromMP4File(mp4File2));
        } catch (Exception e) {
            fail();
        }
        try {
            NbrRawDataUtil.getDurationFromMP4File(mp4File3);
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("bad input"));
        }
        try {
            NbrRawDataUtil.getDurationFromMP4File(mp4File4);
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("bad input"));
        }
    }
}
