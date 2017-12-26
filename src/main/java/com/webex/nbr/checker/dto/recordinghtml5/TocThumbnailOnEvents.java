package com.webex.nbr.checker.dto.recordinghtml5;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
 * XML Body for TOC thumbnail metadata
 * The input of this must conform to the Thumbnail Generation Tool's requirements
 *
 * Created by linzhou on 3/6/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement(name = "Cash")
public class TocThumbnailOnEvents {
    private ThumbnailView thumbnailView = null;

    @SuppressWarnings("unused")
    private TocThumbnailOnEvents() {
        super();
    }

    public TocThumbnailOnEvents(ThumbnailView thumbnailView) {
        this.thumbnailView = thumbnailView;
    }

    public static TocThumbnailOnEvents fromXML(String thumbnailViewXml) throws JAXBException
    {
        Preconditions.checkNotNull(thumbnailViewXml, "thumbnailViewXml");
        JAXBContext context = JAXBContext.newInstance(TocThumbnailOnEvents.class);
        Unmarshaller un = context.createUnmarshaller();
        TocThumbnailOnEvents tocThumbnailOnEvents = (TocThumbnailOnEvents) un.unmarshal(IOUtils.toInputStream(thumbnailViewXml));
        return tocThumbnailOnEvents;
    }

    @XmlElement(name = "ThumbnailView")
    public ThumbnailView getThumbnailView() {
        return thumbnailView;
    }

    public void setThumbnailView(ThumbnailView thumbnailView) {
        this.thumbnailView = thumbnailView;
    }

    // Pretty-print the XML
    public String toString() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(TocThumbnailOnEvents.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            StringWriter writer = new StringWriter();
            jaxbMarshaller.marshal(this, writer);
            return writer.toString();
        } catch (JAXBException e) {
            return "Error formatting XML as a String, " + e.getMessage();
        }
    }
}
