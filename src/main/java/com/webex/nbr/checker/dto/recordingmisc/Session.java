package com.webex.nbr.checker.dto.recordingmisc;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by linzhou on 18/12/2017.
 *
 <Sessions>
     <Session end="1513119433744" id="0" start="1513119414782" type="0"/>
     <Session end="1513119433744" id="108" start="1513119414782" type="50"/>
     <Session end="0" id="101" start="1513119414782" type="1"/>
     <Session end="0" id="103" start="1513119414782" type="23"/>
     <Session end="0" id="104" start="1513119414782" type="5"/>
     <Session end="0" id="106" start="1513119414782" type="21"/>
     <Session end="0" id="268435457" start="1513119415085" type="21"/>
 </Sessions>
 */
public class Session {

    private Long start;
    private Long end;
    private Long id;
    private Integer type;

    @XmlAttribute(name = "start")
    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    @XmlAttribute(name = "end")
    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    @XmlAttribute(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @XmlAttribute(name = "type")
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
