package PWS.ui;

import PWS.Main;
import PWS.RunningState;
import PWS.simulation.Simulation;
import PWS.simulation.bodies.SpaceBody;
import PWS.simulation.bodies.Star;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SimulationVisualizeFrame extends JFrame {
    private final Simulation simulation;

    JPanel componentContainer = new JPanel() {
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for (SpaceBody body : simulation.spaceBodies) {
                    if (body instanceof Star) {
                        g.setColor(Color.YELLOW);
                        g.fillOval((int) (body.getX() / 1e9) + g.getClipBounds().width / 2, -(int) (body.getY() / 1e9) + g.getClipBounds().height / 2, (int)(10*((body.getZ()+2e11) / 2e11)), (int)(10*((body.getZ()+2e11) / 2e11)));
                    } else {
                        g.setColor(Color.GREEN);
                        g.fillOval((int) (body.getX() / 1e9) + g.getClipBounds().width / 2, -(int) (body.getY() / 1e9) + g.getClipBounds().height / 2, (int)(10*((body.getZ()+2e11) / 2e11)), (int)(10*((body.getZ()+2e11)  / 2e11)));
                    }
            }
        }
    };
    public SimulationVisualizeFrame(Simulation simulation) {
        super("Simulation Visualization");
        this.simulation = simulation;
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

    public void updateVisualization() {
        componentContainer.repaint();
    }
}
