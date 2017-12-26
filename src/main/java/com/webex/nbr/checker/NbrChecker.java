package com.webex.nbr.checker;

import com.webex.nbr.checker.dto.RecordingMP4Info;
import com.webex.nbr.checker.dto.StreamPair;
import com.webex.nbr.checker.dto.recordingdata.CameraData;
import com.webex.nbr.checker.dto.recordingmisc.CameraMP4Info;
import com.webex.nbr.checker.dto.recordingmisc.RecordingMisc;
import com.webex.nbr.checker.dto.recordingmisc.TocEvent;
import com.webex.nbr.checker.dto.recordingmp4.CameraAVCInfo;

import java.io.IOException;
import java.util.*;

/**
 * Created by linzhou on 15/12/2017.
 */
public class NbrChecker {

    private List<String> rawDataPathList = new ArrayList<>();
    final static private String EVENT_CONTENT_STREAM_START = "Stream Start";
    final static private String EVENT_CONTENT_STREAM_END = "Stream End";
    final static private Integer EVENT_SESSION_TYPE_AUDIO = 21;
    final static private double CAMERA_DURATION_ERROR_RATE_THRESHOLD = 0.05;

    public NbrChecker(List<String> rawDataPathList) {
        this.rawDataPathList.addAll(rawDataPathList);
    }

    public NbrCheckResult check() {
        NbrCheckResult result = new NbrCheckResult();
        for (String rawDataPath : rawDataPathList) {
            result.addItem(checkItem(rawDataPath));
        }
        return result;
    }

    /**
     * The check flow is following:
     * 1. Get NbrStart from xxx_xxx_recordingXML_xx.xml under RecordingMis of raw data path and record the sessions with type=21
     * 2. Find the recording.xml under RecordingMP4/xxxx/ in which there is StartTimeUTC which value equals to NbrStart
     * 3. Find the camera MP4 under the same directory as recording.xml and get the duration of camera MP4
     * 4. Compare between the duration of the camera MP4 and the duration of the video session
     *
     * The original flow's result is wrong. New follow is following.
     * 1. Get recording XML list under RecordingMis folder
     * 2. Get MISC recording XML/Camera raw data map according to the NbrStart timestamp
     * 3. Get recording XML info under RecordingMP4 folder
     * 3. For every recording, get the correct camera duration with the modified nbrtool
     * 4. Compare between the duration of generated camera MP4 and the estimated duration
     *
     *
     * @return
     */
    private NbrCheckItem checkItem(String rawDataPath) {
        Map<String, RecordingMisc> recordingMiscMap = NbrRawDataUtil.getRecordingMiscs(rawDataPath);
        Map<String, RecordingMP4Info> recordingMP4InfoMap = NbrRawDataUtil.getRecordingMP4InfoMap(
                rawDataPath, recordingMiscMap);
        Map<String, List<CameraAVCInfo>> estimatedCameraMap = getEstimatedCameraMap(rawDataPath);

        //Compare between the duration of generated camera MP4 and the estimated duration
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, RecordingMP4Info> entry : recordingMP4InfoMap.entrySet()) {
            List<CameraMP4Info> generatedCameraMP4List = entry.getValue().getCameraMP4InfoList();
            List<CameraAVCInfo> estimatedCameraMP4List = estimatedCameraMap.get(entry.getKey());

            sb.append(entry.getKey() + ": ");
            if (generatedCameraMP4List == null || estimatedCameraMP4List == null
                    || generatedCameraMP4List.isEmpty() || estimatedCameraMP4List.isEmpty()) {
                sb.append("no generated camera MP4 or estimated camera MP4!");
                break;
            }

            if (generatedCameraMP4List.size() != estimatedCameraMP4List.size()) {
                String msgFormat = "the size(%s) of generated camera MP4 list is different from the size(%s) of estimated camera MP4 list!";
                String msg = String.format(msgFormat, generatedCameraMP4List.size(), estimatedCameraMP4List.size());
                sb.append(msg);
                break;
            }

            for (int i = 0; i < generatedCameraMP4List.size(); i++) {
                CameraMP4Info generatedCameraMP4Info = generatedCameraMP4List.get(i);
                CameraAVCInfo estimatedCameraAVCInfo = estimatedCameraMP4List.get(i);
                if (generatedCameraMP4Info.getDuration() != estimatedCameraAVCInfo.getDuration()) {
                    String msgFormat = "the duration(%d) of generated camera MP4 is different from the duration(%d) of estimated camera MP4! ";
                    String msg = String.format(msgFormat, generatedCameraMP4Info.getDuration(), estimatedCameraAVCInfo.getDuration());
                    sb.append(msg);
                    break;
                }
            }

            sb.append(" check done!\n");
        }

        NbrCheckItem nbrCheckItem = new NbrCheckItem();
        nbrCheckItem.setResult(sb.toString());
        return nbrCheckItem;
    }

    private Map<String, List<CameraAVCInfo>> getEstimatedCameraMap(String rawDataPath) {
        Map<String, List<CameraAVCInfo>> estimatedCameraMap = new HashMap<>();
        Map<String, CameraData> miscCameraMap = NbrRawDataUtil.getCameraFiles(rawDataPath);
        if (miscCameraMap != null && !miscCameraMap.isEmpty()) {
            for (Map.Entry<String, CameraData> entry : miscCameraMap.entrySet()) {
                List<CameraAVCInfo> cameraMP4List = getEstimatedCameras(entry.getValue());
                estimatedCameraMap.put(entry.getKey(), cameraMP4List);
            }
        }

        return estimatedCameraMap;
    }

    private List<CameraAVCInfo> getEstimatedCameras(CameraData cameraData) {
        List<CameraAVCInfo> estimatedCameras = null;
        NbrCheckExecutor executor = null;
        try {
            executor = NbrCheckExecutor.getInstance();
        } catch (Exception e) {
            return estimatedCameras;
        }
        try {
            estimatedCameras = executor.run(cameraData.getDatFile(), cameraData.getIdxFile());
        } catch (Exception e) {
            return estimatedCameras;
        }
        return estimatedCameras;
    }

    /**
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
     * @param recordingMisc
     * @param recordingMP4Info
     * @return
     */
    private String checkCameraLength(RecordingMisc recordingMisc, RecordingMP4Info recordingMP4Info) {
        List<TocEvent> tocEvents = recordingMisc.getMeetingDetail().getTocEventList().getTocEventList();
        List<CameraMP4Info> cameraMP4InfoList = recordingMP4Info.getCameraMP4InfoList();
        if (cameraMP4InfoList.size() > 1) {
            return "Not support multiple camera files!";
        } else if (cameraMP4InfoList.size() == 0) {
            return "No camera file!";
        }

        List<CameraMP4Info> cameraMP4InfoListFromRawdata = parseCameraMP4InfoFromRawdata(tocEvents);
        if (cameraMP4InfoListFromRawdata.size() > 1) {
            return "Not support multiple stream pairs!";
        } else if (cameraMP4InfoListFromRawdata.size() == 0) {
            return "No stream pair!";
        }

        if (checkCameraDuration(cameraMP4InfoList.get(0).getDuration(),
                cameraMP4InfoListFromRawdata.get(0).getDuration())) {
            return "Camera duration is normal, MP4 duration=" + cameraMP4InfoList.get(0).getDuration()
                    + ", Stream duration=" + cameraMP4InfoListFromRawdata.get(0).getDuration();
        } else {
            return "Camera duration is not normal, MP4 duration=" + cameraMP4InfoList.get(0).getDuration()
                    + ", Stream duration=" + cameraMP4InfoListFromRawdata.get(0).getDuration();
        }

    }

    private boolean checkCameraDuration(Long mp4Duration, Long streamDuration) {
        double error_rate = (double)Math.abs(mp4Duration - streamDuration)
                / (double)Math.max(mp4Duration, streamDuration);
        return error_rate < CAMERA_DURATION_ERROR_RATE_THRESHOLD;
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
