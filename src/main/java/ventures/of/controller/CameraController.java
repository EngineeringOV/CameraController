package ventures.of.controller;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ventures.of.model.CameraEnvironment;
import ventures.of.model.CameraSettings;
import ventures.of.model.CameraStates;
import ventures.of.util.*;

import java.io.File;

import static ventures.of.util.ProcessUtil.*;
import static ventures.of.util.RobotUtil.maximizeWindow;

@Slf4j
@Data
@ToString
public class CameraController {
    public static volatile CameraStates cameraStatus;

    private final MasterController masterController;
    private CameraEnvironment ce = new CameraEnvironment();
    private CameraSettings cs = new CameraSettings();

    //video NYI
    private String buildVideoString() {
        return CameraVideoStringBuilder
                .builder()
                .framerate(cs.getFramerate().asDouble())
                //.codec("libav")
                .codec("h264")
                .profile("high")
                .mode("640:480:30")
                .h264TargetLevel(4.1)
                .width(640)
                .height(480)
                .sharpness(cs.getSharpness().asDouble())
                .contrast(cs.getContrast().asDouble())
                //.noPreview()
                .timeout(60000)
                .outputDirAndName(ce.getVIDEO_DIR() + StringUtil.getCurrentTime() + ".h264")
                .denoise("cdn_off")
                .verbose(ce.getVerboseCamera())
                .build();
    }

    private String buildTimelapseString() {
        return CameraTimelapseStringBuilder
                .builder()
                .timeBetweenImagesTimelapse((Long) cs.getTlTimeBetween().getActualValue())
                .timeoutDisabled()
                .outputDirNoName(ce.getTIMELAPSE_DIR() + StringUtil.getCurrentTime())
                .timestamp()
                .shutter(cs.getShutterTime().asLong())
                .gain(cs.getGain().asDouble())
                .sharpness(cs.getSharpness().asDouble())
                .contrast(cs.getContrast().asDouble())
                .denoise("cdn_hq")
                .quality(100)
                .verbose(ce.getVerboseCamera())
                .latest(CameraEnvironment.getLATEST_FILE())
                .build();
    }

    private String buildSnapshotString() {
        return CameraStringBuilder
                .builder()
                .timeoutDisabled()
                .outputDirNoName(ce.getIMAGES_DIR())
                .timestamp()
                .signal()
                .shutter(cs.getShutterTime().asLong())
                .gain(cs.getGain().asDouble())
                .sharpness(cs.getSharpness().asDouble())
                .contrast(cs.getContrast().asDouble())
                .denoise("cdn_hq")
                .quality(100)
                .verbose(ce.getVerboseCamera())
                .latest(CameraEnvironment.getLATEST_FILE())
                .build();
    }

    public CameraController(MasterController masterController) {
        this.masterController = masterController;
        File latest = new File(CameraEnvironment.getLATEST_FILE());
        latest.delete();
        cameraStatus = CameraStates.READY;
    }

    public Void triggerVideo() {
        if (cameraStatus.equals(CameraStates.READY)) {
            cameraStatus = CameraStates.NOT_READY_VIDEO;
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
            cameraStatus = CameraStates.NOT_READY_TIMELAPSE;
            FileUtil.writeToFile(ce.getTIMELAPSE_DIR()+"settings.json", cs.toJson());
            ledBlinking();
            maximizeWindow(8000);
            String tlCommand = buildTimelapseString();
            int exitCode = runCommand(tlCommand, true, true).exitValue();
            int retries = 0;
            while (/*retries < 10000 &&*/ cameraStatus.equals(CameraStates.NOT_READY_TIMELAPSE) && exitCode != 0) {
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
            cameraStatus = CameraStates.NOT_READY_TIMELAPSE;
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