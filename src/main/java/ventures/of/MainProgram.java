package ventures.of;

import com.pi4j.io.i2c.I2CFactory;
import lombok.extern.slf4j.Slf4j;
import ventures.of.controller.*;
import ventures.of.util.EnvironmentVariableUtil;
import ventures.of.util.ProcessUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import static ventures.of.util.StringUtil.printVerbose;

@Slf4j
public class MainProgram {

    public static MasterController masterController;
    public static final Properties properties = new Properties();

    //todo Use the -DGPIO_TFT_BACKLIGHT=23(pinNumber) for fbcp to set pin to  hardware PWM and see if backlight can be controlled from java
    //todo replace magic timers with event hooks
    //todo add driver files in encrypted format in case they are not available in the future, they could be made available again
    //todo add controls manual
    //todo auto installing program
    //todo finish pipeline script
    static {
        try {
            properties.load(readerFromJarFile("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private final static boolean bootToSnapshot = EnvironmentVariableUtil.getPropertyBool("camera.settings.function.boot.toSnapshot");

    public static void main(String[] args) throws InterruptedException, IOException, I2CFactory.UnsupportedBusNumberException {
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
        while (updateBattery) {
            masterController.batteryController.updateBatteryInfo();
            masterController.cameraMenu.getBatteryLabel().setText(masterController.batteryController.buildBatteryLabelString());
            Thread.sleep(5000);
        }
    }

    private static BufferedReader readerFromJarFile(String path) {
        return new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(path)));
    }

}