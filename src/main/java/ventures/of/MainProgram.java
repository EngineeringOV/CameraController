package ventures.of;

import com.pi4j.io.i2c.I2CFactory;
import lombok.extern.slf4j.Slf4j;
import ventures.of.controller.*;
import ventures.of.util.ProcessUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import static ventures.of.util.StringUtil.printVerbose;

@Slf4j
public class MainProgram {


    public static BatteryController batteryController;
    public static DisplayController displayController;
    public static CameraController cameraController;
    public static DiscordController discordController;
    public static CameraMenu cameraMenu;

    public static final Properties properties = new Properties();

//    public static boolean wifiShouldBeEnabled = false;
//    public static boolean bluetoothShouldBeEnabled = false;
    //todo add how to add your mobile WIFI to WPA_Supplicant in readme
    //todo add how to add your config.properties  in readme
    //todo Use the -DGPIO_TFT_BACKLIGHT=23(pinNumber) for fbcp to set pin to  hardware PWM and see if backlight can be controlled from java
    //todo Add underclocking to README (as it is in config.txt on pi)
    //todo make a "master controller" layer to reduce static stuff
    //todo replace magic timers with event hooks

    public static void main(String[] args) throws InterruptedException, IOException, I2CFactory.UnsupportedBusNumberException {
        boolean updateBattery = true;
        long startTime = System.nanoTime();
        printVerbose("Starting camera controller", true);
        ProcessUtil.ledSetManualMode();
        ProcessUtil.ledOff();
        try {
            //todo use different way of finding file when moved to a non static context
            properties.load(new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        batteryController = new BatteryController();
        displayController = new DisplayController();
        cameraController = new CameraController();
        try {
            // this lib is sloooow so starting it in a thread allows us to do other stuff meanwhile, might just start it last though as it's extremely loosly coupled
            // this reduces start time from ~15s to ~4s though
            new Thread(() -> discordController = new DiscordController());
        } catch (Exception e) {
            e.printStackTrace();
        }

        cameraMenu = new CameraMenu();
        printVerbose("Loaded", true);
        // Start still mode to quicker be able to take stills from boot
        cameraController.triggerTakeStill(13000);
        long currentTime = System.nanoTime();
        log.warn("Time to start: " + (float)((currentTime-startTime)/1000000000) + "S");


        while (updateBattery) {
            batteryController.updateBatteryInfo();
            Thread.sleep(1000);
        }
    }

}