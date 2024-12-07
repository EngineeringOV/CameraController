package ventures.of.model;

public enum CameraStates {
    READY,
    NOT_READY_VIDEO,
    NOT_READY_TIMELAPSE,
    NOT_READY_AWAITING_INPUT;

    public String toJson() {
        return "CameraState=" + this.toString();
    }
}