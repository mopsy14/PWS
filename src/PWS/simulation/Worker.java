package PWS.simulation;

import PWS.Main;
import PWS.RunningState;

public class Worker implements Runnable {
    @Override
    public void run() {
        while (Main.state == RunningState.SIMULATING) {
            Runnable runnable = (Runnable) Simulation.INSTANCE.tasks.poll();
            if (runnable==null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                runnable.run();
            }
        }
    }
}
