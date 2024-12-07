package ventures.of.hardware.bms;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import lombok.Data;
import lombok.NoArgsConstructor;
import ventures.of.controller.MasterController;
import ventures.of.hardware.InfoText;

import java.io.IOException;

@Data
@NoArgsConstructor
public class BatteryControllerInterface implements InfoText {
    double lastBattery = 0;
    double lastAmpOut = 0;
    double lastWattOut = 0;
    MasterController masterController;
    I2CBus bus;
    I2CDevice device;

    private long timeToUpdateSeconds = 10;
    private long lastUpdate = 0;

    public BatteryControllerInterface(MasterController masterController, int i2C_address) throws IOException, I2CFactory.UnsupportedBusNumberException {
        this.masterController = masterController;
        bus = I2CFactory.getInstance(I2CBus.BUS_1);
        device = bus.getDevice(i2C_address);
    }

    public String updateInfoText() throws IOException {
        //if (System.currentTimeMillis() - lastUpdate > timeToUpdateSeconds * 1000) {
            lastUpdate = System.currentTimeMillis();

            lastWattOut = getWattage(device);
            lastAmpOut = getAmperage(device);
            lastBattery = getBattery(device);
      //  }
        return buildInfoText();
    }

     double getBattery(I2CDevice device) throws IOException {
         throw new IllegalStateException("BatteryControllerInterface should be inherited and not used directly");
     }
    double getVoltage(I2CDevice device) throws IOException {
        throw new IllegalStateException("BatteryControllerInterface should be inherited and not used directly");
    }
    double getWattage(I2CDevice device) throws IOException {
        throw new IllegalStateException("BatteryControllerInterface should be inherited and not used directly");
    }
    double getAmperage(I2CDevice device) throws IOException {
        throw new IllegalStateException("BatteryControllerInterface should be inherited and not used directly");
    }

    int getValue(I2CDevice device, int register) throws IOException {
        int result = 0;
        byte[] buffer = new byte[2];
        device.read(register, buffer, 0, 2);

        for (byte b : buffer) {
            result = (result << 8) | (b & 0xFF);
        }
        if (result > 32767) {
            result -= 65535;
        }

        return result;
    }

    public String buildInfoText() {
        return getFormat(lastBattery, "%") + " (" + getFormat(lastWattOut, "W") + ")";
    }

    public String getFormat(double input, String symbol) {
        String asString = String.valueOf(input);
        if (!asString.contains(".")) {
            return asString + symbol;
        } else {
            return asString.substring(0, asString.indexOf(".") + 2) + symbol;
        }
    }

    public static BatteryControllerInterface getBMSType(MasterController masterController, String batteryFrom) throws IOException, I2CFactory.UnsupportedBusNumberException {
        switch (batteryFrom){
            case("WAVESHARE_3S_5A"):
                return new BatteryControllerWaveshare3S5AUPS(masterController);
            case("WAVESHARE_ZERO_UPS"):
                return new BatteryControllerWaveshareZeroUPSHat(masterController);
            case("NONE"):
                return null;
            default:
                throw new IllegalStateException("Invalid BMS type (camera.hardware.battery.from)");
        }

    }
}
