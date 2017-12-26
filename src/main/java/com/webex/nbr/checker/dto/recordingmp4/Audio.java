package com.webex.nbr.checker.dto.recordingmp4;

import javax.xml.bind.annotation.XmlValue;

/**
 * Created by linzhou on 19/12/2017.
 */
public class Audio {
    private String filename;

    @XmlValue
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
