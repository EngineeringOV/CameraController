package ventures.of.util;

import java.awt.*;
import java.awt.event.InputEvent;

import static ventures.of.util.StringUtil.printVerbose;

public class RobotUtil {
    public static Void maximizeWindow(int initialDelay) {
        new Thread(() -> {
            try {
                printVerbose("maximizing", false);
                Robot robot = new Robot();
                robot.delay(initialDelay);
                robot.mouseMove(155, 15);
                robot.mousePress(InputEvent.BUTTON1_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_MASK);
                robot.delay(500);
                robot.mouseMove(300, 240);
                printVerbose("maximizing done", false);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }).start();
        return null;
    }
}
