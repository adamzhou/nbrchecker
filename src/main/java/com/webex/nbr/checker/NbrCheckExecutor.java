package com.webex.nbr.checker;

import com.webex.nbr.checker.dto.RecordingMP4Info;
import com.webex.nbr.checker.handler.ConvertRawDataToH264Handler;
import com.webex.nbr.checker.handler.MergeCameraAudioVideoHandler;
import com.webex.nbr.checker.handler.NbrCheckerHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Arrays;
import java.util.UUID;

/**
 * Execute nbrtool simulator to check the generate camera filename
 *
 * The generated file list:
 * camera_1328_20066.avc  camera_1328_20066.off  camera_size.txt  S_V_C-1328
 *
 * Created by linzhou on 25/12/2017.
 */
public class NbrCheckExecutor {

    private static final String TOOL_PACKAGE_RESOURCE_NAME = "/nbrtool/allbin.tar.gz";
    private static final String[] CHECK_TOOL_RESOURCE_NAMES = {
            "/test.sh",
    };
    private static final String EXECUTOR_NAME = "nbrtool";
    //private static final String EXECUTOR_NAME = "test.sh";
    private static final String[] EXECUTABLE_FILES = {
            "nbrtool",
            "nbrtool_i"
    };
    private static final String CHECK_TOOL_TEMP_PATH = "/home/adam/webex-nbr-checker";
    private static final int FILE_READ_BUFF = 1024 * 1024;

    private String workDir;
    private String executorPath;
    private static NbrCheckExecutor nbrCheckExecutor = null;

    private NbrCheckExecutor(String workDir) throws Exception {
        if (StringUtils.isBlank(workDir)) {
            this.workDir = CHECK_TOOL_TEMP_PATH;
        } else {
            this.workDir = workDir;
        }
        initExecutor();
    }

    public static NbrCheckExecutor getInstance() throws Exception {
        if (nbrCheckExecutor == null) {
            nbrCheckExecutor = new NbrCheckExecutor(null);
        }

        return nbrCheckExecutor;
    }

    public static NbrCheckExecutor getInstance(String workDir) throws Exception {
        if (nbrCheckExecutor == null) {
            nbrCheckExecutor = new NbrCheckExecutor(workDir);
        }

        return nbrCheckExecutor;
    }

    private void initExecutor() throws Exception {
        //extract executor from resource and set executorPath
        prepareEnvironment();
        extractResources();
        extractBinTarGzResource();
        executorPath = workDir + File.separator + EXECUTOR_NAME;
    }

    private void extractResources() throws IOException {
        for (String resource : CHECK_TOOL_RESOURCE_NAMES) {
            try (InputStream is = NbrCheckExecutor.class.getResourceAsStream(resource)) {
                File resourceFile = new File(workDir + File.separator + getNameFromPath(resource));
                try (FileOutputStream fos = new FileOutputStream(resourceFile)) {
                    resourceFile.setExecutable(true, false);
                    resourceFile.setReadable(true, false);
                    byte[] buf = new byte[FILE_READ_BUFF];
                    int len = 0;
                    while ((len = is.read(buf)) > 0) {
                        fos.write(buf, 0, len);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    private void extractBinTarGzResource() throws Exception {
        try (InputStream is = NbrCheckExecutor.class.getResourceAsStream(TOOL_PACKAGE_RESOURCE_NAME)) {
            File resourceFile = new File(workDir + File.separator + getNameFromPath(TOOL_PACKAGE_RESOURCE_NAME));
            try (FileOutputStream fos = new FileOutputStream(resourceFile)) {
                resourceFile.setReadable(true, false);
                byte[] buf = new byte[FILE_READ_BUFF];
                int len = 0;
                while ((len = is.read(buf)) > 0) {
                    fos.write(buf, 0, len);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

        decompressBinTarGz();
    }

    private void decompressBinTarGz() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("tar xvfz ").append(workDir)
                .append(File.separator).append(getNameFromPath(TOOL_PACKAGE_RESOURCE_NAME));
        File toolFolder = new File(workDir);

        //Decompress bin.tar.gz with tar command
        try {
            Process pr = Runtime.getRuntime().exec(sb.toString(), getEnv(), toolFolder);
            pr.waitFor();
        } catch (IOException e) {
            System.out.println("Failed to decompress check tool: " + sb.toString() + " " + e.getMessage());
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Failed to decompress check tool: " + sb.toString() + " " + e.getMessage());
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            FileUtils.deleteQuietly(new File(workDir + File.separator + getNameFromPath(TOOL_PACKAGE_RESOURCE_NAME)));
        }

        //set file permission for the decompressed files
        File[] listOfFiles = toolFolder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (Arrays.asList(EXECUTABLE_FILES).contains(file.getName())) {
                    file.setExecutable(true, false);
                }
                file.setReadable(true, false);
            }
        }
    }

    public static String getNameFromPath(String resource) {
        if (StringUtils.isNotBlank(resource) && resource.lastIndexOf("/") != -1) {
            return resource.substring(resource.lastIndexOf("/") + 1);
        } else {
            return resource;
        }
    }

    private void prepareEnvironment() throws IOException {
        File file = new File(workDir);
        if (file.exists()) {
            FileUtils.forceDelete(file);
        }
        file.mkdirs();
    }

    /**
     * The execution command is like:
     * /opt/webex/nbr/tool/nbrtools/nbrtool v /spare/nbr/tmp/workengine/files/0010f056-87a9-4a61-9959-1f4037a14607/wbxmcsr_66.163.58.91_81245574404247262_2918384288_1513190993_21_268435457_1513190994260.idx /spare/nbr/tmp/workengine/files/0010f056-87a9-4a61-9959-1f4037a14607/wbxmcsr_66.163.58.91_81245574404247262_2918384288_1513190993_21_268435457_1513190994260.dat /spare/nbr/tmp/workengine/files/0010f056-87a9-4a61-9959-1f4037a14607/camera
     *
     * @return
     * @throws Exception
     */
    public NbrCheckRecording run(String datFile, String idxFile, RecordingMP4Info generatedMP4Info) {
        NbrCheckRecording nbrCheckItem = new NbrCheckRecording();
        if (generatedMP4Info == null) {
            nbrCheckItem.setErrorMsg("NbrCheckRecording::run: generatedMP4Info is null");
            return nbrCheckItem;
        } else if (StringUtils.isBlank(datFile) || StringUtils.isBlank(idxFile)) {
            nbrCheckItem.setErrorMsg("NbrCheckRecording::run: datFile or idxFile is blank");
            return nbrCheckItem;
        }

        String outputPath = getTempOutput();
        ConvertRawDataToH264Handler convertRawDataToH264Handler = new ConvertRawDataToH264Handler();
        convertRawDataToH264Handler.setOutputDir(outputPath);
        convertRawDataToH264Handler.setToolDir(workDir);
        convertRawDataToH264Handler.setRawdataDatFile(datFile);
        convertRawDataToH264Handler.setRawdataIdxFile(idxFile);
        convertRawDataToH264Handler.setGeneratedMP4Info(generatedMP4Info);

        NbrCheckerHandler handler = convertRawDataToH264Handler;
        NbrCheckerHandler newHandler = handler;

        while (newHandler != null) {
            handler = newHandler;
            try {
                newHandler = handler.process();
            } catch (Exception e) {
                nbrCheckItem.setErrorMsg("NbrCheckRecording::run: handler process error class: " + handler.getClass());
                return nbrCheckItem;
            }
        }

        if (handler instanceof MergeCameraAudioVideoHandler) {
            nbrCheckItem.setCameraLengthOK(false);
            nbrCheckItem.setExistingCameraMP4InfoList(((MergeCameraAudioVideoHandler) handler).getGeneratedMP4Info().getCameraMP4InfoList());
            nbrCheckItem.setNewCameraMP4InfoList(((MergeCameraAudioVideoHandler) handler).getCameraMP4InfoList());
            nbrCheckItem.setExistingRecordingMP4XML(((MergeCameraAudioVideoHandler) handler).getGeneratedMP4Info().getRecordingMP4Folder().concat(File.separator + "recording.xml"));
        } else if (handler instanceof ConvertRawDataToH264Handler) {
            if (((ConvertRawDataToH264Handler) handler).isCameraLengthOK()) {
                nbrCheckItem.setCameraLengthOK(true);
                nbrCheckItem.setExistingCameraMP4InfoList(((ConvertRawDataToH264Handler) handler).getGeneratedMP4Info().getCameraMP4InfoList());
            } else {
                nbrCheckItem.setErrorMsg("NbrCheckRecording::run: Wrong handler class: " + handler.getClass());
            }
        } else {
            nbrCheckItem.setErrorMsg("NbrCheckRecording::run: Wrong handler class: " + handler.getClass());
        }
        return nbrCheckItem;
    }

    private String[] getEnv() {
        String[] envp = new String[1];
        envp[0] = "LD_LIBRARY_PATH=" + workDir;
        return envp;
    }

    private String getTempOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(workDir).append(File.separator)
                .append(UUID.randomUUID());
        String tempDirname = sb.toString();
        File tempDir = new File(tempDirname);
        tempDir.mkdirs();
        return tempDirname;
    }
}
