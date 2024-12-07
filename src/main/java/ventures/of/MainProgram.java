package ventures.of;

import com.pi4j.io.i2c.I2CFactory;
import lombok.extern.slf4j.Slf4j;
import ventures.of.controller.MasterController;
import ventures.of.util.EnvironmentVariableUtil;
import ventures.of.util.ProcessUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;

import static ventures.of.util.StringUtil.printVerbose;

@Slf4j
public class MainProgram {

    public static MasterController masterController;
    public static final Properties properties = new Properties();

    //todo Hi-prio: auto installing script
    //  "uname -m" to check 32vs64 bit  "armv6", "armv7l" for 32 bit "aarch64" for 64 bit
    // "cat /etc/os-release" OS check under "VERSION_CODENAME=xxx", zero needs bullseye or older, rpi5 needs Bookworm or newer


    //todo Hi-prio easy: discord whitelist
    //todo replace magic timers with event hooks
    //todo add driver files in encrypted format in case they are not available in the future, they could be made available again
    //todo add buttons, touch, and support for more screens
    //todo Lo-prio: add controls manual
    //todo Lo-prio: Use the -DGPIO_TFT_BACKLIGHT=23(pinNumber) for fbcp to set pin to  hardware PWM and see if backlight can be controlled from java
    static {
        try {
            EnvironmentVariableUtil.readerFromConfigWithFallback(properties);
            properties.list(System.out);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
    private final static boolean bootToSnapshot = EnvironmentVariableUtil.getPropertyBool("camera.settings.function.boot.toSnapshot");
    private final static List<String> infoText = EnvironmentVariableUtil.getPropertyStringArrayList("camera.settings.infoText.options");
    private final static long infoTextTimer = EnvironmentVariableUtil.getPropertyLong("camera.settings.infoText.timer");

    public static void main(String[] args) throws IOException, I2CFactory.UnsupportedBusNumberException {
        boolean updateBattery = true;
        long startTime = System.currentTimeMillis();
        printVerbose("Starting camera controller", true);
        ProcessUtil.ledSetManualMode();
        ProcessUtil.ledOff();

        masterController = new MasterController();
        printVerbose("Loaded", true);
        if(bootToSnapshot) {
            masterController.cameraController.triggerTakeStill(13000, false);
        }
        long currentTime = System.currentTimeMillis();
        log.warn("Time to start: " + (float) ((currentTime - startTime) / 1000) + "S");

        int infoLabelDisplayIndex = 0;
        while (true) {
            String currentLabel = infoText.get(infoLabelDisplayIndex).toLowerCase();
            //System.out.println("currentLabel = " + currentLabel);
            // Update the info label based on the current label type
            switch (currentLabel) {
                case "BATTERY":
                    masterController.cameraMenu.getInfoLabel().setText(
                            masterController.batteryController.updateInfoText()
                    );
                    break;
                case "TEMPERATURE":
                    masterController.cameraMenu.getInfoLabel().setText(
                            masterController.tempController.updateInfoText()
                    );
                    break;
            }

            infoLabelDisplayIndex = (infoLabelDisplayIndex + 1) % infoText.size();

            try {
                Thread.sleep(infoTextTimer);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }


}