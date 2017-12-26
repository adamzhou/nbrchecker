package com.webex.nbr.checker.dto.recordingmisc;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Created by linzhou on 18/12/2017.
 *
 <TOC EventCount="5" IsHibrid="1" NbrStart="1513119414000" NbrVersion="2.3">
     <Event Content="Recording Start" EventType="256" SessionID="0" SessionType="100" Time="1513119414000"/>
     <Event Content="Video Start" EventType="0" SessionID="106" SessionType="21" Time="1513119414782"/>
     <Event Content="Stream Start" EventType="5" SessionID="268435457" SessionType="21" Time="1513119415085"/>
     <Event Content="Stream End" EventType="6" SessionID="268435457" SessionType="21" Time="1513119415319"/>
     <Event Content="Recording End" EventType="259" SessionID="0" SessionType="100" Time="1513119433744"/>
 </TOC>
 */
public class TocEventList {

    private List<TocEvent> tocEventList;
    private Integer eventCount;
    private Integer isHibrid;
    private long nbrStart;
    private String nbrVersion;

    @XmlElement(name = "Event")
    public List<TocEvent> getTocEventList() {
        return tocEventList;
    }

    public void setTocEventList(List<TocEvent> tocEventList) {
        this.tocEventList = tocEventList;
    }

    @XmlAttribute(name = "EventCount")
    public Integer getEventCount() {
        return eventCount;
    }

    public void setEventCount(Integer eventCount) {
        this.eventCount = eventCount;
    }

    @XmlAttribute(name = "IsHibrid")
    public Integer getIsHibrid() {
        return isHibrid;
    }

    public void setIsHibrid(Integer isHibrid) {
        this.isHibrid = isHibrid;
    }

    @XmlAttribute(name = "NbrStart")
    public long getNbrStart() {
        return nbrStart;
    }

    public void setNbrStart(long nbrStart) {
        this.nbrStart = nbrStart;
    }

    @XmlAttribute(name = "NbrVersion")
    public String getNbrVersion() {
        return nbrVersion;
    }

    public void setNbrVersion(String nbrVersion) {
        this.nbrVersion = nbrVersion;
    }
}
