package ventures.of.controller;

import com.pi4j.io.i2c.I2CFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ventures.of.view.CameraMenu;

import java.io.IOException;

@Data
@Slf4j
public class MasterController {

    public BatteryController batteryController;
    public DisplayController displayController;
    public CameraController cameraController;
    public DiscordController discordController;
    public CameraMenu cameraMenu;
    public MasterController() throws IOException, I2CFactory.UnsupportedBusNumberException {
        batteryController = new BatteryController(this);
        displayController = new DisplayController(this);
        cameraController = new CameraController(this);
        cameraMenu = new CameraMenu(this);
        try {
            log.info("Attempting discord");
            discordController = new DiscordController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
