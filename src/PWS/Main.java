package PWS;

import PWS.simulation.Simulation;
import PWS.ui.ConfigFrame;

public class Main {
    public static volatile RunningState state = RunningState.CONFIG_SCREEN;
    public static ConfigFrame configFrame;

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
        if (Simulation.INSTANCE != null) {
            Simulation.INSTANCE.disposeAWT();
        }

        Logger.closeLoggerFileOutputStream();
    }
}
