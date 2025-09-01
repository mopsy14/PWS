package ui;

public class UI {

    public ConfigWindow configWindow;

    public UI() {
        configWindow = new ConfigWindow();
    }

    public void init() {
        configWindow.setSize(300, 200);
        configWindow.setVisible(true);
    }

}
