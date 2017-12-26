package com.webex.nbr.checker.dto.recordinghtml5;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by linzhou on 12/29/16.
 *
 * Follow WebEx NBR metadata format
 *
 * <BIinterval interval="1808"></BIinterval>
 */
public class BubbleIndicatorInterval {
    Integer interval;

    /**
     * No Args Constructor -- need by Spring
     */
    BubbleIndicatorInterval() {
        super();
    }

    /**
     * Constructor - always construct this from an existing Integer object
     * @param interval
     */
    public BubbleIndicatorInterval(@Nullable Integer interval) {
        super();
        this.interval = interval;
    }

    @XmlAttribute
    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }
}
