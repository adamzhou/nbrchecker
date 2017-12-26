package com.webex.nbr.checker.dto.recordinghtml5;

import javax.xml.bind.annotation.XmlElement;

public class RecordingXML {

    private long startTimeUTC = 0L;    
    private long endTimeUTC = 0L;
    
    private SequenceList screenList = new SequenceList();
    
    private SequenceList cameraList = new SequenceList();

    private BubbleIndicatorInterval biInterval;

    private ThumbnailView thumbnailView;

    private AudioMapView audioMapView;

    /**
     * No Args Constructor -- need by Spring
     */
    @SuppressWarnings("unused")
    private RecordingXML() {
        super();
    }


    @XmlElement(name = "StartTimeUTC")
    public long getStartTimeUTC() {
        return startTimeUTC;
    }
    @XmlElement(name = "EndTimeUTC")
    public long getEndTimeUTC() {
        return endTimeUTC;
    }
    @XmlElement (name = "Screen", nillable = true)
    public SequenceList getScreen() {
        return screenList;
    }
    @XmlElement (name = "Camera", nillable = true)
    public SequenceList getCamera() {
        return cameraList;
    }
    @XmlElement (name = "BIinterval", nillable = true)
    public BubbleIndicatorInterval getBiInterval() {
        return biInterval;
    }
    @XmlElement (name = "ThumbnailView", nillable = true)
    public ThumbnailView getThumbnailView() {
        return thumbnailView;
    }
    @XmlElement (name = "AudiomapView", nillable = true)
    public AudioMapView getAudioMapView() {
        return audioMapView;
    }

    public void setStartTimeUTC(long startTimeUTC) {
        this.startTimeUTC = startTimeUTC;
    }
    public void setEndTimeUTC(long endTimeUTC) {
        this.endTimeUTC = endTimeUTC;
    }
    public void setScreen(SequenceList screenList) {
        this.screenList = screenList;
    }
    public void setCamera(SequenceList cameraList) {
        this.cameraList = cameraList;
    }
    public void setBiInterval(BubbleIndicatorInterval biInterval) {
        this.biInterval = biInterval;
    }
    public void setThumbnailView(ThumbnailView thumbnailView) {
        this.thumbnailView = thumbnailView;
    }
    public void setAudioMapView(AudioMapView audioMapView) {
        this.audioMapView = audioMapView;
    }
}
