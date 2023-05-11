package ventures.of.controller;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import lombok.Data;
import ventures.of.MainProgram;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

@Data
public class BatteryController {

    // I2C address of the UPS board
    private static final int I2C_ADDRESS = 0x43;

    // Register addresses for battery voltage and current
    private static final int CONFIG_REGISTER = 0x00;
    private static final int SHUNT_VOLTAGE_REGISTER = 0x01;
    private static final int BUS_VOLTAGE_REGISTER = 0x02;
    private static final int BUS_WATTAGE_REGISTER = 0x03;
    private static final int BUS_CURRENT_REGISTER = 0x04;
    private static final int CALIBRATION_REGISTER = 0x05;

    private static final int CALIBRATION_VALUE = 26868;
    private I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
    private I2CDevice device = bus.getDevice(I2C_ADDRESS);

    private double lastBattery = 0;
    private double lastAmpOut = 0;
    private double lastWattOut = 0;

    private long timeToUpdateSeconds = 10;
    private long lastUpdate = 0;

    public BatteryController() throws IOException, I2CFactory.UnsupportedBusNumberException {
        lastWattOut = getWattage(device, BUS_WATTAGE_REGISTER);
        lastAmpOut = getAmperage(device, BUS_CURRENT_REGISTER);
        lastBattery = getBattery(device, BUS_VOLTAGE_REGISTER);
    }

    public void updateBatteryInfo() throws IOException {
        if (System.currentTimeMillis() - lastUpdate > timeToUpdateSeconds * 1000) {
            lastUpdate = System.currentTimeMillis();

            lastWattOut = getWattage(device, BUS_WATTAGE_REGISTER);
            lastAmpOut = getAmperage(device, BUS_CURRENT_REGISTER);
            lastBattery = getBattery(device, BUS_VOLTAGE_REGISTER);
            MainProgram.cameraMenu.getBatteryLabel().setText(buildBatteryLabelString());
        }
    }

    /*
            public static void getBatteryInfo() throws IOException, I2CFactory.UnsupportedBusNumberException {

            ("shunt voltage: " + roundToThreeDecimals((getValue(device, SHUNT_VOLTAGE_REGISTER) >> 3) * 0.004) + "V");
            ("Voltage bus: " + roundToThreeDecimals(((getValue(device, BUS_VOLTAGE_REGISTER) >> 3) * 0.004)) + "V");
            ("Power bus: " + roundToThreeDecimals((getValue(device, BUS_WATTAGE_REGISTER) * 0.003048)) + "W");
            ("Current bus: " + roundToThreeDecimals(((getValue(device, BUS_CURRENT_REGISTER) / 1000f) * 0.1524)) + "mAh");
            ("Battery: " + roundToThreeDecimals((((getValue(device, BUS_VOLTAGE_REGISTER) >> 3) * 0.004)-3) / 1.2 * 100) + "%");
        }

     */
    private static double getBattery(I2CDevice device, int register) throws IOException {
        return ((getVoltage(device, register) - 3) / 1.2f * 100);
    }
    private static double getVoltage(I2CDevice device, int register) throws IOException {
        return (getValue(device, register) >> 3) * 0.004d;
    }
    private static double getWattage(I2CDevice device, int register) throws IOException {
       // device.write(CALIBRATION_REGISTER, toByte(CALIBRATION_VALUE));
        return getValue(device, register) * 0.003048d;
    }
    private static double getAmperage(I2CDevice device, int register) throws IOException {
        return (getValue(device, register) / 1000d) * 0.1524d;
    }

    private static int getValue(I2CDevice device, int register) throws IOException {
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

    public String buildBatteryLabelString() {
        return getFormat(lastBattery, "%") + " (" +getFormat(lastWattOut, "W")+")";
    }

    public String getFormat(double input, String symbol) {
        String asString = String.valueOf(input);
        if (!asString.contains(".")) {
            return asString + symbol;
        } else {
            return asString.substring(0, asString.indexOf(".") + 2) + symbol;
        }
    }

private static byte[] toByte(Integer i) {
    return BigInteger.valueOf(i).toByteArray();
}
}
