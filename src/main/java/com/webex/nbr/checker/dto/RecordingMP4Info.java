package com.webex.nbr.checker.dto;

import com.webex.nbr.checker.dto.recordingmisc.CameraMP4Info;
import com.webex.nbr.checker.dto.recordinghtml5.HTML5Pipeline;
import com.webex.nbr.checker.dto.recordingmp4.Recording;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by linzhou on 18/12/2017.
 */
public class RecordingMP4Info {

    private List<CameraMP4Info> cameraMP4InfoList = new ArrayList<>();
    private Recording recordingInfo;
    private String recordingMP4Folder;

    public String getRecordingMP4Folder() {
        return recordingMP4Folder;
    }

    public void setRecordingMP4Folder(String recordingMP4Folder) {
        this.recordingMP4Folder = recordingMP4Folder;
    }

    public Recording getRecordingInfo() {
        return recordingInfo;
    }

    public void setRecordingInfo(Recording recordingInfo) {
        this.recordingInfo = recordingInfo;
    }

    public List<CameraMP4Info> getCameraMP4InfoList() {
        return cameraMP4InfoList;
    }

    public void setCameraMP4InfoList(List<CameraMP4Info> cameraMP4InfoList) {
        this.cameraMP4InfoList = cameraMP4InfoList;
    }

    public void addCameraMP4Info(CameraMP4Info cameraMP4Info) {
        cameraMP4InfoList.add(cameraMP4Info);
    }

    public void sortCameraMP4List() {
        Collections.sort(cameraMP4InfoList, (lhs, rhs) -> {
            // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
            return lhs.getOffset() < rhs.getOffset() ? -1 : (lhs.getOffset() > rhs.getOffset()) ? 1 : 0;
        });
    }
}
