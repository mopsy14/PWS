package PWS.ui;

import PWS.Main;
import PWS.RunningState;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SimulationVisualizeFrame extends JFrame {
    JPanel componentContainer = new JPanel();
    public SimulationVisualizeFrame() {
        super("Simulation Visualization");
        setResizable(false);
        setSize(1000,700);
        setLocation(400,0);
        setLayout(null);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Main.state = RunningState.CLOSING;
                dispose();
            }
        });

        componentContainer.setSize(1000,700);
        componentContainer.setLayout(null);
        add(componentContainer);
    }
}
