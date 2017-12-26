package com.webex.nbr.checker.dto.recordinghtml5;

import com.google.common.base.Preconditions;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Created by linzhou on 1/16/17.
 */
public class TocThumbnail implements Comparable<TocThumbnail> {

    private static final Pattern TOC_THUMBNAIL_NAME_PATTERN = Pattern.compile("tocThumbnail_(\\d+).jpeg");

    private String subject;
    private Long timestamp;    // This is a time offset in MS
    private String filename = null;

    /**
     * No Args Constructor -- need by Spring
     */
    @SuppressWarnings("unused")
    private TocThumbnail() {
        super();
    }

    /**
     * Constructor - always construct this from an existing filename
     * @param filename String
     */
    public TocThumbnail(String filename) {
        super();
        Preconditions.checkNotNull(filename, "filename");
        Preconditions.checkState(parseThumbnailFilename(filename), "TocThumbnail filename is invalid!");

        this.filename = filename;
    }

    private boolean parseThumbnailFilename(String filename) {
        if (!TOC_THUMBNAIL_NAME_PATTERN.matcher(filename).matches()) {
            return false;
        }

        try {
            String split[] = filename.split("_");
            String timestampStr[] = split[1].split(Pattern.quote("."));
            timestamp = Long.parseLong(timestampStr[0]);
            subject = timestampToString(timestamp);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private String timestampToString(Long milliseconds) {
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % TimeUnit.HOURS.toMinutes(1);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % TimeUnit.MINUTES.toSeconds(1);

        String timeString;
        if (hours > 0) {
            timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeString = String.format("%02d:%02d", minutes, seconds);
        }

        return timeString;
    }

    @XmlAttribute
    public String getSubject() {
        return subject;
    }
    @XmlAttribute
    public Long getTimestamp() {
        return timestamp;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @XmlValue
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Comparator
     */
    @Override
    public int compareTo(TocThumbnail t) {
        if (t == null) {
            throw new NullPointerException("Attempt to compare a TocThumbnail to null");
        }

        if (t.getTimestamp() > this.getTimestamp()) {
            return -1;
        }

        if (t.getTimestamp() < this.getTimestamp()) {
            return 1;
        }

        int i = t.getFilename().compareTo(this.getFilename());
        return i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TocThumbnail that = (TocThumbnail) o;

        if (subject != null ? !subject.equals(that.subject) : that.subject != null) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;
        return filename != null ? filename.equals(that.filename) : that.filename == null;
    }

    @Override
    public int hashCode() {
        int result = subject != null ? subject.hashCode() : 0;
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (filename != null ? filename.hashCode() : 0);
        return result;
    }
}
