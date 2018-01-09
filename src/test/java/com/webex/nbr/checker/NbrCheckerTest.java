package com.webex.nbr.checker;

import com.google.common.base.Charsets;
import com.webex.nbr.checker.utils.XMLNode;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by linzhou on 04/01/2018.
 */
public class NbrCheckerTest {
    @Test
    public void testUpdateCameraDuration() {
        /*String testRoot = "/tmp/testUpdateCameraDuration";
        String testDir = testRoot + File.separator + UUID.randomUUID();
        File testDirFile = new File(testDir);
        testDirFile.mkdirs();
        String newRecordingXMLPath = testDir + File.separator + "recording.xml";*/
        Map<String, Long> cameraFilenameNewDurationMap = new HashMap<>();
        cameraFilenameNewDurationMap.put("camera_1330_385702.mp4", 11111L);
        cameraFilenameNewDurationMap.put("camera_399254_214465.mp4", 22222L);
        cameraFilenameNewDurationMap.put("camera_628983_3077360.mp4", 33333L);
        try {
            String recordingXMLContent = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(
                    "recording_under_RecordingMP4.xml"), Charsets.UTF_8);
            XMLNode recordingX = XMLNode.parse(new ByteArrayInputStream(recordingXMLContent.getBytes()));
            NbrChecker.updateCameraDuration(recordingX, cameraFilenameNewDurationMap);

            //verify the updated content
            verifyRecordingXML(recordingX, cameraFilenameNewDurationMap);

            System.out.println(NbrChecker.formatXML(new String(recordingX.generate())));

            /*File file = new File(newRecordingXMLPath);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(new String(recordingX.generate()));
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }/* finally {
            try {
                FileUtils.deleteDirectory(new File(testDir));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

    private void verifyRecordingXML(XMLNode recordingX, Map<String, Long> cameraFilenameNewDurationMap) {
        Iterator<XMLNode> itLevel1 = recordingX.children.iterator();
        XMLNode cameraNode = null;
        while (itLevel1.hasNext()) {
            XMLNode eLevel1 = itLevel1.next();
            if (eLevel1.name.equals("Camera")) {
                cameraNode = eLevel1;
                break;
            }
        }

        if (cameraNode != null) {
            Iterator<XMLNode> cameraSequenceIter = cameraNode.children.iterator();
            while (cameraSequenceIter.hasNext()) {
                XMLNode cameraSequenceNode = cameraSequenceIter.next();
                String cameraFilename = cameraSequenceNode.content.get(0);
                if (cameraSequenceNode.name.equals("Sequence") && cameraFilenameNewDurationMap.get(cameraFilename) != null) {
                    assertTrue(cameraSequenceNode.attrs.get("duration").equals(String.valueOf(cameraFilenameNewDurationMap.get(cameraFilename))));
                }
            }
        }

    }
}
