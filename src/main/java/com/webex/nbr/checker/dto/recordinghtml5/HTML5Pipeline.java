package com.webex.nbr.checker.dto.recordinghtml5;

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
 * XML Body for RecordingMetadata for XML request
 * The output of this must conform to the Player's requirements
 * 
 */
@XmlRootElement(name = "HTML5Pipeline")
public class HTML5Pipeline
{
    private RecordingXML recordingXML = null;
    
    @SuppressWarnings("unused")
    private HTML5Pipeline() {
        super(); 
    }

    public HTML5Pipeline(RecordingXML recordingXML) {
        this.recordingXML = recordingXML;
    }

    public static HTML5Pipeline fromXML(String recordingXml) throws JAXBException
    {
        Preconditions.checkNotNull(recordingXml, "pipelineXml");
        JAXBContext context = JAXBContext.newInstance(HTML5Pipeline.class);
        Unmarshaller un = context.createUnmarshaller();
        HTML5Pipeline HTML5Pipeline = (HTML5Pipeline) un.unmarshal(IOUtils.toInputStream(recordingXml));
        return HTML5Pipeline;
    }

    @XmlElement(name = "RecordingXML")
    public RecordingXML getRecordingXML() {
        return recordingXML;
    }

    public void setRecordingXML(RecordingXML recordingXML) {
        this.recordingXML = recordingXML;
    }


    // Pretty-print the XML
    public String toString()
    {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(HTML5Pipeline.class);
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

