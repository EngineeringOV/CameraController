package ventures.of.util;

public class CameraVideoStringBuilder extends CameraStringBuilder{

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

    public CameraVideoStringBuilder codec(String var) {
        command.append(" --codec ").append(var);
        return this;
    }

    public CameraVideoStringBuilder profile(String var) {
        command.append(" --profile ").append(var);
        return this;
    }

    public CameraVideoStringBuilder mode(String var) {
        command.append(" --mode ").append(var);
        return this;
    }

    public CameraVideoStringBuilder h264TargetLevel4() {
        h264TargetLevel(4.0);
        return this;
    }

    public CameraStringBuilder findAppropriateResolution() {
        String model = EnvironmentVariableUtil.getPropertyString("camera.hardware.rpi.model");
        if("02".equals(model)) {
            width(4640);
            height(3480);
            h264TargetLevel(4.0);
            mode("4640:3480:30");
        }
        // else nothing which defaults to camera max (I think)

        return this;
    }
}
