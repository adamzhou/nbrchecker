package com.webex.nbr.checker.dto.recordingmisc;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Created by linzhou on 18/12/2017.
 *
 <Sessions>
     <Session end="1513119433744" id="0" start="1513119414782" type="0"/>
     <Session end="1513119433744" id="108" start="1513119414782" type="50"/>
     <Session end="0" id="101" start="1513119414782" type="1"/>
     <Session end="0" id="103" start="1513119414782" type="23"/>
     <Session end="0" id="104" start="1513119414782" type="5"/>
     <Session end="0" id="106" start="1513119414782" type="21"/>
     <Session end="0" id="268435457" start="1513119415085" type="21"/>
 </Sessions>
 */
public class SessionList {

    private List<Session> sessionList;

    @XmlElement(name = "Session")
    public List<Session> getSessionList() {
        return sessionList;
    }

    public void setSessionList(List<Session> sessionList) {
        this.sessionList = sessionList;
    }
}
