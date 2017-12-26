package com.webex.nbr.checker.dto.recordingmisc;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import org.apache.commons.io.IOUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringWriter;

/**
 * Created by linzhou on 18/12/2017.
 */
@XmlRootElement(name = "FileDump")
public class RecordingMisc {
    private MeetingDetail meetingDetail = null;

    private RecordingMisc() {
        super();
    }

    public RecordingMisc(MeetingDetail meetingDetail) {
        this.meetingDetail = meetingDetail;
    }

    @XmlElement(name = "MeetingDetail")
    public MeetingDetail getMeetingDetail() {
        return meetingDetail;
    }

    public void setMeetingDetail(MeetingDetail meetingDetail) {
        this.meetingDetail = meetingDetail;
    }

    public static RecordingMisc fromXML(String meetingDetailXml) throws JAXBException {
        Preconditions.checkNotNull(meetingDetailXml, "meetingDetailXml");
        JAXBContext context = JAXBContext.newInstance(RecordingMisc.class);
        Unmarshaller un = context.createUnmarshaller();
        RecordingMisc recordingMisc = (RecordingMisc) un.unmarshal(
                IOUtils.toInputStream(meetingDetailXml, Charsets.UTF_8));
        return recordingMisc;
    }

    /**
     * Pretty-print the XML
     *
     * @return
     */
    public String toString() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(RecordingMisc.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            StringWriter writer = new StringWriter();
            jaxbMarshaller.marshal(this, writer);
            return writer.toString();
        } catch (JAXBException e) {
            System.out.println("Failed to convert RecordingMisc object to String, " + e.getMessage());
            return "Error formatting XML as a String, " + e.getMessage();
        }
    }

}
