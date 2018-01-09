package com.webex.nbr.checker.handler;

import com.webex.nbr.checker.utils.AudioGenerationUtil;
import com.webex.nbr.checker.utils.NbrRawDataUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Step #3
 * 1. Generate blank camera audio(.pcm)
 * 2. Generate camera audio(.aac) with faac
 *
 * Created by linzhou on 27/12/2017.
 */
public class GenerateCameraAudioHandler extends AbstractNbrCheckerHandler implements NbrCheckerHandler {
    @Override
    public NbrCheckerHandler process() throws Exception {
        generateBlankCameraAudioPCM();
        convertPCMToAAC();

        MergeCameraAudioVideoHandler nextHandler = new MergeCameraAudioVideoHandler();
        nextHandler.setRawdataDatFile(getRawdataDatFile());
        nextHandler.setRawdataIdxFile(getRawdataIdxFile());
        nextHandler.setOutputDir(getOutputDir());
        nextHandler.setToolDir(getToolDir());
        nextHandler.setCameraAVCFileInfoList(getCameraAVCFileInfoList());
        nextHandler.setH264cfgFile(getH264cfgFile());
        nextHandler.setAvcToMP4List(getAvcToMP4List());
        nextHandler.setCameraAudioAACList(getCameraAudioAACList());
        nextHandler.setCameraAudioPCMList(getCameraAudioPCMList());
        nextHandler.setFixedFrameRatingMP4List(getFixedFrameRatingMP4List());
        nextHandler.setGeneratedMP4Info(getGeneratedMP4Info());
        return nextHandler;
    }

    private void convertPCMToAAC() throws Exception {
        List<String> cameraAudioAACList = new ArrayList<>();
        for (String cameraAudioPCM : getCameraAudioPCMList()) {
            String cameraAudioAAC = cameraAudioPCM.substring(0, cameraAudioPCM.lastIndexOf(".pcm")).concat(".aac");
            cameraAudioAACList.add(cameraAudioAAC);
            executeToolCommand(getGenerateAACCommand(cameraAudioPCM, cameraAudioAAC), getEnv());
        }
        setCameraAudioAACList(cameraAudioAACList);
    }

    /**
     * FAAC command example:
     * /opt/webex/nbr/tool/faac/bin/faac  -P -X   -R 16000 -C 1 -B 16 -b 64 -c 16000  /spare/nbr/tmp/workengine/files/60e64a955aa4356fe05386ca24ad1ce2/camera_2949_12835.pcm  -o /spare/nbr/tmp/workengine/files/60e64a955aa4356fe05386ca24ad1ce2/camera_2949_12835.aac
     *
     * @param cameraAudioPCM
     * @param cameraAudioAAC
     * @return
     */
    private String getGenerateAACCommand(String cameraAudioPCM, String cameraAudioAAC) {
        StringBuilder sb = new StringBuilder(getFaacPath());
        sb.append(" -P -X -R 16000 -C 1 -B 16 -b 64 -c 16000 ").append(cameraAudioPCM).append(" -o ").append(cameraAudioAAC);
        return sb.toString();
    }

    private void generateBlankCameraAudioPCM() throws Exception {
        List<String> cameraAudioPCMList = new ArrayList<>();
        //List<Long> audioDuration = new ArrayList<>();
        for (String avcToMP4 : getAvcToMP4List()) {
            String pcmFile = avcToMP4.substring(0, avcToMP4.lastIndexOf(".mp4")).concat(".pcm");
            long duration = NbrRawDataUtil.getDurationFromMP4File(avcToMP4);
            AudioGenerationUtil.generatePCM(pcmFile, (int)duration, 16000, 16);
            cameraAudioPCMList.add(pcmFile);
            //audioDuration.add(Long.valueOf(NbrRawDataUtil.getTimestampFromCameraFile(avcToMP4.substring(0, avcToMP4.lastIndexOf(File.separator) + 1))));
        }
        setCameraAudioPCMList(cameraAudioPCMList);
    }
}
