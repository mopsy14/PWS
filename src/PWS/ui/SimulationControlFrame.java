package PWS.ui;

import PWS.Main;
import PWS.RunningState;
import PWS.simulation.Simulation;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SimulationControlFrame extends JFrame {
    private final Simulation simulation;
    JPanel componentContainer = new JPanel();
    public SimulationControlFrame(Simulation simulation) {
        super("Simulation Control");
        this.simulation = simulation;
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
