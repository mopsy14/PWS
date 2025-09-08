package PWS.ui;

import PWS.Main;
import PWS.RunningState;
import PWS.simulation.Simulation;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ConfigFrame extends JFrame {
    JPanel componentContainer = new JPanel();
    JButton startButton = new JButton("Start Simulation");
    JCheckBox enableSimulationRenderingBox = new JCheckBox("Enable Simulation Rendering");
    public ConfigFrame() {
        super("Simulation Configuration");
        setResizable(false);
        setSize(1000,700);
        setLayout(null);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Main.state = RunningState.CLOSING;
                e.getWindow().dispose();
            }
        });

        componentContainer.setSize(1000,700);
        componentContainer.setLayout(null);
        add(componentContainer);

        enableSimulationRenderingBox.setSize(200,30);
        enableSimulationRenderingBox.setLocation(50,50);
        componentContainer.add(enableSimulationRenderingBox);

        startButton.setSize(200,30);
        startButton.setLocation(400,620);
        startButton.addActionListener((event)->{
            dispose();
            Simulation simulation = new Simulation(enableSimulationRenderingBox.isSelected());
            simulation.startSimulation();
        });
        componentContainer.add(startButton);
    }
}
