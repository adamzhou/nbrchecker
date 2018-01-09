package com.webex.nbr.checker;

import com.webex.nbr.checker.dto.recordingmisc.CameraMP4Info;

import java.util.List;

/**
 * NbrCheckRecording is in Recording level
 *
 * Created by linzhou on 15/12/2017.
 */
public class NbrCheckRecording {
    private String rawDataPath;
    private String miscXMLFile;
    private String existingRecordingMP4XML;
    private List<CameraMP4Info> existingCameraMP4InfoList;
    private List<CameraMP4Info> newCameraMP4InfoList;
    private boolean isCameraLengthOK;
    private String result;
    private String errorMsg = null;

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public boolean isCameraLengthOK() {
        return isCameraLengthOK;
    }

    public void setCameraLengthOK(boolean cameraLengthOK) {
        isCameraLengthOK = cameraLengthOK;
    }

    public String getRawDataPath() {
        return rawDataPath;
    }

    public void setRawDataPath(String rawDataPath) {
        this.rawDataPath = rawDataPath;
    }

    public String getMiscXMLFile() {
        return miscXMLFile;
    }

    public void setMiscXMLFile(String miscXMLFile) {
        this.miscXMLFile = miscXMLFile;
    }

    public String getExistingRecordingMP4XML() {
        return existingRecordingMP4XML;
    }

    public void setExistingRecordingMP4XML(String existingRecordingMP4XML) {
        this.existingRecordingMP4XML = existingRecordingMP4XML;
    }

    public List<CameraMP4Info> getExistingCameraMP4InfoList() {
        return existingCameraMP4InfoList;
    }

    public void setExistingCameraMP4InfoList(List<CameraMP4Info> existingCameraMP4InfoList) {
        this.existingCameraMP4InfoList = existingCameraMP4InfoList;
    }

    public List<CameraMP4Info> getNewCameraMP4InfoList() {
        return newCameraMP4InfoList;
    }

    public void setNewCameraMP4InfoList(List<CameraMP4Info> newCameraMP4InfoList) {
        this.newCameraMP4InfoList = newCameraMP4InfoList;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String toString() {
        return result;
    }
}
