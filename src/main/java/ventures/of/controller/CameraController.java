package ventures.of.controller;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ventures.of.model.CameraStates;
import ventures.of.model.ValueWithIndex;
import ventures.of.util.*;

import java.io.File;

import static ventures.of.util.ProcessUtil.*;
import static ventures.of.util.RobotUtil.maximizeWindow;

@Slf4j
@Data
@ToString
public class CameraController {
    public static volatile CameraStates cameraStatus;
    private final String VIDEO_DIR = EnvironmentVariableUtil.getPropertyString("camera.settings.function.video.dir");
    private final String TIMELAPSE_DIR = EnvironmentVariableUtil.getPropertyString("camera.settings.function.timelapse.dir");
    private final String IMAGES_DIR = EnvironmentVariableUtil.getPropertyString("camera.settings.function.image.dir");
    private final int verboseCamera = EnvironmentVariableUtil.getPropertyInt("camera.settings.log.verbose.camera");
    @Getter
    private static final String LATEST_FILE = EnvironmentVariableUtil.getPropertyString("camera.settings.function.lastimage.dir");
    private final MasterController masterController;
    // Marked speed to shutter time basically follows fps calculation according to https://www.flutotscamerarepair.com/Shutterspeed.htm
    // but seems to be off by an unknown factor
    private ValueWithIndex shutterTime = new ValueWithIndex(0, 0, new Number[]{-1L, 1L, 50L, 25_000L, 100_000L, 1_000_000L, 10_000_000L, 100_000_000L});
    // ISO = 100 * 2^(gain)
    // ISO = 100 * 2^2 = 400
    private ValueWithIndex gain = new ValueWithIndex(1, 0.5, 1, 2.5, 5, 7.5, 10, 15, 20);
    private ValueWithIndex tlTimeBetween = new ValueWithIndex(2, 1_000L, 5_000L, 10_000L, 30_000L, 60_000L, 120_000L, 600_000L);
    private ValueWithIndex saturation, quality, fps, resolution;
    private ValueWithIndex[] settings = {shutterTime, gain, tlTimeBetween/*, saturation, quality, fps, resolution*/};


    //NYI
    private String buildVideoString() {
        return CameraVideoStringBuilder
                .builder()
                .framerate(30)
                //.codec("libav")
                .codec("h264")
                .profile("high")
                .mode("640:480:30")
                .h264TargetLevel(4.1)
                .width(640)
                .height(480)
                .sharpness(1.5)
                .contrast(1.2)
                .noPreview()
                .timeout(60000)
                .outputDirAndName(VIDEO_DIR + StringUtil.getCurrentTime() + ".h264")
                .denoise("cdn_off")
                .verbose(verboseCamera)
                .build();
    }

    private String buildTimelapseString() {
        return CameraTimelapseStringBuilder
                .builder()
                .timeBetweenImagesTimelapse((Long) tlTimeBetween.getActualValue())
                .timeoutDisabled()
                .outputDirNoName(TIMELAPSE_DIR + StringUtil.getCurrentTime())
                .timestamp()
                .shutter(shutterTime.getActualValue().longValue())
                .sharpness(1.5)
                .contrast(1.2)
                .denoise("cdn_hq")
                .quality(100)
                .verbose(verboseCamera)
                .latest(LATEST_FILE)
                .build();
    }

    private String buildSnapshotString() {
        return CameraStringBuilder
                .builder()
                .timeoutDisabled()
                .outputDirNoName(IMAGES_DIR)
                .timestamp()
                .signal()
                .shutter(shutterTime.getActualValue().longValue())
                .gain(gain.getActualValue().doubleValue())
                .sharpness(1.25)
                .contrast(1.1)
                .denoise("cdn_hq")
                .quality(100)
                .verbose(verboseCamera)
                .latest(LATEST_FILE)
                .build();
    }

    public CameraController(MasterController masterController) {
        this.masterController = masterController;
        File latest = new File(LATEST_FILE);
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
            ledContinuous();
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
            int retries = 0;
            while (/*retries < 10000 &&*/ cameraStatus.equals(CameraStates.NOT_READY) && exitCode != 0) {
                try {
                    log.error("Attempt " + retries + " to restart timelapse");
                    maximizeWindow(8000);
                    exitCode = runCommand(tlCommand, true, true).exitValue();
                    retries++;
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    log.error("Sleep threw exception during timelapse retry");
                }
            }
            cameraStatus = CameraStates.READY;
            ledContinuous();
        }
        return null;
    }

    public Void triggerTakeStill() {
        return triggerTakeStill(8000, true);
    }

    public Void triggerTakeStill(int maximizeTime, boolean wait) {
        if (cameraStatus.equals(CameraStates.READY)) {
            cameraStatus = CameraStates.NOT_READY_AWAITING_INPUT;
            ledBlinking();
            maximizeWindow(maximizeTime);
            runCommand(buildSnapshotString(), true, wait);
            //If we don't wait we'll never know if it failed
            if(wait) {
                cameraStatus = CameraStates.READY;
                ledContinuous();
            }
        } else if (cameraStatus.equals(CameraStates.NOT_READY_AWAITING_INPUT)) {
            cameraStatus = CameraStates.NOT_READY;
            ledBlinking();
            String pid = getPid("libcamera-still");
            sendSignalToPid(pid);
            ledContinuous();
            cameraStatus = CameraStates.NOT_READY_AWAITING_INPUT;
        }
        return null;
    }

    public static Void killLibCamera() {
        ProcessUtil.killProcesses("libcamera-still", "libcamera-jpeg", "libcamera-vid");
        cameraStatus = (CameraStates.READY);
        return null;
    }
}


