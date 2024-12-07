package ventures.of.hardware.bms;

import com.pi4j.io.i2c.I2CFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ventures.of.controller.MasterController;

import java.io.IOException;

@EqualsAndHashCode(callSuper = true)
@Data
public class BatteryControllerWaveshareZeroUPSHat extends BatteryControllerWaveshare{

    public BatteryControllerWaveshareZeroUPSHat(MasterController masterController) throws IOException, I2CFactory.UnsupportedBusNumberException {
        super(masterController, 0x43);
        lastWattOut = getWattage(device);
        lastAmpOut = getAmperage(device);
        lastBattery = getBattery(device);

        // Register addresses for battery voltage and current
        CONFIG_REGISTER = 0x00;
        SHUNT_VOLTAGE_REGISTER = 0x01;
        BUS_VOLTAGE_REGISTER = 0x02;
        BUS_WATTAGE_REGISTER = 0x03;
        BUS_CURRENT_REGISTER = 0x04;
        CALIBRATION_REGISTER = 0x05;

        CALIBRATION_VALUE = 26868;
    }
}
