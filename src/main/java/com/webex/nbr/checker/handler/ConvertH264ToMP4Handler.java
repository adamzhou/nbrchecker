package com.webex.nbr.checker.handler;

import java.util.ArrayList;
import java.util.List;

/**
 * Step #2
 * 1. Convert H.264(h264.cfg) to MP4 (WorkEngine generates h264.cfg) with mp4muxer
 * 2. Generate the fixed-frame-rating MP4 with ffmpeg
 *
 * Created by linzhou on 27/12/2017.
 */
public class ConvertH264ToMP4Handler extends AbstractNbrCheckerHandler implements NbrCheckerHandler {
    @Override
    public NbrCheckerHandler process() throws Exception {
        convertH264ToMP4();
        List<String> fixedFrameRatingMP4List = new ArrayList<>();
        for (String avcToMP4 : getAvcToMP4List()) {
            generateFixedFrameRatingMP4(avcToMP4);
            fixedFrameRatingMP4List.add(avcToMP4 + ".mp4");
        }
        setFixedFrameRatingMP4List(fixedFrameRatingMP4List);

        GenerateCameraAudioHandler nextHandler = new GenerateCameraAudioHandler();
        nextHandler.setRawdataDatFile(getRawdataDatFile());
        nextHandler.setRawdataIdxFile(getRawdataIdxFile());
        nextHandler.setOutputDir(getOutputDir());
        nextHandler.setToolDir(getToolDir());
        nextHandler.setCameraAVCFileInfoList(getCameraAVCFileInfoList());
        nextHandler.setH264cfgFile(getH264cfgFile());
        nextHandler.setAvcToMP4List(getAvcToMP4List());
        nextHandler.setFixedFrameRatingMP4List(getFixedFrameRatingMP4List());
        nextHandler.setGeneratedMP4Info(getGeneratedMP4Info());

        return nextHandler;
    }

    private void generateFixedFrameRatingMP4(String avcToMP4) throws Exception {
        executeToolCommand(getFixedFrameRatingCommand(avcToMP4), getEnv());
    }

    /**
     * Command example:
     * /opt/webex/nbr/tool/pd/nbr_conv/ffmpeg -i /spare/nbr/tmp/workengine/files/60e64a955aa4356fe05386ca24ad1ce2/camera_2949_12835.mp4 -s 320*180 /spare/nbr/tmp/workengine/files/60e64a955aa4356fe05386ca24ad1ce2/camera_2949_12835.mp4.mp4
     *
     * @return
     */
    private String getFixedFrameRatingCommand(String avcToMP4) {
        StringBuilder sb = new StringBuilder(getFfmpegPath());
        sb.append(" -i ").append(avcToMP4).append(" -s 320*180 ").append(avcToMP4).append(".mp4");
        return sb.toString();
    }

    private void convertH264ToMP4() throws Exception {
        executeToolCommand(getMP4MuxerCommand(), getEnv());
    }

    /**
     * Command example:
     * /opt/webex/nbr/tool/pd/nbr_conv/mp4muxer /spare/nbr/tmp/workengine/files/60e64a955aa4356fe05386ca24ad1ce2/h264.cfg
     *
     * @return
     */
    private String getMP4MuxerCommand() {
        StringBuilder sb = new StringBuilder(getMp4muxerPath());
        sb.append(" ").append(getH264cfgFile());
        return sb.toString();
    }
}
