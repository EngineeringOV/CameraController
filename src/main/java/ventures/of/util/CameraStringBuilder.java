package ventures.of.util;

import java.io.File;

//welcome to the ugliest builder class you ever seen
public class CameraStringBuilder {
    private final StringBuilder command = new StringBuilder();

    // todo seperate to 3 individual ones for images, video, timelapse
    private CameraStringBuilder(String initialCommand) {
        this.command.append(initialCommand);
    }
    public String build() {
        command.append(" --qt-preview");
        return command.toString();
    }
    public static CameraStringBuilder builder(String initialCommand) {
        return new CameraStringBuilder(initialCommand);
    }

    public CameraStringBuilder outputDirNoName(String var) {
        File asFile = new File(var);
        asFile.mkdirs();
        command.append(" -o ").append(var);
        return this;
    }
    public CameraStringBuilder outputDirAndName(String var) {
        File asFile = new File(var.substring(0, var.lastIndexOf("/")));
        asFile.mkdirs();
        command.append(" -o ").append(var);
        return this;
    }

    public CameraStringBuilder timeout(long var) {
        command.append(" -t ").append(var);
        return this;
    }
    public CameraStringBuilder h264TargetLevel(double var) {
        command.append(" --level ").append(var);
        return this;
    }
    public CameraStringBuilder h264TargetLevel4() {
        command.append(" --level 4");
        return this;
    }

    public CameraStringBuilder timeoutDisabled() {
        command.append(" -t 0");
        return this;
    }

    public CameraStringBuilder sharpness(double var) {
        command.append(" --sharpness ").append(var);
        return this;
    }

    public CameraStringBuilder saturation(double var) {
        if(var == -1){
            return this;
        }
        command.append(" --saturation ").append(var);
        return this;
    }

    public CameraStringBuilder denoise(String var) {
        command.append(" --denoise ").append(var);
        return this;
    }

    public CameraStringBuilder timeBetweenImagesTimelapse(long var) {
        command.append(" --timelapse ").append(var);
        return this;
    }

    public CameraStringBuilder contrast(double var) {
        command.append(" --contrast ").append(var);
        return this;
    }

    public CameraStringBuilder quality(int var) {
        command.append(" -q ").append(var);
        return this;
    }

    public CameraStringBuilder gain(double var) {
        if(var == -1){
            return this;
        }

        command.append(" --gain ").append(var);
        return this;
    }

    public CameraStringBuilder width(int var) {
        command.append(" --width ").append(var);
        return this;
    }

    public CameraStringBuilder height(int var) {
        command.append(" --height ").append(var);
        return this;
    }


    public CameraStringBuilder widthViewfinder(int var) {
        command.append(" --viewfinder-width ").append(var);
        return this;
    }

    public CameraStringBuilder heightViewfinder(int var) {
        command.append(" --viewfinder-height ").append(var);
        return this;
    }


    public CameraStringBuilder preview(int x, int y, int width, int height) {
        command.append(" --preview ").append(x).append(",").append(y).append(",").append(width).append(",").append(height);
        return this;
    }


    // https://forums.raspberrypi.com/viewtopic.php?t=323733 idk
    public CameraStringBuilder shutter(long var) {
        if(var == -1){
            return this;
        }
        command.append(/* --framerate 10 */"--shutter ").append(var);
        return this;
    }


    public CameraStringBuilder verbose(int var) {
        command.append(" -v ").append(var);
        return this;
    }

    public CameraStringBuilder latest(String var) {
        command.append(" --latest ").append(var);
        return this;
    }

    public CameraStringBuilder brightness(double var) {
        if(var == -1){
            return this;
        }

        command.append(" --brightness ").append(var);
        return this;
    }

    public CameraStringBuilder flush() {
        command.append(" --flush");
        return this;
    }

    public CameraStringBuilder hdr() {
        command.append(" --hdr");
        return this;
    }

    public CameraStringBuilder signal() {
        command.append(" --signal");
        return this;
    }

    public CameraStringBuilder timestamp() {
        command.append(" --timestamp");
        return this;
    }

    public CameraStringBuilder datetime() {
        command.append(" --datetime");
        return this;
    }

    public CameraStringBuilder framerate(double var) {
        command.append(" --framerate ").append(var);
        return this;
    }

    public CameraStringBuilder autoFocusRange(String var) {
        command.append(" --autofocus-range ").append(var);
        return this;
    }

    public CameraStringBuilder autoFocusSpeed(String var) {
        command.append(" --autofocus-speed ").append(var);
        return this;
    }

    public CameraStringBuilder immediate() {
        command.append(" --immediate");
        return this;
    }

    public CameraStringBuilder noPreview() {
        command.append(" --nopreview");
        return this;
    }

    public CameraStringBuilder verticalFlip() {
        command.append(" --vflip");
        return this;
    }

    public CameraStringBuilder horizontalFlip() {
        command.append(" --hflip");
        return this;
    }

    public CameraStringBuilder rawOutput() {
        command.append(" --raw");
        return this;
    }

}
