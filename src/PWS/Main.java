package PWS;

import PWS.ui.UI;

public class Main {
    public static UI ui = new UI();
    public static volatile RunningState state = RunningState.CONFIG_SCREEN;

    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args) {
        Logger.initLoggers();

        try {
            ui.init();

            while (true) {


                if (state == RunningState.CLOSING)
                    break;
            }

        } catch (Exception e) {
            System.err.println("An error occurred, closing application");
            e.printStackTrace();
        }

        Logger.closeLoggerFileOutputStream();
    }
}
