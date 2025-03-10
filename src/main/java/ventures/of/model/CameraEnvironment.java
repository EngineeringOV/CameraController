package ventures.of.model;

import ventures.of.util.EnvironmentVariableUtil;
import ventures.of.util.JsonBuilder;

public class CameraEnvironment {

    public String toJson() {
        return toJson(0);
    }
    public String toJson(int indentLevel) {
        return JsonBuilder.builder().setIndentLevel(indentLevel)
                .jsonAddStringProperty("LATEST_FILE", getLatestFile())
                .jsonAddStringProperty("VIDEO_DIR", getVideoDir())
                .jsonAddStringProperty("TIMELAPSE_DIR", getTimelapseDir())
                .jsonAddStringProperty("IMAGES_DIR", getImagesDir())
                .jsonAddStringProperty("verboseCamera", getVerboseCamera())
                .jsonEndObject().build();
    }

    public static String getLatestFile() {
        return EnvironmentVariableUtil.getPropertyStringNoCaseModify("camera.settings.function.lastimage.dir");
    }

    public String getVideoDir() {
        return EnvironmentVariableUtil.getPropertyStringNoCaseModify("camera.settings.function.video.dir");
    }

    public String getTimelapseDir() {
        return EnvironmentVariableUtil.getPropertyStringNoCaseModify("camera.settings.function.timelapse.dir");
    }

    public String getImagesDir() {
        return EnvironmentVariableUtil.getPropertyStringNoCaseModify("camera.settings.function.image.dir");
    }

    public int getVerboseCamera() {
        return EnvironmentVariableUtil.getPropertyInt("camera.settings.log.verbose.camera");
    }
}
