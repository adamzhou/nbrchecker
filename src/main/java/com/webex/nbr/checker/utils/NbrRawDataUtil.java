package com.webex.nbr.checker.utils;

import com.google.common.base.Charsets;
import com.webex.nbr.checker.dto.RecordingMP4Info;
import com.webex.nbr.checker.dto.recordingdata.CameraData;
import com.webex.nbr.checker.dto.recordingmisc.CameraMP4Info;
import com.webex.nbr.checker.dto.recordingmisc.RecordingMisc;
import com.webex.nbr.checker.dto.recordingmp4.CameraAVCInfo;
import com.webex.nbr.checker.dto.recordingmp4.Recording;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by linzhou on 16/12/2017.
 */
public class NbrRawDataUtil {

    private static final String RECORDING_DATA_FOLDER = "RecordingData";
    private static final String RECORDING_MIS_FOLDER = "RecordingMis";
    private static final String RECORDING_MP4_FOLDER = "RecordingMP4";


    private static final String CAMERA_MP4_PREFIX = "camera_";
    private static final String RECORDING_XML = "recording.xml";
    private static final String CAMERA_DAT_FILE_SUFFIX = ".dat";
    private static final String CAMERA_IDX_FILE_SUFFIX = ".idx";
    private static final int CAMERA_RAW_FILE_SPLIT_COUNT = 8;
    private static final int CAMERA_RAW_FILE_TIMESTAMP_INDEX = 4;
    private static final int AVC_SPLIT_COUNT = 3;
    private static final int AVC_OFFSET_INDEX = 1;
    private static final int AVC_DURATION_INDEX = 2;
    private static final double CAMERA_DURATION_ERROR_RATE_THRESHOLD = 0.05;

    /**
     * XML filename under RecordingMis
     * 524719532_8117041532869353420171212_recordingXML_1.xml
     */
    private static final String RECORDING_MIS_XML_REG_EX = "(\\d{1,25})_(\\d{9,25})_recordingXML_(\\d{1,3})(.xml)$";

    /**
     * Camera MP4 filename under RecordingMP4/{recordingId}
     * camera_1328_18738.mp4
     */
    private static final String CAMERA_MP4_REG_EX = "^.*_(\\d{1,15})_(\\d{1,15})(.mp4)$";

    /**
     * Camera AVC filename, nbrtool's output, temporary file
     * camera_1328_18738.avc
     */
    private static final String CAMERA_AVC_REG_EX = "^.*_(\\d{1,15})_(\\d{1,15})(.avc)$";

    /**
     * Camera dat/idx filename under RecordingData
     * wbxmcsr_66.163.58.17_81170415328693534_2918384125_1513119414_21_268435457_1513119415085.dat
     * wbxmcsr_66.163.58.17_81170415328693534_2918384125_1513119414_21_268435457_1513119415085.idx
     *
     * wbxmcsr_173.36.202.50_82016146365089351_173.36.203.87_1513932033_21_268435457_1513932033877.dat
     * wbxmcsr_173.36.202.50_82016146365089351_173.36.203.87_1513932033_21_268435457_1513932033877.idx
     */
    private static final String RECORDING_DATA_CAMERA_REG_EX = "wbxmcsr_(\\d{1,3}\\.){3}\\d{1,3}_(\\d{1,25})_((\\d{1,3}\\.){3}\\d{1,3}|\\d{1,25})_(\\d{1,25})_21_(\\d{1,25})_(\\d{1,25})(.idx|.dat)$";

    public static String getRecordingDataFolder(String rawDataPath) {
        if (StringUtils.isNotBlank(rawDataPath)) {
            return rawDataPath + File.separator + RECORDING_DATA_FOLDER;
        } else {
            return null;
        }
    }

    public static String getRecordingMisFolder(String rawDataPath) {
        if (StringUtils.isNotBlank(rawDataPath)) {
            return rawDataPath + File.separator + RECORDING_MIS_FOLDER;
        } else {
            return null;
        }
    }

    public static String getRecordingMP4Folder(String rawDataPath) {
        if (StringUtils.isNotBlank(rawDataPath)) {
            return rawDataPath + File.separator + RECORDING_MP4_FOLDER;
        } else {
            return null;
        }
    }

    public static Map<String, RecordingMisc> getRecordingMiscs(String rawDataPath) {
        Map<String, RecordingMisc> recordingMiscMap = new HashMap<>();
        String recordingMisFolder = getRecordingMisFolder(rawDataPath);
        if (StringUtils.isNoneBlank(recordingMisFolder)) {
            File recordingMis = new File(recordingMisFolder);
            FilenameFilter recordingMisXMLFilter = getRecordingMisXMLFilter();
            File[] listOfFiles = recordingMis.listFiles(recordingMisXMLFilter);
            if (listOfFiles != null) {
                for (File file : listOfFiles) {
                    if (file != null && file.isFile()) {
                        recordingMiscMap.put(file.getPath(), parseRecordingMisXML(file));
                    }
                }
            }
        }

        return recordingMiscMap;
    }

    private static RecordingMisc parseRecordingMisXML(File file) {
        if (file != null) {
            try {
                String recordingMisXML = IOUtils.toString(new FileInputStream(file), Charsets.UTF_8);
                return RecordingMisc.fromXML(recordingMisXML);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private static FilenameFilter getRecordingMisXMLFilter() {
        return (dir, name) -> {
            Pattern pattern = Pattern.compile(RECORDING_MIS_XML_REG_EX);
            Matcher m = pattern.matcher(name);
            return m.matches();
        };
    }

    public static Map<String, RecordingMP4Info> getRecordingMP4InfoMap(String rawDataPath,
                                                                       Map<String, RecordingMisc> recordingMiscMap) {
        String recordingMP4Folder = getRecordingMP4Folder(rawDataPath);
        Map<String, RecordingMP4Info> recordingMP4InfoMap = new HashMap<>();
        if (StringUtils.isNoneBlank(recordingMP4Folder)) {
            File recordingMp4 = new File(recordingMP4Folder);
            //under recordingMp4, there are multiple recording folders maybe.
            File[] listOfFiles = recordingMp4.listFiles();
            if (listOfFiles != null) {
                for (File recordingMp4Path : listOfFiles) {
                    if (recordingMp4Path.isDirectory()) {
                        RecordingMP4Info mp4info = getMP4Info(recordingMp4Path);
                        if (mp4info != null) {
                            String recordingMisc = matchRecordingMisc(mp4info, recordingMiscMap);
                            if (StringUtils.isNotBlank(recordingMisc)) {
                                recordingMP4InfoMap.put(recordingMisc, mp4info);
                            }
                        }
                    }
                }
            }
        }

        return recordingMP4InfoMap;
    }

    /**
     * Compare between "StartTimeUTC" in RecordingMP4/xxx/recording.xml and "TOC NbrStart" in RecordingMis/*.xml
     *
     * @param mp4info
     * @param recordingMiscMap
     * @return
     */
    private static String matchRecordingMisc(RecordingMP4Info mp4info, Map<String, RecordingMisc> recordingMiscMap) {
        if (mp4info == null || mp4info.getRecordingInfo() == null
                || recordingMiscMap == null || recordingMiscMap.isEmpty()) {
            return null;
        }
        long startTimeFromMP4 = mp4info.getRecordingInfo().getStartTimeUTC();
        for (String recordingMiscName : recordingMiscMap.keySet()) {
            RecordingMisc recordingMisc = recordingMiscMap.get(recordingMiscName);
            long nbrStartFromMisc = recordingMisc.getMeetingDetail().getTocEventList().getNbrStart();
            if (startTimeFromMP4 / 1000 == nbrStartFromMisc / 1000) {
                return recordingMiscName;
            }
        }
        return null;
    }

    private static RecordingMP4Info getMP4Info(File recordingMp4Path) {
        RecordingMP4Info recordingMP4Info = new RecordingMP4Info();
        File[] listOfFiles = recordingMp4Path.listFiles();
        if (listOfFiles != null) {
            recordingMP4Info.setRecordingMP4Folder(recordingMp4Path.getPath());
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    if (file.getName().startsWith(CAMERA_MP4_PREFIX) && checkFileNameByRegex(file, CAMERA_MP4_REG_EX)) {
                        CameraMP4Info cameraMP4Info = parseCameraMP4Info(file.getPath());
                        recordingMP4Info.addCameraMP4Info(cameraMP4Info);
                    } else if (checkFileNameByRegex(file, RECORDING_XML)) {
                        recordingMP4Info.setRecordingInfo(parseRecordingMP4XML(file));
                    }
                }
            }
            recordingMP4Info.sortCameraMP4List();
        }

        return recordingMP4Info;
    }

    private static Recording parseRecordingMP4XML(File file) {
        if (file != null) {
            try {
                String recordingMP4XML = IOUtils.toString(new FileInputStream(file), Charsets.UTF_8);
                return Recording.fromXML(recordingMP4XML);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JAXBException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * camera_1330_385702.mp4
     *
     * @param filePath
     * @return
     */
    public static CameraMP4Info parseCameraMP4Info(String filePath) {
        if (StringUtils.isNotBlank(filePath)) {
            String filename = filePath;
            if (filePath.lastIndexOf(File.separator) != -1) {
                filename = filePath.substring(filePath.lastIndexOf(File.separator) + 1, filePath.length());
            }
            Pattern pattern = Pattern.compile(CAMERA_MP4_REG_EX);
            Matcher m = pattern.matcher(filename.toLowerCase());
            if (m.find()) {
                CameraMP4Info cameraMP4Info = new CameraMP4Info();
                cameraMP4Info.setOffset(Long.parseLong(m.group(1)));
                cameraMP4Info.setDuration(Long.parseLong(m.group(2)));
                cameraMP4Info.setFilePath(filePath);
                return cameraMP4Info;
            } else {
                System.out.println("camera filename format is wrong: " + filename);
                return null;
            }

        } else {
            return null;
        }
    }

    /**
     * camera_1330_385702.avc
     *
     * @param filePath
     * @return
     */
    public static CameraAVCInfo parseCameraAVCInfo(String filePath) {
        if (StringUtils.isNotBlank(filePath)) {
            String filename = filePath;
            if (filePath.lastIndexOf(File.separator) != -1) {
                filename = filePath.substring(filePath.lastIndexOf(File.separator) + 1, filePath.length());
            }
            Pattern pattern = Pattern.compile(CAMERA_AVC_REG_EX);
            Matcher m = pattern.matcher(filename.toLowerCase());
            if (m.find()) {
                CameraAVCInfo cameraAVCInfo = new CameraAVCInfo();
                cameraAVCInfo.setOffset(Long.parseLong(m.group(1)));
                cameraAVCInfo.setDuration(Long.parseLong(m.group(2)));
                cameraAVCInfo.setFilePath(filePath);
                return cameraAVCInfo;
            } else {
                System.out.println("camera filename format is wrong: " + filename);
                return null;
            }

        } else {
            return null;
        }
    }

    private static boolean checkFileNameByRegex(File file, String regex) {
        if (file != null) {
            Pattern pattern = Pattern.compile(regex);
            Matcher m = pattern.matcher(file.getName().toLowerCase());
            return m.matches();
        } else {
            return false;
        }
    }

    public static Map<String, CameraData> getCameraFiles(String rawDataPath) {
        Map<String, CameraData> miscCameraFilesMap = new HashMap<>();
        String recordingMisFolder = getRecordingMisFolder(rawDataPath);
        String recordingDataFolder = getRecordingDataFolder(rawDataPath);
        File[] listOfMiscFiles = null;
        File[] listOfCameraFiles = null;

        //get xml files under RecordingMis
        if (StringUtils.isNoneBlank(recordingMisFolder)) {
            File recordingMis = new File(recordingMisFolder);
            FilenameFilter recordingMisXMLFilter = getRecordingMisXMLFilter();
            listOfMiscFiles = recordingMis.listFiles(recordingMisXMLFilter);
        }

        //get camera dat/idx files under RecordingData
        if (StringUtils.isNotBlank(recordingDataFolder)) {
            File recordingData = new File(recordingDataFolder);
            FilenameFilter recordingCameraDataFilter = getRecordingCameraDataFilter();
            listOfCameraFiles = recordingData.listFiles(recordingCameraDataFilter);
        }

        //get miscXml/cameraFiles map
        if (listOfMiscFiles != null && listOfCameraFiles != null) {
            for (File miscXmlfile : listOfMiscFiles) {
                CameraData cameraData = new CameraData();
                String timestampFromMisXml = getTimestampFromMisXml(miscXmlfile);
                if (StringUtils.isNotBlank(timestampFromMisXml)) {
                    for (File cameraFile : listOfCameraFiles) {
                        String timestampFromCameraFile = getTimestampFromCameraFile(cameraFile.getName());
                        if (timestampFromMisXml.equals(timestampFromCameraFile)) {
                            if (cameraFile.getName().endsWith(CAMERA_DAT_FILE_SUFFIX)) {
                                cameraData.setDatFile(cameraFile.getAbsolutePath());
                            } else if (cameraFile.getName().endsWith(CAMERA_IDX_FILE_SUFFIX)) {
                                cameraData.setIdxFile(cameraFile.getAbsolutePath());
                            }
                        }
                    }
                    if (StringUtils.isNotBlank(cameraData.getDatFile()) && StringUtils.isNotBlank(cameraData.getIdxFile())) {
                        miscCameraFilesMap.put(miscXmlfile.getPath(), cameraData);
                    }
                }
            }
        }

        return miscCameraFilesMap;
    }

    public static String getTimestampFromCameraFile(String cameraFilename) {
        if (StringUtils.isNotBlank(cameraFilename)) {
            String[] arr = cameraFilename.split("_");
            if (arr.length == CAMERA_RAW_FILE_SPLIT_COUNT) {
                return arr[CAMERA_RAW_FILE_TIMESTAMP_INDEX];
            }
        }

        return null;
    }

    /**
     * Get timestamp from misc XML file
     *
     * @param miscXMLFile
     * @return
     */
    private static String getTimestampFromMisXml(File miscXMLFile) {
        if (miscXMLFile == null) {
            return null;
        }

        RecordingMisc recordingMisc = parseRecordingMisXML(miscXMLFile);
        if (recordingMisc != null && recordingMisc.getMeetingDetail() != null
                && recordingMisc.getMeetingDetail().getTocEventList() != null) {
            return String.valueOf(recordingMisc.getMeetingDetail().getTocEventList().getNbrStart() / 1000);
        }

        return null;
    }

    public static FilenameFilter getRecordingCameraDataFilter() {
        return (dir, name) -> {
            Pattern pattern = Pattern.compile(RECORDING_DATA_CAMERA_REG_EX);
            Matcher m = pattern.matcher(name);
            return m.matches();
        };
    }

    public static List<CameraMP4Info> getCameraMP4InfoByAVC(String path) {
        List<CameraMP4Info> cameraMP4InfoList = new ArrayList<>();

        if (StringUtils.isBlank(path)) {
            return cameraMP4InfoList;
        }

        File pathFile = new File(path);
        File[] listOfFiles = pathFile.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && file.getName().startsWith(CAMERA_MP4_PREFIX)
                        && checkFileNameByRegex(file, CAMERA_AVC_REG_EX)) {
                    CameraMP4Info cameraMP4Info = parseCameraMP4InfoByAVC(file);
                    if (cameraMP4Info != null) {
                        cameraMP4InfoList.add(cameraMP4Info);
                    }
                }
            }
            Collections.sort(cameraMP4InfoList, (lhs, rhs) -> {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return lhs.getOffset() < rhs.getOffset() ? -1 : (lhs.getOffset() > rhs.getOffset()) ? 1 : 0;
            });
        }

        return cameraMP4InfoList;
    }

    private static CameraMP4Info parseCameraMP4InfoByAVC(File avcfile) {
        CameraMP4Info cameraMP4Info = null;
        if (avcfile != null) {
            String offFile = avcfile.getPath().substring(0, avcfile.getPath().lastIndexOf(".avc")).concat(".off");
            try {
                long offset = getOffsetFromAVCFile(offFile);
                long realDuration = getLastTimestamp(offFile);
                cameraMP4Info = new CameraMP4Info();
                cameraMP4Info.setOffset(offset);
                cameraMP4Info.setDuration(realDuration);
                cameraMP4Info.setFilePath(avcfile.getPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return cameraMP4Info;
    }

    public static List<CameraAVCInfo> getCameraAVCInfo(String path) {
        List<CameraAVCInfo> cameraAVCInfoList = new ArrayList<>();

        if (StringUtils.isBlank(path)) {
            return cameraAVCInfoList;
        }

        File pathFile = new File(path);
        File[] listOfFiles = pathFile.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && file.getName().startsWith(CAMERA_MP4_PREFIX)
                        && checkFileNameByRegex(file, CAMERA_AVC_REG_EX)) {
                    CameraAVCInfo cameraAVCInfo = parseCameraAVCInfo(file.getPath());
                    cameraAVCInfoList.add(cameraAVCInfo);
                }
            }
            Collections.sort(cameraAVCInfoList, (lhs, rhs) -> {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return lhs.getOffset() < rhs.getOffset() ? -1 : (lhs.getOffset() > rhs.getOffset()) ? 1 : 0;
            });
        }

        return cameraAVCInfoList;
    }

    /**
     * Get Camera AVC file info including *.avc and *.off
     * Pair<L, R> in returned object
     * L: AVC file path
     * R: OFF file path
     *
     * @param path
     * @return
     */
    public static List<Pair<String, String>> getCameraAVCFileInfo(String path) {
        List<Pair<String, String>> cameraAVCFileInfoList = new ArrayList<>();

        if (StringUtils.isBlank(path)) {
            return cameraAVCFileInfoList;
        }

        File pathFile = new File(path);
        File[] listOfFiles = pathFile.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && file.getName().startsWith(CAMERA_MP4_PREFIX)
                        && checkFileNameByRegex(file, CAMERA_AVC_REG_EX)) {
                    Pair<String, String> avcPair = new ImmutablePair<>(file.getAbsolutePath(), avcFileToOff(file.getAbsolutePath()));
                    cameraAVCFileInfoList.add(avcPair);
                }
            }
            Collections.sort(cameraAVCFileInfoList, (lhs, rhs) -> {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                long lhsOffset = getOffsetFromAVCFile(lhs.getLeft());
                long rhsOffset = getOffsetFromAVCFile(rhs.getLeft());
                return lhsOffset < rhsOffset ? -1 : (lhsOffset > rhsOffset) ? 1 : 0;
            });
        }

        return cameraAVCFileInfoList;
    }

    public static String avcFileToOff(String avcFile) {
        if (StringUtils.isBlank(avcFile) && !avcFile.endsWith(".avc")) {
            return null;
        }

        return avcFile.substring(0, avcFile.length() - 4) + ".off";
    }

    public static long getOffsetFromAVCFile(String avcFile) {
        return getValueFromMediaFileByIndex(avcFile, AVC_OFFSET_INDEX);
    }

    private static long getValueFromMediaFileByIndex(String file, int index) {
        if (StringUtils.isNotBlank(file)) {
            String[] arr = file.split("_");
            if (arr.length == AVC_SPLIT_COUNT) {
                try {
                    return Long.valueOf(arr[index]);
                } catch (NumberFormatException e) {
                    return -1;
                }
            }
        }

        return -1;
    }

    public static long getDurationFromMP4File(String mp4File) throws Exception {
        String filename = getNameFromPath(mp4File);
        if (StringUtils.isBlank(filename)) {
            System.out.println("Failed to getDurationFromMP4File from mp4File: " + mp4File);
            throw new Exception("bad input");
        }
        String substring;
        int pos = mp4File.lastIndexOf(".");
        if (pos != -1) {
            substring = mp4File.substring(0, pos);
        } else {
            substring = mp4File;
        }

        String[] arr = substring.split("_");
        if (arr.length <= AVC_DURATION_INDEX) {
            System.out.println("Failed to getDurationFromMP4File from mp4File: " + mp4File);
            throw new Exception("bad input");
        }
        return Long.valueOf(arr[AVC_DURATION_INDEX]);
    }

    public static String getNameFromPath(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        int pos = path.lastIndexOf(File.separator);
        if (pos != -1) {
            return path.substring(pos + 1, path.length());
        } else {
            return path;
        }
    }

    public static String replaceDuration(String filePath, long newDuration) throws Exception {
        if (StringUtils.isBlank(filePath)) {
            throw new Exception("filePath is blank!");
        }

        File originFile = new File(filePath);
        if (!originFile.exists() || originFile.isDirectory()) {
            throw new Exception("File does not exist or it's a directory");
        }

        String newFilename = replaceDurationInAvcOffFilename(originFile.getName(), newDuration);
        String newFilePath = originFile.getParent() + File.separator + newFilename;
        File newFile = new File(newFilePath);
        originFile.renameTo(newFile);
        return newFile.getPath();
    }

    public static String replaceDurationInAvcOffFilename(String filename, long newDuration) throws Exception {
        if (StringUtils.isBlank(filename)) {
            throw new Exception("filename is blank!");
        }

        String[] arr = filename.split("_");
        if (arr.length != AVC_SPLIT_COUNT) {
            throw new Exception("filename format is incorrect!");
        }
        arr[AVC_DURATION_INDEX] = String.valueOf(newDuration);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i != arr.length - 1) {
                sb.append("_");
            }
        }
        String suffix;
        if (filename.endsWith(".avc")) {
            suffix = ".avc";
        } else if (filename.endsWith(".off")) {
            suffix = ".off";
        } else {
            throw new Exception("filename suffix is incorrect!");
        }
        sb.append(suffix);
        return sb.toString();
    }

    /**
     * Get last timestamp from *.off so that to update duration value in AVC/OFF filename
     *
     * *.off file content is like:
     * ======================================
     * ....
     * 12767           43193333        565
     * 12805           43193367        99
     * 12835           43193400        255
     * =======================================
     * In above section, the last timestamp is 12835.
     *
     *
     * @param offFile
     * @return
     */
    public static long getLastTimestamp(String offFile) throws Exception {
        if (StringUtils.isNotBlank(offFile)) {
            String lastLine = "";
            String currentLine;

            try (BufferedReader br = new BufferedReader(new FileReader(offFile))) {
                while ((currentLine = br.readLine()) != null) {
                    //System.out.println(currentLine);
                    lastLine = currentLine;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw e;
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }

            return Long.valueOf(lastLine.substring(0, lastLine.indexOf("\t")));
        } else {
            System.out.println("getLastTimestamp offFile is blank!");
            throw new Exception("offFile is blank!");
        }
    }

    public static boolean hasDiff(long duration1, long duration2) {
        long diff = Math.abs(duration1 - duration2);
        long bigger = duration1 > duration2 ? duration1 : duration2;
        double diff_rate = (double) diff / (double) bigger;
        return diff_rate >= CAMERA_DURATION_ERROR_RATE_THRESHOLD;
    }

}
