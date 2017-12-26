package com.webex.nbr.checker.dto.recordingmisc;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Created by linzhou on 18/12/2017.
 *
 <Audio>
     <SessionActionNotify receiveTimestamp="1513119415666" timestamp="1513119415333"/>
 </Audio>
 */
public class AudioList {

    private List<SessionActionNotify> sessionActionNotifyList;

    @XmlElement(name = "SessionActionNotify")
    public List<SessionActionNotify> getSessionActionNotifyList() {
        return sessionActionNotifyList;
    }

    public void setSessionActionNotifyList(List<SessionActionNotify> sessionActionNotifyList) {
        this.sessionActionNotifyList = sessionActionNotifyList;
    }
}
