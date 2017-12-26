package com.webex.nbr.checker.dto.recordingmisc;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * Created by linzhou on 18/12/2017.
 *
 * <LoginName CLID="0" NodeID="16781313" NodeType="139264" UAID="37" UID="524719532" VAID="0">Jenny League</LoginName>
 */
public class LoginName {

    private Integer clId;
    private Integer nodeId;
    private Integer nodeType;
    private Integer uaId;
    private Integer uId;
    private Integer vaId;
    private String loginName;

    @XmlAttribute(name = "CLID")
    public Integer getClId() {
        return clId;
    }

    public void setClId(Integer clId) {
        this.clId = clId;
    }

    @XmlAttribute(name = "NodeID")
    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    @XmlAttribute(name = "NodeType")
    public Integer getNodeType() {
        return nodeType;
    }

    public void setNodeType(Integer nodeType) {
        this.nodeType = nodeType;
    }

    @XmlAttribute(name = "UAID")
    public Integer getUaId() {
        return uaId;
    }

    public void setUaId(Integer uaId) {
        this.uaId = uaId;
    }

    @XmlAttribute(name = "UID")
    public Integer getuId() {
        return uId;
    }

    public void setuId(Integer uId) {
        this.uId = uId;
    }

    @XmlAttribute(name = "VAID")
    public Integer getVaId() {
        return vaId;
    }

    public void setVaId(Integer vaId) {
        this.vaId = vaId;
    }

    @XmlValue
    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
}
