package ventures.of.util;

public class CameraVideoStringBuilder extends CameraStringBuilder{
    private final StringBuilder command = new StringBuilder();

    private CameraVideoStringBuilder() {
        super("libcamera-vid");
    }
    public String build() {
        command.append(" --qt-preview");
        return command.toString();
    }
    public static CameraVideoStringBuilder builder() {
        return new CameraVideoStringBuilder();
    }

    public CameraVideoStringBuilder framerate(double var) {
        command.append(" --framerate ").append(var);
        return this;
    }

    public CameraVideoStringBuilder h264TargetLevel(double var) {
        command.append(" --level ").append(var);
        return this;
    }
    public CameraVideoStringBuilder h264TargetLevel4() {
        command.append(" --level 4");
        return this;
    }


}
