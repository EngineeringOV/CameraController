package ventures.of.view;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.swing.*;
import java.awt.*;

@Data
@EqualsAndHashCode(callSuper=true)
public class BatteryLabel extends JLabel {

    public BatteryLabel(String initialText, Font labelFont) {
        super(initialText);
        this.setForeground(Color.WHITE);
        this.setBackground(Color.BLACK);
        this.setFont(labelFont);
        this.setOpaque(true);
    }
}
