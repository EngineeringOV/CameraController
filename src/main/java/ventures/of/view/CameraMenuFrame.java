package ventures.of.view;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.swing.*;
import java.awt.*;

@Data
@EqualsAndHashCode(callSuper=true)
public class CameraMenuFrame extends Frame {

        public CameraMenuFrame(JLabel selectedLabel, JLabel infoLabel) {
            super();
            JLabel test = new JLabel("test ");
            this.setAlwaysOnTop(true);
            this.setBackground(Color.black);
            this.setUndecorated(true);
            this.setOpacity(0);
            GridLayout gridLayout = new GridLayout(2, 0);
            this.setLayout(gridLayout);
            this.add(selectedLabel);
            this.add(infoLabel);
            //this.add(test);
            this.pack();
            this.setLocation(10, 215);
            this.setVisible(true);
        }
}
