package PWS.ui;

import javax.swing.*;
import java.awt.*;

public class ConfigFrame extends JFrame {
    Label randomLabel = new Label("Hoi Masha");
    public ConfigFrame() throws HeadlessException {
        super("Simulation Configuration");
        setUndecorated(false);
        setSize(600,400);
        setVisible(true);


        randomLabel.setSize(100,100);
        randomLabel.setLocation(100,100);
        randomLabel.setVisible(true);
        add(randomLabel);
    }
}
