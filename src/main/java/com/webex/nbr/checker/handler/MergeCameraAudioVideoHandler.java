package com.webex.nbr.checker.handler;

import com.webex.nbr.checker.utils.NbrRawDataUtil;
import com.webex.nbr.checker.dto.recordingmisc.CameraMP4Info;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Step #4
 * 1. Merge audio to camera MP4 with ffmpeg
 *
 * Created by linzhou on 27/12/2017.
 */
public class MergeCameraAudioVideoHandler extends AbstractNbrCheckerHandler implements NbrCheckerHandler {
    @Override
    public NbrCheckerHandler process() throws Exception {
        mergeCameraAudioVideo();

        return null;
    }

    private void mergeCameraAudioVideo() throws Exception {
        List<CameraMP4Info> cameraMP4InfoList = new ArrayList<>();
        for (String aac : getCameraAudioAACList()) {
            String fixedFrameRatingMP4 = aac.substring(0, aac.lastIndexOf(".aac")).concat(".mp4.mp4");
            String mergedMP4 = aac.substring(0, aac.lastIndexOf(".aac")).concat(".mp4");
            File mergedMP4File = new File(mergedMP4);
            if (mergedMP4File.exists() && mergedMP4File.isFile()) {
                FileUtils.forceDelete(mergedMP4File);
            }
            executeToolCommand(getMergeCameraAVCommand(fixedFrameRatingMP4, aac, mergedMP4), getEnv());
            if (mergedMP4File.exists() && mergedMP4File.isFile()) {
                CameraMP4Info cameraMP4Info = NbrRawDataUtil.parseCameraMP4Info(mergedMP4);
                cameraMP4InfoList.add(cameraMP4Info);
            }
        }
        Collections.sort(cameraMP4InfoList);
        setCameraMP4InfoList(cameraMP4InfoList);
    }

    /**
     * Merge Camera Audio/Video command example:
     * /opt/webex/nbr/tool/pd/nbr_conv/ffmpeg  -i /spare/nbr/tmp/workengine/files/60e64a955aa4356fe05386ca24ad1ce2/camera_2949_12835.mp4 -i /spare/nbr/tmp/workengine/files/60e64a955aa4356fe05386ca24ad1ce2/camera_2949_12835.aac -y -vcodec copy -acodec copy -absf aac_adtstoasc -movflags faststart /spare/nbr/tmp/workengine/files/60e64a955aa4356fe05386ca24ad1ce2/camera_2949_12835.mp4.mp4
     *
     * @param inputMP4
     * @param aac
     * @param mergedMP4
     * @return
     */
    private String getMergeCameraAVCommand(String inputMP4, String aac, String mergedMP4) {
        StringBuilder sb = new StringBuilder(getFfmpegPath());
        sb.append(" -i ").append(inputMP4).append(" -i ").append(aac)
                .append(" -y -vcodec copy -acodec copy -absf aac_adtstoasc -movflags faststart ")
                .append(mergedMP4);
        return sb.toString();
    }
}
