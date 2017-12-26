package com.webex.nbr.checker.dto.recordinghtml5;

import org.apache.commons.io.IOUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by linzhou on 1/20/17.
 *
 * Follow WebEx NBR metadata format
 *
 * <AudiomapView>
 *   <Audiomap isHost="true" speaker="Adam Zhou">
 *     <seg duration="12011" timestamp="1550"></seg>
 *     <seg duration="16044" timestamp="209588"></seg>
 *   </Audiomap>
 *   <Audiomap isHost="false" speaker="Fiann Feng">
 *     <seg duration="10515" timestamp="28544"></seg>
 *     <seg duration="5999" timestamp="45044"></seg>
 *   </Audiomap>
 * </AudiomapView>
 */
@XmlRootElement(name = "AudiomapView")
public class AudioMapView {
    List<AudioMap> audioMapList;

    /**
     * No Args Constructor -- need by Spring
     */
    AudioMapView() {
        super();
        this.audioMapList = new ArrayList<>();
    }

    /**
     * Constructor - always construct this from an existing audioMapList object
     * @param audioMapList List<AudioMap>
     */
    public AudioMapView(List<AudioMap> audioMapList) {
        super();
        this.audioMapList = audioMapList;
    }

    /**
     * Return the list of TocThumbnail objects in order
     * Sorts by timestamp
     * @return List<AudioMap>
     */
    @XmlElement(name = "Audiomap")
    public List<AudioMap> getAudioMapList() {
        Collections.sort(audioMapList);    // See AudioMap.compareTo for the actual implementation
        return audioMapList;
    }

    public static AudioMapView fromXML(String audioMapViewXml) throws JAXBException {
        checkNotNull(audioMapViewXml, "audioMapViewXml");
        JAXBContext context = JAXBContext.newInstance(AudioMapView.class);
        Unmarshaller un = context.createUnmarshaller();
        AudioMapView audioMapView = (AudioMapView) un.unmarshal(IOUtils.toInputStream(audioMapViewXml));
        return audioMapView;
    }

    public void setAudioMapList(List<AudioMap> audioMapList)
    {
        this.audioMapList = audioMapList;
    }

    public void addAudioMap(AudioMap audioMap)
    {
        this.audioMapList.add(audioMap);
    }
}
