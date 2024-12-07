package ventures.of.util;

public class CameraTimelapseStringBuilder extends CameraStringBuilder {

    private CameraTimelapseStringBuilder() {
        super("libcamera-still ");
    }

    public String build() {
        command.append(" --qt-preview");
        return command.toString();
    }

    public static CameraTimelapseStringBuilder builder() {
        return new CameraTimelapseStringBuilder();
    }

    public CameraTimelapseStringBuilder timeBetweenImagesTimelapse(long var) {
        command.append(" --timelapse ").append(var);
        return this;
    }


}
