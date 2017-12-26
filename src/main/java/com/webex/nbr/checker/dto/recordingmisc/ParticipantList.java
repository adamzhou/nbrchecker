package com.webex.nbr.checker.dto.recordingmisc;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by linzhou on 18/12/2017.

 <ParticipantList list="2">
     <Participant>
         <LoginName CLID="0" NodeID="16781313" NodeType="139264" UAID="37" UID="524719532" VAID="0">Jenny League</LoginName>
         <JoinDateTimeUTC>1513119414782</JoinDateTimeUTC>
         <LeaveDateTimeUTC>1513119433744</LeaveDateTimeUTC>
         <CorporateEmailID>524719532</CorporateEmailID>
     </Participant>
     <Participant>
         <LoginName CLID="0" NodeID="16789505" NodeType="50334208" UAID="10175" UID="524719532" VAID="0">TelePresence systems</LoginName>
         <JoinDateTimeUTC>1513119414782</JoinDateTimeUTC>
         <LeaveDateTimeUTC>1513119433744</LeaveDateTimeUTC>
         <CorporateEmailID>524719532</CorporateEmailID>
     </Participant>
 </ParticipantList>
 */
public class ParticipantList {

    List<Participant> participantList;
    Integer count;

    ParticipantList() {
        this.participantList = new ArrayList<>();
    }

    public ParticipantList(List<Participant> participantList, @Nullable Integer count) {
        this.participantList = participantList;
        this.count = count;
    }

    @XmlElement(name = "Participant")
    public List<Participant> getParticipantList() {
        return participantList;
    }

    public void setParticipantList(List<Participant> participantList) {
        this.participantList = participantList;
    }

    @XmlAttribute(name = "list")
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
