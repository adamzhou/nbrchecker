package com.webex.nbr.checker.dto.recordingmisc;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by linzhou on 18/12/2017.
 *
 <RoleEvents originalHostID="524719532">
     <RoleEvent Role="Host" UID="524719532" timestamp="1513119414782"/>
     <RoleEvent Role="Presenter" UID="524719532" timestamp="1513119414782"/>
 </RoleEvents>
 */
public class RoleEvent {

    private String role;
    private Long uid;
    private Long timestamp;

    @XmlAttribute(name = "Role")
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @XmlAttribute(name = "UID")
    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    @XmlAttribute(name = "timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
