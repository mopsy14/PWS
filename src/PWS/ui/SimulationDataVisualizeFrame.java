package PWS.ui;

import PWS.Main;
import PWS.RunningState;
import PWS.simulation.SimulationData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SimulationDataVisualizeFrame extends JFrame {

    private static final double rStarsMin = 7e8;
    private static final double rStarsMax = 2e11;
    private static final double rStarsRange = rStarsMax - rStarsMin;
    private static final double rPlanetMin = 8e9;
    private static final double rPlanetMax = 1e12;
    private static final double rPlanetRange = rPlanetMax - rPlanetMin;

    JPanel componentContainer = new JPanel() {
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            synchronized (Main.currentDataSet) {
                synchronized (Main.newDataSet) {
                    for (SimulationData data : Main.currentDataSet) {
                        g.setColor(new Color(0.0f, Math.clamp((float) (1.0 - Math.abs(Main.referenceLightIntensity - data.receivedLight()) / Main.referenceLightIntensity), 0, 1), 0.0f));
                        g.fillOval((int) (((data.rPlanet() - rPlanetMin) / rPlanetRange) * getWidth()), (int) ((1 - (data.rStars() - rStarsMin) / rStarsRange) * getHeight()), 10, 10);
                    }
                    for (SimulationData data : Main.newDataSet) {
                        g.setColor(new Color(0.0f, 0.0f, 1.0f));
                        g.fillOval((int) (((data.rPlanet() - rPlanetMin) / rPlanetRange) * getWidth()), (int) ((1 - (data.rStars() - rStarsMin) / rStarsRange) * getHeight()), 10, 10);
                    }
                }
            }
        }
    };
    public SimulationDataVisualizeFrame() {
        super("Simulation Data Visualization");
        setResizable(false);
        setSize(1500,900);
        setLocation(100,0);
        setLayout(null);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Main.state = RunningState.CLOSING;
                dispose();
            }
        });

        componentContainer.setSize(1500,900);
        componentContainer.setLayout(null);
        add(componentContainer);
    }

    public void updateVisualization() {
        componentContainer.repaint();
    }
}
