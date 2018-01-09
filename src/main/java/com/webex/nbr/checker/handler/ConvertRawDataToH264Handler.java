package com.webex.nbr.checker.handler;

import com.webex.nbr.checker.utils.NbrRawDataUtil;
import com.webex.nbr.checker.dto.recordingmisc.CameraMP4Info;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Step #1
 * 1. Convert raw data(.dat & .idx) to H.264(.avc & .off) with nbrtool
 * 2. Update camera filename(.avc & .off)’s duration part with the last timestamp in .off file
 * 3. Generate h264.cfg
 *
 * h264.cfg format is like following:
 * OutputMP4fileName       /spare/nbr/tmp/workengine/files/ce64b605-50bb-43a5-9c3f-3d0499d5d1d4/camera_2949_12835.mp4
 * InputFileNumber 1
 * InputH264FileName       /spare/nbr/tmp/workengine/files/ce64b605-50bb-43a5-9c3f-3d0499d5d1d4/camera_2949_12835.avc
 * InputtsFileName /spare/nbr/tmp/workengine/files/ce64b605-50bb-43a5-9c3f-3d0499d5d1d4/camera_2949_12835.off
 *
 * Created by linzhou on 27/12/2017.
 */
public class ConvertRawDataToH264Handler extends AbstractNbrCheckerHandler implements NbrCheckerHandler {

    private static final String H264_CONFIG_FILE = "h264.cfg";

    @Override
    public NbrCheckerHandler process() throws Exception {
        generateAVC();
        updateAVCDuration();

        if (checkCameraLengthOK()) {
            setCameraLengthOK(true);
            return null;
        }

        generateH264Cfg();

        ConvertH264ToMP4Handler nextHandler = new ConvertH264ToMP4Handler();
        nextHandler.setRawdataDatFile(getRawdataDatFile());
        nextHandler.setRawdataIdxFile(getRawdataIdxFile());
        nextHandler.setOutputDir(getOutputDir());
        nextHandler.setToolDir(getToolDir());
        nextHandler.setCameraAVCFileInfoList(getCameraAVCFileInfoList());
        nextHandler.setH264cfgFile(getH264cfgFile());
        nextHandler.setAvcToMP4List(getAvcToMP4List());
        nextHandler.setGeneratedMP4Info(getGeneratedMP4Info());
        return nextHandler;
    }

    private boolean checkCameraLengthOK() throws Exception {
        List<CameraMP4Info> existingCameraMP4InfoList = getGeneratedMP4Info().getCameraMP4InfoList();
        List<CameraMP4Info> newCameraAVCInfoList = getNewCameraAVCInfoList();
        if (existingCameraMP4InfoList == null || existingCameraMP4InfoList.isEmpty() ||
                newCameraAVCInfoList == null || newCameraAVCInfoList.isEmpty()) {
            System.out.println("No camera data!");
            throw new Exception("No camera data");
        }

        if (existingCameraMP4InfoList.size() != newCameraAVCInfoList.size()) {
            System.out.println("Camera count is different!");
            throw new Exception("Camera count is different");
        }

        Collections.sort(existingCameraMP4InfoList);
        Collections.sort(newCameraAVCInfoList);

        for (int i = 0; i < existingCameraMP4InfoList.size(); i++) {
            CameraMP4Info existingCameraMP4Info = existingCameraMP4InfoList.get(i);
            CameraMP4Info newCameraAVCInfo = newCameraAVCInfoList.get(i);
            if (NbrRawDataUtil.hasDiff(existingCameraMP4Info.getDuration(), newCameraAVCInfo.getDuration())) {
                return false;
            }
        }
        return true;
    }

    private List<CameraMP4Info> getNewCameraAVCInfoList() {
        List<CameraMP4Info> newCameraAVCInfoList = new ArrayList<>();
        // Pair<L, R>
        // L: AVC file path
        // R: OFF file path
        List<Pair<String, String>> newCameraAVCPairList = getCameraAVCFileInfoList();
        if (newCameraAVCPairList != null && !newCameraAVCPairList.isEmpty()) {
            for (Pair<String, String> newCameraAVCPair : newCameraAVCPairList) {
                newCameraAVCInfoList.add(NbrRawDataUtil.parseCameraAVCInfo(newCameraAVCPair.getLeft()));
            }
            Collections.sort(newCameraAVCInfoList);
        }
        return newCameraAVCInfoList;
    }

    private void generateH264Cfg() throws Exception {
        //Pair<L, R>
        //L: *.avc
        //R: *.off
        List<Pair<String, String>> cameraAVCFileInfoList = getCameraAVCFileInfoList();
        if (cameraAVCFileInfoList == null || cameraAVCFileInfoList.isEmpty()) {
            System.out.println("cameraAVCFileInfoList is null!");
            throw new Exception("Cannot generate h264.cfg");
        }

        File h264cfgFile = new File(getOutputDir() + File.separator + H264_CONFIG_FILE);
        List<String> avcToMP4List = new ArrayList<>();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(h264cfgFile))) {
            StringBuilder sb = new StringBuilder();
            sb.append("OutputMP4fileName       ");
            for (int i = 0; i < cameraAVCFileInfoList.size(); i++) {
                String avcFile = cameraAVCFileInfoList.get(i).getLeft();
                String outputMP4Filename = avcFile.substring(0, avcFile.lastIndexOf(".avc")).concat(".mp4");
                avcToMP4List.add(outputMP4Filename);
                if (i != 0) {
                    sb.append("\t");
                }
                sb.append(outputMP4Filename).append("\n");
            }
            sb.append("InputFileNumber\t").append(cameraAVCFileInfoList.size()).append("\n");
            for (Pair<String, String> cameraAVCFileInfo : cameraAVCFileInfoList) {
                sb.append("InputH264FileName\t").append(cameraAVCFileInfo.getLeft()).append("\n");
                sb.append("InputtsFileName\t").append(cameraAVCFileInfo.getRight()).append("\n");
            }
            writer.write(sb.toString());
        } catch (IOException e) {
            System.out.println("Failed to write test file! e=" + e.getMessage());
            throw e;
        }
        setH264cfgFile(h264cfgFile.getPath());
        setAvcToMP4List(avcToMP4List);
    }

    private void updateAVCDuration() {
        // Pair<L, R>
        // L: AVC file path
        // R: OFF file path
        List<Pair<String, String>> cameraAVCFileInfoList = NbrRawDataUtil.getCameraAVCFileInfo(getOutputDir());
        if (cameraAVCFileInfoList == null) {
            return;
        }

        List<Pair<String, String>> newCameraAVCFileInfoList = new ArrayList<>();
        for (Pair<String, String> cameraAVCFileInfo : cameraAVCFileInfoList) {
            String avcFile = cameraAVCFileInfo.getLeft();
            String offFile = cameraAVCFileInfo.getRight();
            long newDuration = 0;
            try {
                newDuration = NbrRawDataUtil.getLastTimestamp(offFile);
                String newAvcFile = NbrRawDataUtil.replaceDuration(avcFile, newDuration);
                String newOffFile = NbrRawDataUtil.replaceDuration(offFile, newDuration);
                newCameraAVCFileInfoList.add(new ImmutablePair<>(newAvcFile, newOffFile));
            } catch (Exception e) {
                System.out.println("Failed to rename avcFile/offFile: " + avcFile + ", " + offFile);
            }

        }
        setCameraAVCFileInfoList(newCameraAVCFileInfoList);
    }

    private void generateAVC() throws Exception {
        executeToolCommand(getRawdataToAVCCommand(), getEnv());
    }

    private String getRawdataToAVCCommand() {
        StringBuilder sb = new StringBuilder(getNbrtoolPath());
        sb.append(" v ").append(getRawdataIdxFile()).append(" ");
        sb.append(getRawdataDatFile()).append(" ");
        sb.append(getOutputDir()).append(File.separator).append("camera");
        return sb.toString();
    }
}
