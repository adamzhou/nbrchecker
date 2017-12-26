package com.webex.nbr.checker.dto.recordingmp4;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class Sequence implements Comparable<Sequence> {

    @XmlAttribute long duration;
    @XmlAttribute long start;    // This is a time offset in MS from when the Recording starts.
    String size = null;    // "width,height".  Can only be set if this is a VIDEO track
    String url = null;
    
    /**
     * No Args Constructor -- need by Spring
     */
    @SuppressWarnings("unused")
    private Sequence() {
        super();
    }
    
    public long getDuration() {
        return duration;
    }
    
    public long getStart() {
        return start;
    }
    
    /**
     * @return String Video resolution "width,height", or null if there was no VIDEO track in this track
     */
    @XmlAttribute 
    public String getSize() {
        return size;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public void setStart(int start) {
        this.start = start;
    }
    public void setSize(String size) {
        this.size = size;
    }

    @XmlValue
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Comparator
     * Start time rules, but if start times are equal then end time rules.
     * After that, Size and Url are each lexigraphically compared.
     * Sequence is the same iff all fields are the same.
     */
    @Override
    public int compareTo(Sequence s) {
        if (s == null)
            throw new NullPointerException("Attempt to compare a Sequence to null");
        if (s.getStart() > this.getStart())
            return (-1);
        if (s.getStart() < this.getStart())
            return (1);
        if (s.getDuration() > this.getDuration())
            return (-1);
        if (s.getDuration() < this.getDuration())
            return (1);
        
        int i;
        i = s.getSize().compareTo(this.getSize());
        if (i != 0)
            return (i);
        i = s.getUrl().compareTo(this.getUrl());
            return (i);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sequence sequence = (Sequence) o;

        if (duration != sequence.duration) return false;
        if (start != sequence.start) return false;
        if (size != null ? !size.equals(sequence.size) : sequence.size != null) return false;
        return url != null ? url.equals(sequence.url) : sequence.url == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (duration ^ (duration >>> 32));
        result = 31 * result + (int) (start ^ (start >>> 32));
        result = 31 * result + (size != null ? size.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }
}
