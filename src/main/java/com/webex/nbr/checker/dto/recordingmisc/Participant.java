package com.webex.nbr.checker.dto.recordingmisc;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by linzhou on 18/12/2017.
 <Participant>
     <LoginName CLID="0" NodeID="16781313" NodeType="139264" UAID="37" UID="524719532" VAID="0">Jenny League</LoginName>
     <JoinDateTimeUTC>1513119414782</JoinDateTimeUTC>
     <LeaveDateTimeUTC>1513119433744</LeaveDateTimeUTC>
     <CorporateEmailID>524719532</CorporateEmailID>
 </Participant>
 */
public class Participant {

    private LoginName loginName;
    private long joinDateTimeUTC;
    private long leaveDateTimeUTC;
    private long corporateEmailID;

    public LoginName getLoginName() {
        return loginName;
    }

    public void setLoginName(LoginName loginName) {
        this.loginName = loginName;
    }

    @XmlElement(name = "JoinDateTimeUTC")
    public long getJoinDateTimeUTC() {
        return joinDateTimeUTC;
    }

    public void setJoinDateTimeUTC(long joinDateTimeUTC) {
        this.joinDateTimeUTC = joinDateTimeUTC;
    }

    @XmlElement(name = "LeaveDateTimeUTC")
    public long getLeaveDateTimeUTC() {
        return leaveDateTimeUTC;
    }

    public void setLeaveDateTimeUTC(long leaveDateTimeUTC) {
        this.leaveDateTimeUTC = leaveDateTimeUTC;
    }

    @XmlElement(name = "CorporateEmailID")
    public long getCorporateEmailID() {
        return corporateEmailID;
    }

    public void setCorporateEmailID(long corporateEmailID) {
        this.corporateEmailID = corporateEmailID;
    }
}
