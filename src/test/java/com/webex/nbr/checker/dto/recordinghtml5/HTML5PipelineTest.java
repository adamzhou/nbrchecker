package com.webex.nbr.checker.dto.recordinghtml5;

import com.google.common.base.Charsets;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;

import static org.junit.Assert.fail;

/**
 * Created by linzhou on 18/12/2017.
 */
public class HTML5PipelineTest {

    @Test
    public void testFromXML() {
        String recordingXML = null;
        try {
            recordingXML = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("recording_HTML5Pipeline.xml"),
                    Charsets.UTF_8);
            System.out.println("==============================================================");
            System.out.println("Original XML");
            System.out.println("==============================================================");
            System.out.println(recordingXML);
            System.out.println("==============================================================");

            HTML5Pipeline html5Pipeline = HTML5Pipeline.fromXML(recordingXML);
            System.out.println("==============================================================");
            System.out.println("Pretty XML");
            System.out.println("==============================================================");
            System.out.println(html5Pipeline.toString());
            System.out.println("==============================================================");
        } catch (IOException e) {
            fail("fail to load recording_HTML5Pipeline.xml from resources");
        } catch (JAXBException e) {
            fail("fail to parse recording_HTML5Pipeline.xml from resources");
        }
    }
}
