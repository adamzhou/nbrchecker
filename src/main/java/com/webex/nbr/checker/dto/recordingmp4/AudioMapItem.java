package com.webex.nbr.checker.dto.recordingmp4;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by linzhou on 1/20/17.
 */
public class AudioMapItem implements Comparable<AudioMapItem> {

    private long attendeeId;
    private long uid;
    private String uname;
    private long duration;
    private long timestamp;

    /**
     * No Args Constructor -- need by Spring
     */
    @SuppressWarnings("unused")
    private AudioMapItem() {
        super();
    }

    /**
     * Constructor - always construct this from duration and timestamp
     */
    public AudioMapItem(long duration, long timestamp) {
        this.duration = duration;
        this.timestamp = timestamp;
    }

    @XmlAttribute(name = "AttendeeID")
    public long getAttendeeId() {
        return attendeeId;
    }

    public void setAttendeeId(long attendeeId) {
        this.attendeeId = attendeeId;
    }

    @XmlAttribute(name = "UID")
    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    @XmlAttribute(name = "UName")
    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    @XmlAttribute(name = "duration")
    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @XmlAttribute(name = "timestamp")
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Comparator
     */
    @Override
    public int compareTo(AudioMapItem a) {
        if (a == null) {
            throw new NullPointerException("Attempt to compare a AudioMapItem to null");
        }

        if (a.getTimestamp() > this.getTimestamp()) {
            return -1;
        }

        if (a.getTimestamp() < this.getTimestamp()) {
            return 1;
        }

        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AudioMapItem that = (AudioMapItem) o;

        if (duration != that.duration) return false;
        return timestamp == that.timestamp;
    }

    @Override
    public int hashCode() {
        int result = (int) (duration ^ (duration >>> 32));
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }
}
