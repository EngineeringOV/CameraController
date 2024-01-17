package ventures.of.view;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.swing.*;
import java.awt.*;

@Data
@EqualsAndHashCode(callSuper=true)
public class CameraMenuFrame extends Frame {

    public CameraMenuFrame(JLabel selectedLabel, JLabel batteryLabel) {
        super();
        this.setAlwaysOnTop(true);
        this.setBackground(Color.black);
        this.setUndecorated(true);
        this.setOpacity(0);
        this.setLayout(new GridLayout(2, 0));
        this.add(selectedLabel);
        this.add(batteryLabel);
        this.pack();
        this.setLocation(10, 215);
        this.setVisible(true);
    }
}
