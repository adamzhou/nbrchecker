package com.webex.nbr.checker.dto.recordingmp4;

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

    private List<AudioMapItem> audioMapItemList;

    /**
     * No Args Constructor -- need by Spring
     */
    AudioMap() {
        super();
        this.audioMapItemList = new ArrayList<>();
    }

    /**
     * Constructor - always construct this from an existing audioMapItemList object
     * @param audioMapItemList List<AudioMapItem>
     */
    public AudioMap(List<AudioMapItem> audioMapItemList) {
        super();
        checkNotNull(audioMapItemList);
        checkState(!audioMapItemList.isEmpty());
        this.audioMapItemList = audioMapItemList;
    }

    @XmlElement(name = "Item")
    public List<AudioMapItem> getAudioMapItemList() {
        Collections.sort(audioMapItemList);    // See AudioMapItem.compareTo for the actual implementation
        return audioMapItemList;
    }

    public void setAudioMapItemList(List<AudioMapItem> audioMapItemList) {
        this.audioMapItemList = audioMapItemList;
    }

    public void addAudioMapSeg(AudioMapItem audioMapItem)
    {
        this.audioMapItemList.add(audioMapItem);
    }

    public long getTotalDuration() {
        long total = 0;
        for (AudioMapItem audioMapItem : audioMapItemList) {
            total += audioMapItem.getDuration();
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

        return audioMapItemList != null ? audioMapItemList.equals(audioMap.audioMapItemList) : audioMap.audioMapItemList == null;
    }

    @Override
    public int hashCode() {
        int result = audioMapItemList != null ? audioMapItemList.hashCode() : 0;
        return result;
    }
}
