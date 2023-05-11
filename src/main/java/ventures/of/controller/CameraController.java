package ventures.of.controller;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ventures.of.model.CameraStates;
import ventures.of.model.ValueWithIndex;
import ventures.of.util.CameraStringBuilder;
import ventures.of.util.ProcessUtil;
import ventures.of.util.StringUtil;

import java.io.File;

import static ventures.of.util.ProcessUtil.*;
import static ventures.of.util.RobotUtil.maximizeWindow;

@Slf4j
@Data
@ToString
public class CameraController {
    public volatile CameraStates cameraStatus;
    private static final String VIDEO_DIR = "/home/pi-camera/projects/images/video/";
    private static final String TIMELAPSE_DIR = "/home/pi-camera/projects/images/timelapses/";
    private static final String IMAGES_DIR = "/home/pi-camera/projects/images/";
    @Getter
    private static final String LATEST_FILE = "/home/pi-camera/projects/latest.jpg";
    // Marked speed to shutter time basically follows fps calculation according to https://www.flutotscamerarepair.com/Shutterspeed.htm
    private ValueWithIndex shutterTime = new ValueWithIndex(0,0, new Number[]{-1, 1,50,25000,100000,1000000,10000000,100000000});
    // ISO = 100 * 2^(gain)
    // ISO = 100 * 2^2 = 400
    private ValueWithIndex gain = new ValueWithIndex(1, 0.5,1,2.5,5,7.5,10);
    private ValueWithIndex tlTimeBetween = new ValueWithIndex(2, 1000l,5000l,10000l,30000l,60000l);
    private ValueWithIndex saturation, quality, fps, resolution;
    private ValueWithIndex[] settings = {shutterTime, gain, tlTimeBetween/*, saturation, quality, fps, resolution*/};
    private String buildVideoString() {
        return CameraStringBuilder
                .builder("libcamera-vid")
                .width(1280)
                .height(720)
                .framerate(30)
                .h264TargetLevel4()
                .sharpness(1.5)
                .contrast(1.2)
                .noPreview()
                .timeout(60000)
                .outputDirAndName(VIDEO_DIR + StringUtil.getCurrentTime()+".h264")
                .denoise("cdn_off")
                .verbose(0)
                .build();
    }

    private String buildTimelapseString() {
        return CameraStringBuilder
                .builder("libcamera-still")
                .timeoutDisabled()
                .timeBetweenImagesTimelapse((Long) tlTimeBetween.getActualValue())
                .outputDirNoName(TIMELAPSE_DIR + StringUtil.getCurrentTime())
                .timestamp()
                .shutter(shutterTime.getActualValue().longValue())
                .sharpness(1.5)
                .contrast(1.2)
                .denoise("cdn_hq")
                .quality(100)
                .verbose(0)
                .latest(LATEST_FILE)
                .build();
    }

    private String buildSnapshotString() {
        return CameraStringBuilder
                .builder("libcamera-still")
                .timeoutDisabled()
                .outputDirNoName(IMAGES_DIR)
                .timestamp()
                .signal()
                .shutter(shutterTime.getActualValue().longValue())
                .sharpness(1.5)
                .contrast(1.2)
                .denoise("cdn_hq")
                .quality(100)
                .verbose(0)
                .latest(LATEST_FILE)
                .build();
    }

    public CameraController() {
        File latest = new File("/home/pi-camera/projects/latest.jpg");
        latest.delete();
        cameraStatus = CameraStates.READY;
    }

    public Void triggerVideo() {
        if (cameraStatus.equals(CameraStates.READY)) {
            cameraStatus = CameraStates.NOT_READY;
            ledBlinking();
            maximizeWindow(8000);
            runCommand(buildVideoString(), true, true);
            cameraStatus = CameraStates.READY;
            ledContinous();
        }
        return null;
    }

    public Void triggerTimelapse() {
        if (cameraStatus.equals(CameraStates.READY)) {
            cameraStatus = CameraStates.NOT_READY;
            ledBlinking();
            maximizeWindow(8000);
            String tlCommand = buildTimelapseString();
            int exitCode = runCommand(tlCommand, true, true).exitValue();
            // todo better retry
            int retries = 0;
            while (/*retries < 10000 &&*/ cameraStatus.equals(CameraStates.NOT_READY) && exitCode != 0) {
                try {
                    log.error("Attempt " + retries + " to restart timelapse");
                    maximizeWindow(8000);
                    exitCode = runCommand(tlCommand, true, true).exitValue();
                    retries++;
                    Thread.sleep(3000);
                }
                catch (InterruptedException e) {
                    log.error("Sleep threw exception during timelapse retry");
                }
            }
            cameraStatus = CameraStates.READY;
            ledContinous();
        }
        return null;
    }

    public Void triggerTakeStill() {
        return triggerTakeStill(8000);
    }

    public Void triggerTakeStill(int maximizeTime) {
        if (cameraStatus.equals(CameraStates.READY)) {
            cameraStatus = CameraStates.NOT_READY_AWAITING_INPUT;
            ledBlinking();
            maximizeWindow(maximizeTime);
            runCommand(buildSnapshotString(), true, false);
            cameraStatus = CameraStates.READY;
            ledContinous();
        } else if (cameraStatus.equals(CameraStates.NOT_READY_AWAITING_INPUT)) {
            cameraStatus = CameraStates.NOT_READY;
            ledBlinking();
            String pid = getPid("libcamera-still");
            sendSignalToPid(pid);
            ledContinous();
            cameraStatus = CameraStates.NOT_READY_AWAITING_INPUT;
        }
        return null;
    }

    public Void killLibCamera() {
        ProcessUtil.killProcesses("libcamera-still", "libcamera-jpeg", "libcamera-vid");
        cameraStatus = (CameraStates.READY);
        return null;
    }
}


