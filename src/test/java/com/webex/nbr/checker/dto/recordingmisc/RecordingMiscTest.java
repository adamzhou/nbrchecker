package com.webex.nbr.checker.dto.recordingmisc;

import com.google.common.base.Charsets;
import com.webex.nbr.checker.dto.recordingmisc.RecordingMisc;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;

import static org.junit.Assert.fail;

/**
 * Created by linzhou on 18/12/2017.
 */
public class RecordingMiscTest {

    @Test
    public void testFromXml() {
        String metadataXML = null;
        try {
            metadataXML = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("524719532_8117041532869353420171212_recordingXML_1.xml"),
                    Charsets.UTF_8);

            System.out.println("==============================================================");
            System.out.println("Original XML");
            System.out.println("==============================================================");
            System.out.println(metadataXML);
            System.out.println("==============================================================");

            RecordingMisc recordingMisc = RecordingMisc.fromXML(metadataXML);
            System.out.println("==============================================================");
            System.out.println("Pretty XML");
            System.out.println("==============================================================");
            System.out.println(recordingMisc.toString());
            System.out.println("==============================================================");

        } catch (IOException e) {
            fail("fail to load metadata.xml from resources");
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("fail to transform XML to RecordingMisc object!");
        }
    }
}
