package com.webex.nbr.checker.dto.recordingmp4;

import com.google.common.base.Charsets;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;

import static org.junit.Assert.fail;

/**
 * Created by linzhou on 19/12/2017.
 */
public class RecordingTest {

    @Test
    public void testFromXML() {
        String recordingXML = null;
        try {
            recordingXML = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("recording_under_RecordingMP4.xml"),
                    Charsets.UTF_8);
            System.out.println("==============================================================");
            System.out.println("Original XML");
            System.out.println("==============================================================");
            System.out.println(recordingXML);
            System.out.println("==============================================================");

            Recording recording = Recording.fromXML(recordingXML);
            System.out.println("==============================================================");
            System.out.println("Pretty XML");
            System.out.println("==============================================================");
            System.out.println(recording.toString());
            System.out.println("==============================================================");
        } catch (IOException e) {
            fail("fail to load recording_under_RecordingMP4.xml from resources");
        } catch (JAXBException e) {
            fail("fail to parse recording_under_RecordingMP4.xml from resources");
        }
    }
}
