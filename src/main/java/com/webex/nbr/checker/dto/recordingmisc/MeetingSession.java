package com.webex.nbr.checker.dto.recordingmisc;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by linzhou on 18/12/2017.
 *
 * <MeetingSession type="chat"/>
 *
 */
public class MeetingSession {

    private String type;

    @XmlAttribute(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
