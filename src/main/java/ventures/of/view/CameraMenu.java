package ventures.of.view;

import com.pi4j.io.gpio.PinState;
import lombok.Data;
import lombok.NoArgsConstructor;
import ventures.of.controller.CameraController;
import ventures.of.controller.MasterController;
import ventures.of.model.ValueWithIndex;
import ventures.of.util.*;
import ventures.of.view.menu.item.DirectMenuItem;
import ventures.of.view.menu.item.MenuItemInterface;
import ventures.of.view.menu.item.MenuItemSetting;
import ventures.of.view.menu.item.SuperMenuItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import static ventures.of.util.StringUtil.printVerbose;

//todo Hi-prio super menuItems which contain others, so maybe one for settings etc but still allow free standing menuItems
@Data
@NoArgsConstructor
public class CameraMenu {
    private final Boolean touchEnabled = EnvironmentVariableUtil.getPropertyBool("camera.hardware.display.touch");

    private MasterController masterController;
    private Color invisibleColor = new Color(0, 0, 0, 0);
    private boolean menuShown = true;
    private int currentItem = 0;

    //todo sub "SYSTEM" menu
    private DirectMenuItem rebootItem = new DirectMenuItem("Reboot", (e -> ProcessUtil.rebootAction()));
    private DirectMenuItem shutdownItem = new DirectMenuItem("Shutdown", (e -> ProcessUtil.shutdownAction()));
    private DirectMenuItem killCamItem = new DirectMenuItem("Kill cam", (e -> CameraController.killLibCamera()));
   // private DirectMenuItem toggleWifi = new DirectMenuItem("Restart Wifi", (e -> ProcessUtil.restartWifi()));
    //private MenuItem toggleBlueTooth = new MenuItem("Last image", (e -> showImageZoomAction(3,3, 1, 1, "latest.jpg")), (f -> ImageViewerView.destroyFrames()));

    //todo sub "OTHER" menu
    private DirectMenuItem toggleMenuItem = new DirectMenuItem("Menu", (e -> toggleMenuAction()));
    private DirectMenuItem maximizeItem = new DirectMenuItem("Maximize", (e -> RobotUtil.maximizeWindow(0)));
    //todo show only if latest.jpg exists
    private DirectMenuItem showLastImage = new DirectMenuItem("Last image", (e -> showImageZoomAction(3, 3, 1, 1, "latest.jpg")), (f -> ImageViewerView.destroyFrames()));

    // private MenuItem backlightItem = new MenuItem("Backlight", null);
    //todo sub "PHOTO"
    private DirectMenuItem shutterItem;
    private DirectMenuItem gainItem;
    private DirectMenuItem contrastItem;
    private DirectMenuItem timeBetweenItem;
    private DirectMenuItem restoreDefaultsItem;
    //private MenuItem showLastImage = new MenuItem("Saturation", (e -> cameraController.killLibCamera()));
    //private MenuItem showLastImage = new MenuItem("Sharpness", (e -> cameraController.killLibCamera()));

    //Touch controls
    private DirectMenuItem startVideo = new DirectMenuItem("Start video", (e -> masterController.cameraController.triggerVideo()));
    private DirectMenuItem startTimelapse = new DirectMenuItem("Start timelapse", (e -> masterController.cameraController.triggerTimelapse()));
    private DirectMenuItem startSnapshot = new DirectMenuItem("Start snapshot", (e -> masterController.cameraController.triggerTakeStill()));

    // MENU free items
    private MenuItemInterface[] settings;
    private Font labelFont;
    private SelectedLabel selectedLabel;
    private BatteryLabel infoLabel;
    private CameraMenuFrame window;

    //wip super menu
    private SuperMenuItem superMenuItem;

    public CameraMenu(MasterController masterController) {
        this.masterController = masterController;

        ValueWithIndex shutterTime = masterController.cameraController.getCs().getShutterTime();
        ValueWithIndex gain = masterController.cameraController.getCs().getGain();
        ValueWithIndex timeBetween = masterController.cameraController.getCs().getTlTimeBetween();
        ValueWithIndex contrast = masterController.cameraController.getCs().getContrast();

        shutterItem = new MenuItemSetting("Shutter",shutterTime);

        gainItem = new MenuItemSetting("Gain",  gain, "db");
        contrastItem = new MenuItemSetting("Contrast",  contrast, "");
        timeBetweenItem = new MenuItemSetting("TL time", timeBetween);
        restoreDefaultsItem = new DirectMenuItem("Restore defaults", (e -> {
            Arrays.stream(masterController.cameraController.getCs().getSettings()).forEach(ValueWithIndex::restoreDefault);
            return null;
        }));

        superMenuItem = new SuperMenuItem("PHOTO", shutterItem, gainItem, contrastItem, timeBetweenItem, restoreDefaultsItem);
        if(touchEnabled) {
            settings = new MenuItemInterface[]{toggleMenuItem, shutdownItem, rebootItem, maximizeItem, killCamItem,
                    /*toggleWifi,*/ showLastImage, timeBetweenItem, shutterItem, gainItem,
                    contrastItem, restoreDefaultsItem, startVideo, startTimelapse, startSnapshot, superMenuItem};
        }
        else {
            settings = new MenuItemInterface[]{toggleMenuItem, shutdownItem, rebootItem, maximizeItem, killCamItem,
                    /*toggleWifi,*/ showLastImage, timeBetweenItem, shutterItem, gainItem,
                    contrastItem, restoreDefaultsItem, superMenuItem};
        }
        //todo graphic for super items
        labelFont = new Font(new JLabel().getFont().getName(), Font.BOLD, 20);
        selectedLabel = new SelectedLabel(settings[currentItem].getName().apply(null), labelFont);
        infoLabel = new BatteryLabel(masterController.batteryController.buildInfoText(), labelFont);
        window = new CameraMenuFrame(selectedLabel, infoLabel);
        selectedLabel.addMouseListener( new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                menuTriggerCurrentAction();
            }
        });
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

    public MenuItemInterface getCurrentMainItem() {
        return settings[currentItem];
    }

    private Void showImageZoomAction(int dividerX, int dividerY, int x, int y, String imageName) {
        ImageViewerView.createView(dividerX, dividerY, x, y, imageName);
        return null;
    }
}
