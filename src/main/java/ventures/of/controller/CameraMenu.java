package ventures.of.controller;

import com.pi4j.io.gpio.PinState;
import lombok.Data;
import ventures.of.MainProgram;
import ventures.of.model.MenuItem;
import ventures.of.model.ValueWithIndex;
import ventures.of.util.FunctionUtil;
import ventures.of.util.ProcessUtil;
import ventures.of.util.RobotUtil;
import ventures.of.util.StringUtil;
import ventures.of.view.ImageViewerView;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

import static ventures.of.util.StringUtil.printVerbose;

@Data
public class CameraMenu {
    private Color invisibleColor = new Color(0, 0, 0, 0);
    private boolean menuShown = true;
    private int currentItem = 0;

    private MenuItem toggleMenuItem = new MenuItem("Menu", (e -> MainProgram.cameraMenu.toggleMenuAction()));
    private MenuItem rebootItem = new MenuItem("Reboot", (e -> ProcessUtil.rebootAction()));
    private MenuItem shutdownItem = new MenuItem("Shutdown", (e -> ProcessUtil.shutdownAction()));
    private MenuItem killCamItem = new MenuItem("Kill cam", (e -> MainProgram.cameraController.killLibCamera()));
    private MenuItem maximizeItem = new MenuItem("Maximize", (e -> {
        RobotUtil.maximizeWindow(0);
        return null;
    }));
    //todo show only if latest.jpg exists
    private MenuItem showLastImage = new MenuItem("Last image", (e -> showImageZoomAction(3, 3, 1, 1, "latest.jpg")), (f -> ImageViewerView.destroyFrames()));
    //private MenuItem toggleWifi = new MenuItem("Last image", (e -> showImageZoomAction(3,3, 1, 1, "latest.jpg")), (f -> ImageViewerView.destroyFrames()));
    //private MenuItem toggleBlueTooth = new MenuItem("Last image", (e -> showImageZoomAction(3,3, 1, 1, "latest.jpg")), (f -> ImageViewerView.destroyFrames()));

    private ValueWithIndex shutterTime = MainProgram.cameraController.getShutterTime();
    private ValueWithIndex gain = MainProgram.cameraController.getGain();
    private ValueWithIndex timeBetween = MainProgram.cameraController.getTlTimeBetween();
    private MenuItem shutterItem = new MenuItem((f -> "Shutter (" + StringUtil.formatIntoShorterString((int) shutterTime.getActualValue()) + "ms)"), (e -> {
        MainProgram.cameraController.killLibCamera();
        shutterTime.incrementIndexBy(1);
        return null;
    }));
    private MenuItem gainItem = new MenuItem((f -> "Gain (" + gain.getActualValue() + "db)"), (e -> {
        MainProgram.cameraController.killLibCamera();
        gain.incrementIndexBy(1);
        return null;
}));
    private MenuItem timeBetweenItem = new MenuItem((f -> "TL time (" + StringUtil.formatIntoShorterString((long) timeBetween.getActualValue()) + "ms)"), (e -> {
        MainProgram.cameraController.killLibCamera();
        timeBetween.incrementIndexBy(1);
        return null;
    }));
    private MenuItem restoreDefaultsItem = new MenuItem("Restore defaults",  (e -> {Arrays.stream(MainProgram.cameraController.getSettings()).forEach(ValueWithIndex::restoreDefault); return null;}));

    //private MenuItem showLastImage = new MenuItem("Saturation", (e -> MainProgram.cameraController.killLibCamera()));
    //private MenuItem showLastImage = new MenuItem("Sharpness", (e -> MainProgram.cameraController.killLibCamera()));
    //private MenuItem showLastImage = new MenuItem("Contrast", (e -> MainProgram.cameraController.killLibCamera()));
    // private MenuItem backlightItem = new MenuItem("Backlight", null);

    private MenuItem[] settings = {toggleMenuItem, shutdownItem,rebootItem, maximizeItem, killCamItem, showLastImage,timeBetweenItem, shutterItem, gainItem, restoreDefaultsItem};
    private JLabel selectedLabel = new JLabel(settings[currentItem].getName().apply(null));
    private JLabel batteryLabel = new JLabel(MainProgram.batteryController.buildBatteryLabelString());
    private JFrame window = new JFrame();

    public CameraMenu() {
        Font labelFont = new Font(selectedLabel.getFont().getName(), Font.BOLD, 20);
        //SelectedLabel
        selectedLabel.setForeground(Color.WHITE);
        selectedLabel.setBackground(Color.BLACK);
        selectedLabel.setFont(labelFont);
        selectedLabel.setOpaque(true);
        //batteryLabel
        batteryLabel.setForeground(Color.WHITE);
        batteryLabel.setBackground(Color.BLACK);
        batteryLabel.setFont(labelFont);
        batteryLabel.setOpaque(true);
        //Window
        window.setAlwaysOnTop(true);
        window.setBackground(Color.black);
        window.setUndecorated(true);
        window.setOpacity(0);
        window.setLayout(new GridLayout(2, 0));
        window.add(selectedLabel);
        window.add(batteryLabel);
        window.pack();
        window.setLocation(10, 215);
        window.setVisible(true);
    }

    private Void toggleMenuAction() {

        menuShown = !menuShown;
        window.setVisible(menuShown);

        return null;
    }

    Void menuTriggerCurrentAction() {
        if (menuShown) {
            FunctionUtil.applyIfExists(getCurrentMainItem().getActionClick(), null);
        } else {
            toggleMenuAction();
        }

        selectedLabel.setText(getCurrentMainItem().getName().apply(null));
        window.pack();
        window.repaint();
        return null;
    }

    //todo merge Left and right to DRY
    Void menuGoRightAction() {
        if (MainProgram.displayController.getClick().getState() != PinState.HIGH) {
            return null;
        }
        if (menuShown) {
            FunctionUtil.applyIfExists(getCurrentMainItem().getChangeFromInMenu(), null);
            currentItem++;
            if (currentItem >= settings.length) {
                currentItem = 0;
            }
            selectedLabel.setText(getCurrentMainItem().getName().apply(null));
            window.pack();
            window.repaint();
            printVerbose("getCurrentMainItem().getName() = " + getCurrentMainItem().getName(), false);
        }
        return null;
    }

    Void menuGoLeftAction() {
        if (MainProgram.displayController.getClick().getState() != PinState.HIGH) {
            return null;
        }
        if (menuShown) {
            FunctionUtil.applyIfExists(getCurrentMainItem().getChangeFromInMenu(), null);
            currentItem--;
            if (currentItem < 0) {
                currentItem = settings.length - 1;
            }
            selectedLabel.setText(getCurrentMainItem().getName().apply(null));
            window.pack();
            window.repaint();
            printVerbose("getCurrentMainItem().getName() = " + getCurrentMainItem().getName(), false);
        }
        return null;
    }

    private MenuItem getCurrentMainItem() {
        return settings[currentItem];
    }

    private Void showImageZoomAction(int dividerX, int dividerY, int x, int y, String imageName) {
        ImageViewerView.createView(dividerX, dividerY, x, y, imageName);
        return null;
    }
}
