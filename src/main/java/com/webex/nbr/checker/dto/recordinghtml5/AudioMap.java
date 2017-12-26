package com.webex.nbr.checker.dto.recordinghtml5;

import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Created by linzhou on 1/20/17.
 */
public class AudioMap implements Comparable<AudioMap> {

    private boolean isHost;
    private String speaker;
    private List<AudioMapSeg> audioMapSegList;

    /**
     * No Args Constructor -- need by Spring
     */
    AudioMap() {
        super();
        this.audioMapSegList = new ArrayList<>();
    }

    /**
     * Constructor - always construct this from an existing audioMapSegList object
     * @param isHost boolean
     * @param speaker String
     * @param audioMapSegList List<AudioMapItem>
     */
    public AudioMap(boolean isHost, String speaker, List<AudioMapSeg> audioMapSegList) {
        super();
        checkState(StringUtils.isNotEmpty(speaker));
        checkNotNull(audioMapSegList);
        checkState(!audioMapSegList.isEmpty());
        this.isHost = isHost;
        this.speaker = speaker;
        this.audioMapSegList = audioMapSegList;
    }

    @XmlAttribute
    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

    @XmlAttribute
    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    @XmlElement(name = "seg")
    public List<AudioMapSeg> getAudioMapSegList() {
        Collections.sort(audioMapSegList);    // See AudioMapItem.compareTo for the actual implementation
        return audioMapSegList;
    }

    public void setAudioMapSegList(List<AudioMapSeg> audioMapSegList) {
        this.audioMapSegList = audioMapSegList;
    }

    public void addAudioMapSeg(AudioMapSeg audioMapSeg)
    {
        this.audioMapSegList.add(audioMapSeg);
    }

    public long getTotalDuration() {
        long total = 0;
        for (AudioMapSeg audioMapSeg : audioMapSegList) {
            total += audioMapSeg.getDuration();
        }
        return total;
    }

    /**
     * Comparator
     */
    @Override
    public int compareTo(AudioMap a) {
        if (a == null) {
            throw new NullPointerException("Attempt to compare a AudioMap to null");
        }

        if (a.getTotalDuration() > this.getTotalDuration()) {
            return 1;
        }

        if (a.getTotalDuration() < this.getTotalDuration()) {
            return -1;
        }

        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AudioMap audioMap = (AudioMap) o;

        if (isHost != audioMap.isHost) return false;
        if (speaker != null ? !speaker.equals(audioMap.speaker) : audioMap.speaker != null) return false;
        return audioMapSegList != null ? audioMapSegList.equals(audioMap.audioMapSegList) : audioMap.audioMapSegList == null;
    }

    @Override
    public int hashCode() {
        int result = (isHost ? 1 : 0);
        result = 31 * result + (speaker != null ? speaker.hashCode() : 0);
        result = 31 * result + (audioMapSegList != null ? audioMapSegList.hashCode() : 0);
        return result;
    }
}
