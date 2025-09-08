package PWS.ui;

import javax.swing.*;
import java.awt.*;

public class ConfigFrame extends JFrame {
    JPanel componentContainer = new JPanel();
    JButton startButton = new JButton("Start Simulation");
    public ConfigFrame() throws HeadlessException {
        super("Simulation Configuration");
        setResizable(false);
        setSize(1000,700);
        setLayout(null);
        setVisible(true);

        componentContainer.setSize(1000,700);
        componentContainer.setLayout(null);
        add(componentContainer);

        startButton.setSize(200,25);
        startButton.setLocation(400,620);
        componentContainer.add(startButton);
    }
}
