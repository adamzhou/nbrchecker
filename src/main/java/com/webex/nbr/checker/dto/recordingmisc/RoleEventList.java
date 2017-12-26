package com.webex.nbr.checker.dto.recordingmisc;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Created by linzhou on 18/12/2017.
 *
 <RoleEvents originalHostID="524719532">
     <RoleEvent Role="Host" UID="524719532" timestamp="1513119414782"/>
     <RoleEvent Role="Presenter" UID="524719532" timestamp="1513119414782"/>
 </RoleEvents>
 */
public class RoleEventList {
    private List<RoleEvent> roleEventList;
    private Long originalHostId;

    @XmlElement(name = "RoleEvent")
    public List<RoleEvent> getRoleEventList() {
        return roleEventList;
    }

    public void setRoleEventList(List<RoleEvent> roleEventList) {
        this.roleEventList = roleEventList;
    }

    @XmlAttribute(name = "originalHostID")
    public Long getOriginalHostId() {
        return originalHostId;
    }

    public void setOriginalHostId(Long originalHostId) {
        this.originalHostId = originalHostId;
    }
}
