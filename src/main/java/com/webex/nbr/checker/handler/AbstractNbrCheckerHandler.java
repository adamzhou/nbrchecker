package com.webex.nbr.checker.handler;

import com.webex.nbr.checker.dto.RecordingMP4Info;
import com.webex.nbr.checker.dto.recordingmisc.CameraMP4Info;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by linzhou on 27/12/2017.
 */
abstract public class AbstractNbrCheckerHandler {

    private static final String TOOL_NBRTOOL = "nbrtool";
    private static final String TOOL_FAAC = "faac";
    private static final String TOOL_FFMPEG = "ffmpeg";
    private static final String TOOL_MP4MUXER = "mp4muxer";

    private String toolDir;
    private String outputDir;
    private String rawdataDatFile;
    private String rawdataIdxFile;
    //Pair<L, R>
    //L: *.avc
    //R: *.off
    private List<Pair<String, String>> cameraAVCFileInfoList;
    private String h264cfgFile;
    private List<String> avcToMP4List;
    private List<String> cameraAudioPCMList;
    private List<String> cameraAudioAACList;
    private List<String> fixedFrameRatingMP4List;
    List<CameraMP4Info> cameraMP4InfoList;
    private RecordingMP4Info generatedMP4Info;
    boolean cameraLengthOK = false;

    public boolean isCameraLengthOK() {
        return cameraLengthOK;
    }

    public void setCameraLengthOK(boolean cameraLengthOK) {
        this.cameraLengthOK = cameraLengthOK;
    }

    public RecordingMP4Info getGeneratedMP4Info() {
        return generatedMP4Info;
    }

    public void setGeneratedMP4Info(RecordingMP4Info generatedMP4Info) {
        this.generatedMP4Info = generatedMP4Info;
    }

    public List<CameraMP4Info> getCameraMP4InfoList() {
        return cameraMP4InfoList;
    }

    public void setCameraMP4InfoList(List<CameraMP4Info> cameraMP4InfoList) {
        this.cameraMP4InfoList = cameraMP4InfoList;
    }

    public List<String> getFixedFrameRatingMP4List() {
        return fixedFrameRatingMP4List;
    }

    public void setFixedFrameRatingMP4List(List<String> fixedFrameRatingMP4List) {
        this.fixedFrameRatingMP4List = fixedFrameRatingMP4List;
    }

    public List<String> getCameraAudioAACList() {
        return cameraAudioAACList;
    }

    public void setCameraAudioAACList(List<String> cameraAudioAACList) {
        this.cameraAudioAACList = cameraAudioAACList;
    }

    public List<String> getCameraAudioPCMList() {
        return cameraAudioPCMList;
    }

    public void setCameraAudioPCMList(List<String> cameraAudioPCMList) {
        this.cameraAudioPCMList = cameraAudioPCMList;
    }

    public List<String> getAvcToMP4List() {
        return avcToMP4List;
    }

    public void setAvcToMP4List(List<String> avcToMP4List) {
        this.avcToMP4List = avcToMP4List;
    }

    public String getH264cfgFile() {
        return h264cfgFile;
    }

    public void setH264cfgFile(String h264cfgFile) {
        this.h264cfgFile = h264cfgFile;
    }

    public List<Pair<String, String>> getCameraAVCFileInfoList() {
        return cameraAVCFileInfoList;
    }

    public void setCameraAVCFileInfoList(List<Pair<String, String>> cameraAVCFileInfoList) {
        this.cameraAVCFileInfoList = cameraAVCFileInfoList;
    }

    public String getRawdataDatFile() {
        return rawdataDatFile;
    }

    public void setRawdataDatFile(String rawdataDatFile) {
        this.rawdataDatFile = rawdataDatFile;
    }

    public String getRawdataIdxFile() {
        return rawdataIdxFile;
    }

    public void setRawdataIdxFile(String rawdataIdxFile) {
        this.rawdataIdxFile = rawdataIdxFile;
    }

    public String getToolDir() {
        return toolDir;
    }

    public void setToolDir(String toolDir) {
        this.toolDir = toolDir;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    protected String[] getEnv() {
        String[] envp = new String[1];
        envp[0] = "LD_LIBRARY_PATH=" + toolDir;
        return envp;
    }

    protected String getNbrtoolPath() {
        return toolDir + File.separator + TOOL_NBRTOOL;
    }

    protected String getFfmpegPath() {
        return toolDir + File.separator + TOOL_FFMPEG;
    }

    protected String getFaacPath() {
        return toolDir + File.separator + TOOL_FAAC;
    }

    protected String getMp4muxerPath() {
        return toolDir + File.separator + TOOL_MP4MUXER;
    }

    protected void executeToolCommand(String commandLine, String[] envp) throws Exception {
        try {
            Process pr = Runtime.getRuntime().exec(commandLine, envp);
            try (
                    BufferedReader br = new BufferedReader(new InputStreamReader(pr.getErrorStream()))
            ) {
                String line;
                while ((line = br.readLine()) != null) {
                    //DO NOT OUTPUT
                    //System.out.println(line);
                }
            } catch (IOException e) {
                System.out.println("Failed to read the output of the process." + e.getMessage());
            } finally {
                try {
                    pr.waitFor();
                } catch (InterruptedException e) {
                    System.out.println("The process is interrupted: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to generateAVC: " + commandLine + " " + e.getMessage());
            throw e;
        }
    }

}
