package PWS.ui;

import PWS.Main;
import PWS.RunningState;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class UI {

    public ConfigFrame configFrame;

    public void init() {
        configFrame = new ConfigFrame();


        configFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Main.state = RunningState.CLOSING;
                e.getWindow().dispose();
            }
        });
    }

}
