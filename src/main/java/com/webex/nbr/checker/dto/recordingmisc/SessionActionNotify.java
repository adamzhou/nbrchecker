package com.webex.nbr.checker.dto.recordingmisc;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by linzhou on 18/12/2017.
 *
 <Audio>
     <SessionActionNotify receiveTimestamp="1513119415666" timestamp="1513119415333"/>
 </Audio>
 */
public class SessionActionNotify {

    private Long receiveTimestamp;
    private Long timestamp;

    @XmlAttribute(name = "receiveTimestamp")
    public Long getReceiveTimestamp() {
        return receiveTimestamp;
    }

    public void setReceiveTimestamp(Long receiveTimestamp) {
        this.receiveTimestamp = receiveTimestamp;
    }

    @XmlAttribute(name = "timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
