package ventures.of.model;

import lombok.*;
import ventures.of.util.EnvironmentVariableUtil;
import ventures.of.util.JsonBuilder;

@Data
@NoArgsConstructor
@ToString
public class CameraEnvironment {
    @Getter
    private static final String LATEST_FILE = EnvironmentVariableUtil.getPropertyString("camera.settings.function.lastimage.dir");
    private final String VIDEO_DIR = EnvironmentVariableUtil.getPropertyString("camera.settings.function.video.dir");
    private final String TIMELAPSE_DIR = EnvironmentVariableUtil.getPropertyString("camera.settings.function.timelapse.dir");
    private final String IMAGES_DIR = EnvironmentVariableUtil.getPropertyString("camera.settings.function.image.dir");
    private final int verboseCamera = EnvironmentVariableUtil.getPropertyInt("camera.settings.log.verbose.camera");


    public String toJson() {
        return toJson(0);
    }
    public String toJson(int indentLevel) {
        return JsonBuilder.builder().setIndentLevel(indentLevel)
                .jsonAddStringProperty("LATEST_FILE", LATEST_FILE)
                .jsonAddStringProperty("VIDEO_DIR", VIDEO_DIR)
                .jsonAddStringProperty("TIMELAPSE_DIR", TIMELAPSE_DIR)
                .jsonAddStringProperty("IMAGES_DIR", IMAGES_DIR)
                .jsonAddStringProperty("verboseCamera", verboseCamera)
                .jsonEndObject().build();
    }

}
