package com.webex.nbr.checker.dto.recordingmisc;

import javax.xml.bind.annotation.XmlAttribute;

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
public class TocEvent {

    private String content;
    private Integer eventType;
    private Long sessionId;
    private Integer sessionType;
    private Long time;

    @XmlAttribute(name = "Content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @XmlAttribute(name = "EventType")
    public Integer getEventType() {
        return eventType;
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }

    @XmlAttribute(name = "SessionID")
    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    @XmlAttribute(name = "SessionType")
    public Integer getSessionType() {
        return sessionType;
    }

    public void setSessionType(Integer sessionType) {
        this.sessionType = sessionType;
    }

    @XmlAttribute(name = "Time")
    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
