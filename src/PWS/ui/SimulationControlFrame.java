package PWS.ui;

import PWS.Main;
import PWS.RunningState;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SimulationControlFrame extends JFrame {
    JPanel componentContainer = new JPanel();
    public SimulationControlFrame() {
        super("Simulation Control");
        setResizable(false);
        setSize(400,300);
        setLayout(null);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Main.state = RunningState.CLOSING;
                dispose();
            }
        });

        componentContainer.setSize(400,300);
        componentContainer.setLayout(null);
        add(componentContainer);
    }
}
