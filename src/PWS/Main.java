package PWS;

import PWS.simulation.Simulation;
import PWS.ui.ConfigFrame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static volatile RunningState state = RunningState.CONFIG_SCREEN;
    public static ConfigFrame configFrame;
    public static final List<Simulation> simulationInstances = Collections.synchronizedList(new ArrayList<>());

    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args) {
        Logger.initLoggers();

        try {
            configFrame = new ConfigFrame();

            while (true) {


                if (state == RunningState.CLOSING)
                    break;
            }

        } catch (Exception e) {
            System.err.println("An error occurred, closing application");
            e.printStackTrace();
        }

        configFrame.dispose();
        synchronized (simulationInstances) {
            for (Simulation simulation : simulationInstances) {
                simulation.disposeAWT();
            }
        }

        Logger.closeLoggerFileOutputStream();
    }
}
