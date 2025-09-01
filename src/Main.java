import ui.UI;

public class Main {
    public static UI ui = new UI();
    public static RunningState state = RunningState.CONFIG_SCREEN;
    public static void main(String[] args) {
        ui.init();
        while (true) {



            if (state == RunningState.CLOSING)
                break;
        }
    }
}
