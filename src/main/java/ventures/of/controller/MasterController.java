package ventures.of.controller;

import com.pi4j.io.i2c.I2CFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ventures.of.hardware.bms.BatteryControllerInterface;
import ventures.of.util.EnvironmentVariableUtil;
import ventures.of.view.CameraMenu;

import java.io.IOException;

@Data
@Slf4j
public class MasterController {
    String batteryFrom = EnvironmentVariableUtil.getPropertyString("camera.hardware.battery.from");
    String discordToken = EnvironmentVariableUtil.getPropertyString("discord.api.token");
    boolean discordEnabled = EnvironmentVariableUtil.getPropertyBool("discord.api.enabled");

    public BatteryControllerInterface batteryController;
    public TempController tempController;
    public DisplayController displayController;
    public CameraController cameraController;
    public DiscordController discordController;
    public CameraMenu cameraMenu;
    public MasterController() throws IOException, I2CFactory.UnsupportedBusNumberException {
        batteryController = BatteryControllerInterface.getBMSType(this, batteryFrom);
        tempController = new TempController(this);
        displayController = new DisplayController(this);
        cameraController = new CameraController(this);
        cameraMenu = new CameraMenu(this);
        if(discordEnabled && (discordToken == null || !discordToken.trim().isEmpty())) {
        try {
            log.info("Attempting discord");
            discordController = new DiscordController(this, discordToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    }

}
