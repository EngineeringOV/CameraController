package ventures.of.view;

import com.pi4j.io.gpio.PinState;
import lombok.Data;
import lombok.NoArgsConstructor;
import ventures.of.controller.CameraController;
import ventures.of.controller.MasterController;
import ventures.of.model.ValueWithIndex;
import ventures.of.util.*;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

import static ventures.of.util.StringUtil.printVerbose;

//todo super menuItems which contain others, so maybe one for settings etc
//todo temperature item
@Data
@NoArgsConstructor
public class CameraMenu {
    private MasterController masterController;
    private Color invisibleColor = new Color(0, 0, 0, 0);
    private boolean menuShown = true;
    private int currentItem = 0;

    private MenuItem toggleMenuItem = new MenuItem("Menu", (e -> toggleMenuAction()));
    private MenuItem rebootItem = new MenuItem("Reboot", (e -> ProcessUtil.rebootAction()));
    private MenuItem shutdownItem = new MenuItem("Shutdown", (e -> ProcessUtil.shutdownAction()));
    private MenuItem killCamItem = new MenuItem("Kill cam", (e -> CameraController.killLibCamera()));
    private MenuItem maximizeItem = new MenuItem("Maximize", (e -> RobotUtil.maximizeWindow(0)));
    //todo show only if latest.jpg exists
    private MenuItem showLastImage = new MenuItem("Last image", (e -> showImageZoomAction(3, 3, 1, 1, "latest.jpg")), (f -> ImageViewerView.destroyFrames()));
    //private MenuItem toggleWifi = new MenuItem("Last image", (e -> showImageZoomAction(3,3, 1, 1, "latest.jpg")), (f -> ImageViewerView.destroyFrames()));
    //private MenuItem toggleBlueTooth = new MenuItem("Last image", (e -> showImageZoomAction(3,3, 1, 1, "latest.jpg")), (f -> ImageViewerView.destroyFrames()));
    //private MenuItem showLastImage = new MenuItem("Saturation", (e -> cameraController.killLibCamera()));
    //private MenuItem showLastImage = new MenuItem("Sharpness", (e -> cameraController.killLibCamera()));
    //private MenuItem showLastImage = new MenuItem("Contrast", (e -> cameraController.killLibCamera()));
    // private MenuItem backlightItem = new MenuItem("Backlight", null);

    private ValueWithIndex shutterTime = masterController.cameraController.getShutterTime();
    private ValueWithIndex gain = masterController.cameraController.getGain();
    private ValueWithIndex timeBetween = masterController.cameraController.getTlTimeBetween();

    private MenuItem shutterItem = new MenuItemSetting("Shutter",shutterTime);
    private MenuItem gainItem = new MenuItemSetting("Gain",  gain, "db");
    private MenuItem timeBetweenItem = new MenuItemSetting("TL time", timeBetween);
    private MenuItem restoreDefaultsItem = new MenuItem("Restore defaults", (e -> {
        Arrays.stream(masterController.cameraController.getSettings()).forEach(ValueWithIndex::restoreDefault);
        return null;
    }));


    private MenuItem[] settings = {toggleMenuItem, shutdownItem, rebootItem, maximizeItem, killCamItem, showLastImage, timeBetweenItem, shutterItem, gainItem, restoreDefaultsItem};
    private Font labelFont = new Font(new JLabel().getFont().getName(), Font.BOLD, 20);
    private SelectedLabel selectedLabel = new SelectedLabel(settings[currentItem].getName().apply(null), labelFont);
    private BatteryLabel batteryLabel = new BatteryLabel(masterController.batteryController.buildBatteryLabelString(), labelFont);
    private CameraMenuFrame window = new CameraMenuFrame(selectedLabel, batteryLabel);

    public CameraMenu(MasterController masterController) {
        this.masterController = masterController;
    }

    private Void toggleMenuAction() {
        menuShown = !menuShown;
        window.setVisible(menuShown);

        return null;
    }

    public static Void updateSettingAction(ValueWithIndex setting) {
        CameraController.killLibCamera();
        setting.incrementIndexBy(1);

        return null;
    }

    public static String buildSettingText(String settingName, ValueWithIndex setting) {
        return settingName + " (" + StringUtil.formatIntoShorterString((long) setting.getActualValue(), NumberNotations.MS_TIME_NOTATION_SUFFIXES.getMap()) + ")";
    }

    public static String buildSettingTextStaticSuffix(String settingName, ValueWithIndex setting, String notation) {
        return settingName + " (" + setting.getActualValue() + notation+")";
    }

    public Void menuTriggerCurrentAction() {
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

    public Void menuMoveAction(int indexChange) {
        if (masterController.displayController.getClick().getState() != PinState.HIGH) {
            return null;
        }
        if (menuShown) {
            FunctionUtil.applyIfExists(getCurrentMainItem().getChangeFromInMenu(), null);
            currentItem = MathUtil.incrementByAndReturnAround(currentItem + indexChange, 0, settings.length - 1);
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
