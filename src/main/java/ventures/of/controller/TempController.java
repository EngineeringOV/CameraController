package ventures.of.controller;

import com.pi4j.io.i2c.I2CFactory;
import lombok.Data;
import ventures.of.hardware.InfoText;
import ventures.of.util.ProcessUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Data
public class TempController implements InfoText {


    private final MasterController masterController;

    private String lastTemp;

    private long timeToUpdateSeconds = 10;
    private long lastUpdate = 0;

    public TempController(MasterController masterController) {
        this.masterController = masterController;
    }

    public String buildInfoText() {
        return lastTemp+"°C";
    }

    @Override
    public String updateInfoText() throws IOException {
       // if (System.currentTimeMillis() - lastUpdate > timeToUpdateSeconds * 1000) {
            lastUpdate = System.currentTimeMillis();
            Process p = Runtime.getRuntime().exec("vcgencmd measure_temp");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = bufferedReader.readLine();

            lastTemp = line.substring(5);

      //  }
        return buildInfoText();
    }
}
