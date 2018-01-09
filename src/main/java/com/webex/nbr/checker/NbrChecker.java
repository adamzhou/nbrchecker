package com.webex.nbr.checker;

import com.google.common.base.Stopwatch;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.webex.nbr.checker.dto.RecordingMP4Info;
import com.webex.nbr.checker.dto.StreamPair;
import com.webex.nbr.checker.dto.recordingdata.CameraData;
import com.webex.nbr.checker.dto.recordingmisc.CameraMP4Info;
import com.webex.nbr.checker.dto.recordingmisc.RecordingMisc;
import com.webex.nbr.checker.dto.recordingmisc.TocEvent;
import com.webex.nbr.checker.utils.NbrRawDataUtil;
import com.webex.nbr.checker.utils.XMLNode;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by linzhou on 15/12/2017.
 */
public class NbrChecker {

    private List<String> rawDataPathList = new ArrayList<>();
    final static private String EVENT_CONTENT_STREAM_START = "Stream Start";
    final static private String EVENT_CONTENT_STREAM_END = "Stream End";
    final static private Integer EVENT_SESSION_TYPE_AUDIO = 21;

    public NbrChecker(List<String> rawDataPathList) {
        this.rawDataPathList.addAll(rawDataPathList);
    }

    public NbrCheckResult check() {
        Stopwatch sw = Stopwatch.createStarted();
        NbrCheckResult result = new NbrCheckResult();
        for (String rawDataPath : rawDataPathList) {
            result.addItem(checkItem(rawDataPath));
        }
        sw.stop();
        NbrCheckRecording nbrCheckItem = new NbrCheckRecording();
        nbrCheckItem.setResult("=======Total time: " + sw.toString() + "=======");
        result.addItem(nbrCheckItem);
        return result;
    }

    /**
     * The check flow is following:
     * 1. Get NbrStart from xxx_xxx_recordingXML_xx.xml under RecordingMis of raw data path and record the sessions with type=21
     * 2. Find the recording.xml under RecordingMP4/xxxx/ in which there is StartTimeUTC which value equals to NbrStart
     * 3. Find the camera MP4 under the same directory as recording.xml and get the duration of camera MP4
     * 4. Compare between the duration of the camera MP4 and the duration of the video session
     *
     * The original flow's result is wrong. New flow is following.
     * 1. Get recording XML list under RecordingMis folder
     * 2. Get MISC recording XML/Camera raw data map according to the NbrStart timestamp
     * 3. Get recording XML info under RecordingMP4 folder
     * 3. For every recording, get the correct camera duration with the modified nbrtool
     * 4. Compare between the duration of generated camera MP4 and the estimated duration
     *
     * RecordingMis/*.xml
     *
     <TOC EventCount="5" IsHibrid="1" NbrStart="1513119414000" NbrVersion="2.3">
     <Event Content="Recording Start" EventType="256" SessionID="0" SessionType="100" Time="1513119414000"/>
     <Event Content="Video Start" EventType="0" SessionID="106" SessionType="21" Time="1513119414782"/>
     <Event Content="Stream Start" EventType="5" SessionID="268435457" SessionType="21" Time="1513119415085"/>
     <Event Content="Stream End" EventType="6" SessionID="268435457" SessionType="21" Time="1513119415319"/>
     <Event Content="Recording End" EventType="259" SessionID="0" SessionType="100" Time="1513119433744"/>
     </TOC>
     *
     *
     * Result output:
     *
     * if the camera length is correct:
     * /xxxx/RecordingMP4/{RecordId} Path, Camera Length OK
     *
     * if the camera length is not correct:
     * /xxxx/RecordingMP4/{RecordId} Path, Camera Length Comparision(original length, estimated length), New Camera File Path, New recording.xml Path
     *
     * @return
     */
    private NbrCheckRecording checkItem(String rawDataPath) {
        Stopwatch sw = Stopwatch.createStarted();
        // the key of all the maps is RecordingMis/xxxx.xml
        Map<String, RecordingMisc> recordingMiscMap = NbrRawDataUtil.getRecordingMiscs(rawDataPath);
        Map<String, RecordingMP4Info> recordingMP4InfoMap = NbrRawDataUtil.getRecordingMP4InfoMap(
                rawDataPath, recordingMiscMap);
        Map<String, NbrCheckRecording> estimatedCameraMap = getEstimatedCameraMap(rawDataPath, recordingMP4InfoMap);

        //Compare between the duration of generated camera MP4 and the estimated duration
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, RecordingMP4Info> entry : recordingMP4InfoMap.entrySet()) {
            List<CameraMP4Info> generatedCameraMP4List = entry.getValue().getCameraMP4InfoList();
            NbrCheckRecording estimatedRecording = estimatedCameraMap.get(entry.getKey());

            sb.append(entry.getValue().getRecordingMP4Folder() + ", ");
            if (generatedCameraMP4List == null || estimatedRecording == null
                    || generatedCameraMP4List.isEmpty()) {
                sb.append("No generated camera MP4!\n");
                break;
            }

            if (estimatedRecording.isCameraLengthOK()) {
                String msg = "Camera Length OK\n";
                sb.append(msg);
                break;
            } else if (estimatedRecording.getNewCameraMP4InfoList() == null) {
                String msg = "No estimated camera MP4!\n";
                sb.append(msg);
                break;
            } else if (generatedCameraMP4List.size() != estimatedRecording.getNewCameraMP4InfoList().size()) {
                String msgFormat = "The size of camera MP4 list is different(generated: %d, estimated: %d)!\n";
                String msg = String.format(msgFormat, generatedCameraMP4List.size(), estimatedRecording.getNewCameraMP4InfoList().size());
                sb.append(msg);
                break;
            }

            //generate new recording.xml to replace the camera duration
            String newRecordingXML = generateNewRecordingXML(estimatedRecording);

            for (int i = 0; i < generatedCameraMP4List.size(); i++) {
                CameraMP4Info generatedCameraMP4Info = generatedCameraMP4List.get(i);
                CameraMP4Info estimatedCameraMP4Info = estimatedRecording.getNewCameraMP4InfoList().get(i);

                //generate new camera mp4 with old camera filename with the original duration
                String newCameraMP4File = generateNewCameraMP4(NbrRawDataUtil.getNameFromPath(generatedCameraMP4Info.getFilePath()), estimatedCameraMP4Info.getFilePath());
                String msgFormat = "Camera Length Comparision(generated: %d, estimated: %d), %s, %s";
                String msg = String.format(msgFormat, generatedCameraMP4Info.getDuration(),
                        estimatedCameraMP4Info.getDuration(), newCameraMP4File, newRecordingXML);
                sb.append(msg);
                break;
            }
            sb.append("\n");
        }

        sw.stop();
        sb.append("====== It took " + sw.toString() + " to parse this raw data path: " + rawDataPath + "========");
        NbrCheckRecording nbrCheckItem = new NbrCheckRecording();
        nbrCheckItem.setResult(sb.toString());
        return nbrCheckItem;
    }

    /**
     * Generate the new recording.xml with the new camera duration
     *
     * @param estimatedRecording
     * @return new recording.xml path
     */
    private String generateNewRecordingXML(NbrCheckRecording estimatedRecording) {
        if (estimatedRecording == null || estimatedRecording.getExistingCameraMP4InfoList() == null
                || estimatedRecording.getExistingCameraMP4InfoList().isEmpty()
                || estimatedRecording.getNewCameraMP4InfoList() == null
                || estimatedRecording.getNewCameraMP4InfoList().isEmpty()) {
            System.out.println("NbrChecker::generateNewRecordingXML: existingCameraMP4List is blank or newCameraMP4InfoList is blank!");
            return null;
        }

        if (estimatedRecording.getExistingCameraMP4InfoList().size() != estimatedRecording.getNewCameraMP4InfoList().size()) {
            System.out.println("NbrChecker::generateNewRecordingXML: existingCameraMP4List size does not equal to newCameraMP4InfoList size!");
            return null;
        }

        if (StringUtils.isBlank(estimatedRecording.getExistingRecordingMP4XML())) {
            System.out.println("NbrChecker::generateNewRecordingXML: existingRecordingPM4XML is blank!");
            return null;
        }

        //get the mapping of camera filename and new duration
        Map<String, Long> cameraFilenameNewDurationMap = getCameraDurationMap(estimatedRecording);

        try {
            byte[] recordingXML = Files.readAllBytes(Paths.get(estimatedRecording.getExistingRecordingMP4XML()));
            XMLNode recordingX = XMLNode.parse(new ByteArrayInputStream(recordingXML));
            updateCameraDuration(recordingX, cameraFilenameNewDurationMap);
            String newRecordingXMLPath = getNewRecordingXMLPath(estimatedRecording);
            File file = new File(newRecordingXMLPath);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(formatXML(new String(recordingX.generate())));
            }
            return newRecordingXMLPath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatXML(String unformattedXML) {
        try {
            final Document document = parseXmlFile(unformattedXML);

            OutputFormat format = new OutputFormat(document);
            format.setLineWidth(65);
            format.setIndenting(true);
            format.setIndent(2);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);

            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Document parseXmlFile(String in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getNewRecordingXMLPath(NbrCheckRecording nbrCheckRecording) {
        String newXMLPath = null;
        if (nbrCheckRecording != null && nbrCheckRecording.getNewCameraMP4InfoList() != null
                && !nbrCheckRecording.getNewCameraMP4InfoList().isEmpty()) {
            String cameraPath = nbrCheckRecording.getNewCameraMP4InfoList().get(0).getFilePath();
            if (cameraPath.lastIndexOf(File.separator) != -1) {
                newXMLPath = cameraPath.substring(0, cameraPath.lastIndexOf(File.separator) + 1).concat("newRecording.xml");
            } else {
                newXMLPath = "newRecording.xml";
            }
        }
        return newXMLPath;
    }

    public static void updateCameraDuration(XMLNode recordingX, Map<String, Long> cameraFilenameNewDurationMap) {
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
                    cameraSequenceNode.attrs.put("duration", String.valueOf(cameraFilenameNewDurationMap.get(cameraFilename)));
                }
            }
        }
    }

    /**
     * get the mapping of camera name and duration
     * camera_1330_385702.mp4, 385702
     *
     *
     *
     * @param nbrCheckRecording
     * @return
     */
    private Map<String, Long> getCameraDurationMap(NbrCheckRecording nbrCheckRecording) {
        Map<String, Long> cameraDurationMap = new HashMap<>();
        if (nbrCheckRecording != null && nbrCheckRecording.getExistingCameraMP4InfoList() != null
                && nbrCheckRecording.getNewCameraMP4InfoList() != null
                && !nbrCheckRecording.getExistingCameraMP4InfoList().isEmpty()
                && !nbrCheckRecording.getNewCameraMP4InfoList().isEmpty()
                && nbrCheckRecording.getExistingCameraMP4InfoList().size() == nbrCheckRecording.getNewCameraMP4InfoList().size()) {
            for (int i = 0; i < nbrCheckRecording.getNewCameraMP4InfoList().size(); i++) {
                cameraDurationMap.put(NbrRawDataUtil.getNameFromPath(nbrCheckRecording.getExistingCameraMP4InfoList().get(i).getFilePath()),
                        nbrCheckRecording.getNewCameraMP4InfoList().get(i).getDuration());
            }
        }
        return cameraDurationMap;
    }

    private String generateNewCameraMP4(String newName, String sourceFilePath) {
        if (StringUtils.isBlank(newName) || StringUtils.isBlank(sourceFilePath)) {
            return null;
        }

        String newFilePath = sourceFilePath.substring(0, sourceFilePath.lastIndexOf(File.separator) + 1).concat(newName);

        try {
            FileUtils.copyFile(new File(sourceFilePath), new File(newFilePath));
            return newFilePath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param rawDataPath
     * @param recordingMP4InfoMap
     * @return Map<String, NbrCheckRecording> the key is RecordingMis/xxxx.xml, it relates to one recording
     */
    private Map<String, NbrCheckRecording> getEstimatedCameraMap(String rawDataPath,
                                                                 Map<String, RecordingMP4Info> recordingMP4InfoMap) {
        Map<String, NbrCheckRecording> estimatedCameraMap = new HashMap<>();
        Map<String, CameraData> miscCameraMap = NbrRawDataUtil.getCameraFiles(rawDataPath);
        if (miscCameraMap != null && !miscCameraMap.isEmpty()) {
            //for every pair (camera.dat/camera.idx)
            for (Map.Entry<String, CameraData> entry : miscCameraMap.entrySet()) {
                RecordingMP4Info generatedMP4Info = recordingMP4InfoMap.get(entry.getKey());
                NbrCheckRecording nbrCheckItem = getEstimatedCameras(entry.getValue(), generatedMP4Info);
                estimatedCameraMap.put(entry.getKey(), nbrCheckItem);
            }
        }

        return estimatedCameraMap;
    }

    private NbrCheckRecording getEstimatedCameras(CameraData cameraData, RecordingMP4Info generatedMP4Info) {
        NbrCheckExecutor executor = null;
        try {
            executor = NbrCheckExecutor.getInstance();
        } catch (Exception e) {
            NbrCheckRecording nbrCheckItem = new NbrCheckRecording();
            nbrCheckItem.setErrorMsg("NbrChecker::getEstimatedCameras failed to call NbrCheckExecutor.getInstance!");
            return nbrCheckItem;
        }
        return executor.run(cameraData.getDatFile(), cameraData.getIdxFile(), generatedMP4Info);
    }

    private List<CameraMP4Info> parseCameraMP4InfoFromRawdata(List<TocEvent> tocEvents) {
        List<CameraMP4Info> cameraMP4InfoList = new ArrayList<>();
        Map<Long, String> audioStreamEvents = new HashMap<>();
        if (tocEvents != null && !tocEvents.isEmpty()) {
            for (TocEvent tocEvent : tocEvents) {
                if (EVENT_CONTENT_STREAM_START.equals(tocEvent.getContent())
                        || EVENT_CONTENT_STREAM_END.equals(tocEvent.getContent())
                        && tocEvent.getSessionType().equals(EVENT_SESSION_TYPE_AUDIO)) {
                    audioStreamEvents.put(tocEvent.getTime(), tocEvent.getContent());
                }
            }
        }
        List<StreamPair> streamPairList = getStreamPairList(audioStreamEvents);
        for (StreamPair streamPair : streamPairList) {
            CameraMP4Info cameraMP4Info = new CameraMP4Info();
            if (streamPair.getStart() == null || streamPair.getEnd() == null) {
                System.out.println("Cannot get the full streamPair from TocEvents!");
                break;
            }
            cameraMP4Info.setDuration(streamPair.getEnd() - streamPair.getStart());
            cameraMP4InfoList.add(cameraMP4Info);
        }

        return cameraMP4InfoList;
    }

    private List<StreamPair> getStreamPairList(Map<Long, String> audioStreamEvents) {

        List<StreamPair> streamPairList = new ArrayList<>();
        if (audioStreamEvents != null && audioStreamEvents.size() > 0) {
            SortedSet<Map.Entry<Long, String>> sortedSet = new TreeSet<>(
                    (e1, e2) -> e1.getKey().compareTo(e2.getKey()));
            sortedSet.addAll(audioStreamEvents.entrySet());
            for (Map.Entry<Long, String> entry : sortedSet) {
                int size = streamPairList.size();
                if (size == 0) {
                    StreamPair streamPair = new StreamPair();
                    if (EVENT_CONTENT_STREAM_START.equals(entry.getValue())) {
                        streamPair.setStart(entry.getKey());
                    } else if (EVENT_CONTENT_STREAM_END.equals(entry.getValue())) {
                        streamPair.setEnd(entry.getKey());
                    } else {
                        System.out.println("Found invalid stream status: " + entry.getValue());
                    }
                    streamPairList.add(streamPair);
                } else {
                    if (EVENT_CONTENT_STREAM_START.equals(entry.getValue()) && streamPairList.get(size - 1).getStart() == null) {
                        streamPairList.get(size - 1).setStart(entry.getKey());
                    } else if (EVENT_CONTENT_STREAM_START.equals(entry.getValue()) && streamPairList.get(size - 1).getStart() != null) {
                        StreamPair streamPair = new StreamPair();
                        streamPair.setStart(entry.getKey());
                        streamPairList.add(streamPair);
                    } else if (EVENT_CONTENT_STREAM_END.equals(entry.getValue()) && streamPairList.get(size - 1).getEnd() == null) {
                        streamPairList.get(size - 1).setEnd(entry.getKey());
                    } else if (EVENT_CONTENT_STREAM_END.equals(entry.getValue()) && streamPairList.get(size - 1).getEnd() != null) {
                        StreamPair streamPair = new StreamPair();
                        streamPair.setEnd(entry.getKey());
                        streamPairList.add(streamPair);
                    }
                }
            }
        }

        return streamPairList;
    }
}
