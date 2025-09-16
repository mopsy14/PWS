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
    JPanel componentContainer = new JPanel() {
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for(SpaceBody body : Simulation.INSTANCE.spaceBodies) {
                    if (body instanceof Star) {
                        g.setColor(Color.YELLOW);
                        g.fillOval((int) (body.x / 1e9) + g.getClipBounds().width / 2, -(int) (body.y / 1e9) + g.getClipBounds().height / 2, (int)(10*((body.z+2e11) / 2e11)), (int)(10*((body.z+2e11) / 2e11)));
                    } else {
                        g.setColor(Color.GREEN);
                        g.fillOval((int) (body.x / 1e9) + g.getClipBounds().width / 2, -(int) (body.y / 1e9) + g.getClipBounds().height / 2, (int)(10*((body.z+2e11) / 2e11)), (int)(10*((body.z+2e11)  / 2e11)));
                    }
            }
        }
    };
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

    public void updateVisualization() {
        componentContainer.repaint();
    }
}
