package ventures.of.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ventures.of.util.JsonBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveState {
    CameraStates cameraStates;
    CameraSettings cameraSettings;

    public String toJson(){
        return JsonBuilder.builder()
                .jsonAddStringProperty("cameraStates", cameraStates.toString())
                .jsonAddStringProperty("cameraSettings", cameraSettings.toJson(1))
                .build();

    }
}
