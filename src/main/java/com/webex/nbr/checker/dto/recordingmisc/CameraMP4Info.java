package com.webex.nbr.checker.dto.recordingmisc;

/**
 * Created by linzhou on 18/12/2017.
 */
public class CameraMP4Info implements Comparable<CameraMP4Info> {

    private Long offset;
    private Long duration;
    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    /**
     * Comparator
     */
    @Override
    public int compareTo(CameraMP4Info t) {
        if (t == null) {
            throw new NullPointerException("Attempt to compare a CameraMP4Info to null");
        }

        if (t.getOffset() > this.getOffset()) {
            return -1;
        }

        if (t.getOffset() < this.getOffset()) {
            return 1;
        }

        return 0;
    }
}
