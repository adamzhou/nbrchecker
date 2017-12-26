package com.webex.nbr.checker.dto.recordingmisc;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.Date;

/**
 * Created by linzhou on 18/12/2017.
 * <RecordingMisc>
     <MeetingDetail id="81170415328693534">
         <ModeratorName>Jenny League</ModeratorName>
         <ModeratorLogin>524719532</ModeratorLogin>
         <MeetingName>recording webex and TP</MeetingName>
         <StartTimeUTC/>
         <EndTimeUTC/>
         <ModeratorEmail>524719532</ModeratorEmail>
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
         <MeetingSession type="chat"/>
         <TOC EventCount="5" IsHibrid="1" NbrStart="1513119414000" NbrVersion="2.3">
             <Event Content="Recording Start" EventType="256" SessionID="0" SessionType="100" Time="1513119414000"/>
             <Event Content="Video Start" EventType="0" SessionID="106" SessionType="21" Time="1513119414782"/>
             <Event Content="Stream Start" EventType="5" SessionID="268435457" SessionType="21" Time="1513119415085"/>
             <Event Content="Stream End" EventType="6" SessionID="268435457" SessionType="21" Time="1513119415319"/>
             <Event Content="Recording End" EventType="259" SessionID="0" SessionType="100" Time="1513119433744"/>
         </TOC>
         <Sessions>
             <Session end="1513119433744" id="0" start="1513119414782" type="0"/>
             <Session end="1513119433744" id="108" start="1513119414782" type="50"/>
             <Session end="0" id="101" start="1513119414782" type="1"/>
             <Session end="0" id="103" start="1513119414782" type="23"/>
             <Session end="0" id="104" start="1513119414782" type="5"/>
             <Session end="0" id="106" start="1513119414782" type="21"/>
             <Session end="0" id="268435457" start="1513119415085" type="21"/>
         </Sessions>
         <RoleEvents originalHostID="524719532">
             <RoleEvent Role="Host" UID="524719532" timestamp="1513119414782"/>
             <RoleEvent Role="Presenter" UID="524719532" timestamp="1513119414782"/>
         </RoleEvents>
         <Audio>
             <SessionActionNotify receiveTimestamp="1513119415666" timestamp="1513119415333"/>
         </Audio>
         <recordingStartDateLabel>Tuesday, December 12, 2017</recordingStartDateLabel>
         <recordingStartTimeLabel>4:56 pm, Central Standard Time (Chicago, GMT-06:00)</recordingStartTimeLabel>
         <recordingStartGmtTime>1513119414000</recordingStartGmtTime>
     </MeetingDetail>
 </RecordingMisc>

 */
public class MeetingDetail {

    private String id;
    private String moderatorName;
    private String moderatorLogin;
    private String meetingName;
    private Date startTimeUTC;
    private Date endTimeUTC;
    private String moderatorEmail;
    private ParticipantList participantList;
    private MeetingSession meetingSession;
    private TocEventList tocEventList;
    private SessionList sessionList;
    private RoleEventList roleEventList;
    private AudioList audioList;
    private String recordingStartDateLabel;
    private String recordingStartTimeLabel;
    private long recordingStartGmtTime;

    @XmlAttribute(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement(name = "ModeratorName")
    public String getModeratorName() {
        return moderatorName;
    }

    public void setModeratorName(String moderatorName) {
        this.moderatorName = moderatorName;
    }

    @XmlElement(name = "ModeratorLogin")
    public String getModeratorLogin() {
        return moderatorLogin;
    }

    public void setModeratorLogin(String moderatorLogin) {
        this.moderatorLogin = moderatorLogin;
    }

    @XmlElement(name = "MeetingName")
    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    @XmlElement(name = "StartTimeUTC")
    public Date getStartTimeUTC() {
        return startTimeUTC;
    }

    public void setStartTimeUTC(Date startTimeUTC) {
        this.startTimeUTC = startTimeUTC;
    }

    @XmlElement(name = "EndTimeUTC")
    public Date getEndTimeUTC() {
        return endTimeUTC;
    }

    public void setEndTimeUTC(Date endTimeUTC) {
        this.endTimeUTC = endTimeUTC;
    }

    @XmlElement(name = "ModeratorEmail")
    public String getModeratorEmail() {
        return moderatorEmail;
    }

    public void setModeratorEmail(String moderatorEmail) {
        this.moderatorEmail = moderatorEmail;
    }

    @XmlElement(name = "ParticipantList")
    public ParticipantList getParticipantList() {
        return participantList;
    }

    public void setParticipantList(ParticipantList participantList) {
        this.participantList = participantList;
    }

    @XmlElement(name = "MeetingSession")
    public MeetingSession getMeetingSession() {
        return meetingSession;
    }

    public void setMeetingSession(MeetingSession meetingSession) {
        this.meetingSession = meetingSession;
    }

    @XmlElement(name = "TOC")
    public TocEventList getTocEventList() {
        return tocEventList;
    }

    public void setTocEventList(TocEventList tocEventList) {
        this.tocEventList = tocEventList;
    }

    @XmlElement(name = "Sessions")
    public SessionList getSessionList() {
        return sessionList;
    }

    public void setSessionList(SessionList sessionList) {
        this.sessionList = sessionList;
    }

    @XmlElement(name = "RoleEvents")
    public RoleEventList getRoleEventList() {
        return roleEventList;
    }

    public void setRoleEventList(RoleEventList roleEventList) {
        this.roleEventList = roleEventList;
    }

    @XmlElement(name = "Audio")
    public AudioList getAudioList() {
        return audioList;
    }

    public void setAudioList(AudioList audioList) {
        this.audioList = audioList;
    }

    @XmlElement(name = "recordingStartDateLabel")
    public String getRecordingStartDateLabel() {
        return recordingStartDateLabel;
    }

    public void setRecordingStartDateLabel(String recordingStartDateLabel) {
        this.recordingStartDateLabel = recordingStartDateLabel;
    }

    @XmlElement(name = "recordingStartTimeLabel")
    public String getRecordingStartTimeLabel() {
        return recordingStartTimeLabel;
    }

    public void setRecordingStartTimeLabel(String recordingStartTimeLabel) {
        this.recordingStartTimeLabel = recordingStartTimeLabel;
    }

    @XmlElement(name = "recordingStartGmtLabel")
    public long getRecordingStartGmtTime() {
        return recordingStartGmtTime;
    }

    public void setRecordingStartGmtTime(long recordingStartGmtTime) {
        this.recordingStartGmtTime = recordingStartGmtTime;
    }
}
