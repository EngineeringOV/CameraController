package ventures.of.hardware.bms;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ventures.of.controller.MasterController;

import java.io.IOException;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class BatteryControllerWaveshare extends BatteryControllerInterface{

    // Register addresses for battery voltage and current
    int CONFIG_REGISTER = 0x00;
    int SHUNT_VOLTAGE_REGISTER = 0x01;
    int BUS_VOLTAGE_REGISTER = 0x02;
    int BUS_WATTAGE_REGISTER = 0x03;
    int BUS_CURRENT_REGISTER = 0x04;
    int CALIBRATION_REGISTER = 0x05;
    int CALIBRATION_VALUE = 4096;

    private long timeToUpdateSeconds = 10;
    private long lastUpdate = 0;

    protected BatteryControllerWaveshare(MasterController masterController, int I2C_ADDRESS) throws IOException, I2CFactory.UnsupportedBusNumberException {
        super(masterController, I2C_ADDRESS);
        this.masterController = masterController;
    }

    @Override
    double getBattery(I2CDevice device) throws IOException {
        return ((getVoltage(device) - 3) / 1.2f * 100);
    }

    @Override
    double getVoltage(I2CDevice device) throws IOException {
        return (getValue(device, BUS_VOLTAGE_REGISTER) >> 3) * 0.004d;
    }

    @Override
    double getWattage(I2CDevice device) throws IOException {
        // device.write(CALIBRATION_REGISTER, toByte(CALIBRATION_VALUE));
        return getValue(device, BUS_WATTAGE_REGISTER) * 0.003048d;
    }

    @Override
    double getAmperage(I2CDevice device) throws IOException {
        return (getValue(device, BUS_CURRENT_REGISTER) / 1000d) * 0.1524d;
    }

}
