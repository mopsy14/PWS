package PWS.ui;

import PWS.Main;
import PWS.RunningState;
import PWS.simulation.Simulation;
import PWS.simulation.SimulationStartData;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

public class ConfigFrame extends JFrame {
    JPanel componentContainer = new JPanel();
    JButton startButton = new JButton("Start Simulation");
    JCheckBox enableSimulationRenderingBox = new JCheckBox("Enable Simulation Rendering");
    Random random = new Random();
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
            //dispose();
            for (int i = 0; i < 10; i++) {
                Simulation simulation = new Simulation(enableSimulationRenderingBox.isSelected());
                synchronized (Main.simulationInstances) {
                    if (Main.state == RunningState.CLOSING)
                        return;
                    Main.simulationInstances.add(simulation);
                }
                Main.runningSimulations.addAndGet(1);
                simulation.startSimulation(generateStartData());
            }
        });
        componentContainer.add(startButton);
    }
    private SimulationStartData generateStartData() {
        double rStars = random.nextDouble(1e9,5e10);
        double rPlanet = random.nextDouble(2*rStars,5e11);
        return new SimulationStartData(rPlanet, rStars);
    }
}
