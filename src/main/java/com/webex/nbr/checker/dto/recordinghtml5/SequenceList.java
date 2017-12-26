package com.webex.nbr.checker.dto.recordinghtml5;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SequenceList {

    List<Sequence> sequenceList;
    Integer duration;
    
    /**
     * No Args Constructor -- need by Spring
     */
    SequenceList() {
        super();
        this.sequenceList = new ArrayList<Sequence>();
    }
    
    /**
     * Constructor - always construct this from an existing Variant object
     * @param variant
     */
    public SequenceList(List<Sequence> sequenceList, @Nullable Integer duration)
    {
        super();
        this.sequenceList = sequenceList;
        this.duration = duration;
    }
    
    /**
     * Return the list of Sequence objects in order
     * Sorts by startTime, then by endTime if startTimes are equal
     * @return List<Sequence>
     */
    @XmlElement(name = "Sequence")
    public List<Sequence> getSequenceList()
    {
        Collections.sort(sequenceList);    // See Sequence.compareTo for the actual implementation
        return (sequenceList);
    }
    
    public void setSequenceList(List<Sequence> sequenceList)
    {
        this.sequenceList = sequenceList;
    }
    
    public void addSequence(Sequence sequence)
    {
        this.sequenceList.add(sequence);
    }

    @XmlAttribute
    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
