package com.webex.nbr.checker.dto.recordinghtml5;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by linzhou on 1/16/17.
 *
 * Follow WebEx NBR metadata format
 *
 * <ThumbnailView>
 *     <Thumbnail subject="00:00" timestamp="0">tocThumbnail_0.jpeg</Thumbnail>
 *     <Thumbnail subject="00:06" timestamp="6800">tocThumbnail_6800.jpeg</Thumbnail>
 *     <Thumbnail subject="18:00" timestamp="1086800">tocThumbnail_1086800.jpeg</Thumbnail>
 * </ThumbnailView>
 */
public class ThumbnailView {
    List<TocThumbnail> tocThumbnailList;

    /**
     * No Args Constructor -- need by Spring
     */
    ThumbnailView() {
        super();
        this.tocThumbnailList = new ArrayList<>();
    }

    /**
     * Constructor - always construct this from an existing tocThumbnailList object
     * @param tocThumbnailList List<TocThumbnail>
     */
    public ThumbnailView(List<TocThumbnail> tocThumbnailList) {
        super();
        this.tocThumbnailList = tocThumbnailList;
    }

    /**
     * Return the list of TocThumbnail objects in order
     * Sorts by timestamp
     * @return List<TocThumbnail>
     */
    @XmlElement(name = "Thumbnail")
    public List<TocThumbnail> getTocThumbnailList() {
        Collections.sort(tocThumbnailList);    // See TocThumbnail.compareTo for the actual implementation
        return tocThumbnailList;
    }

    public void setTocThumbnailList(List<TocThumbnail> tocThumbnailList)
    {
        this.tocThumbnailList = tocThumbnailList;
    }

    public void addTocThumbnail(TocThumbnail tocThumbnail)
    {
        this.tocThumbnailList.add(tocThumbnail);
    }
}
