package com.webex.nbr.checker.dto.recordingmp4;

import com.google.common.base.Preconditions;
import com.webex.nbr.checker.dto.recordingmisc.ParticipantList;
import com.webex.nbr.checker.dto.recordingmisc.RoleEventList;
import org.apache.commons.io.IOUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringWriter;

@XmlRootElement(name = "Recording")
public class Recording {

    private long startTimeUTC = 0L;    
    private long endTimeUTC = 0L;
    private SequenceList screenList = new SequenceList();
    private SequenceList cameraList = new SequenceList();
    private BubbleIndicatorInterval biInterval;
    private AudioMap audioMap;
    private Audio audio;
    private RoleEventList roleEventList;
    private ParticipantList participantList;

    /**
     * No Args Constructor -- need by Spring
     */
    @SuppressWarnings("unused")
    private Recording() {
        super();
    }


    @XmlElement(name = "StartTimeUTC")
    public long getStartTimeUTC() {
        return startTimeUTC;
    }
    @XmlElement(name = "EndTimeUTC")
    public long getEndTimeUTC() {
        return endTimeUTC;
    }
    @XmlElement (name = "Screen", nillable = true)
    public SequenceList getScreen() {
        return screenList;
    }
    @XmlElement (name = "Camera", nillable = true)
    public SequenceList getCamera() {
        return cameraList;
    }
    @XmlElement (name = "BIinterval", nillable = true)
    public BubbleIndicatorInterval getBiInterval() {
        return biInterval;
    }
    @XmlElement (name = "AudioMap", nillable = true)
    public AudioMap getAudioMap() {
        return audioMap;
    }
    @XmlElement (name = "Audio", nillable = true)
    public Audio getAudio() {
        return audio;
    }
    @XmlElement (name = "RoleEvents", nillable = true)
    public RoleEventList getRoleEventList() {
        return roleEventList;
    }
    @XmlElement (name = "ParticipantList", nillable = true)
    public ParticipantList getParticipantList() {
        return participantList;
    }

    public void setStartTimeUTC(long startTimeUTC) {
        this.startTimeUTC = startTimeUTC;
    }
    public void setEndTimeUTC(long endTimeUTC) {
        this.endTimeUTC = endTimeUTC;
    }
    public void setScreen(SequenceList screenList) {
        this.screenList = screenList;
    }
    public void setCamera(SequenceList cameraList) {
        this.cameraList = cameraList;
    }
    public void setBiInterval(BubbleIndicatorInterval biInterval) {
        this.biInterval = biInterval;
    }
    public void setAudioMap(AudioMap audioMap) {
        this.audioMap = audioMap;
    }
    public void setAudio(Audio audio) {
        this.audio = audio;
    }
    public void setRoleEventList(RoleEventList roleEventList) {
        this.roleEventList = roleEventList;
    }
    public void setParticipantList(ParticipantList participantList) {
        this.participantList = participantList;
    }

    public static Recording fromXML(String recordingXML) throws JAXBException
    {
        Preconditions.checkNotNull(recordingXML, "recordingXml");
        JAXBContext context = JAXBContext.newInstance(Recording.class);
        Unmarshaller un = context.createUnmarshaller();
        Recording recording = (Recording) un.unmarshal(IOUtils.toInputStream(recordingXML));
        return recording;
    }

    // Pretty-print the XML
    public String toString()
    {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Recording.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            StringWriter writer = new StringWriter();
            jaxbMarshaller.marshal(this, writer);
            return (writer.toString());
        } catch (JAXBException e) {
            return ("Error formatting XML as a String, " + e.getMessage());
        }
    }
}
