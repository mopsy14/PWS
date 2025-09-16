package PWS.simulation;

import PWS.Main;
import PWS.RunningState;

public class Worker implements Runnable {

    @Override
    public void run() {
        while (Main.state == RunningState.SIMULATING) {
            Runnable runnable = Simulation.INSTANCE.tasks.poll();
            if (runnable != null) {
                try {
                    runnable.run();
                } finally {
                    Simulation.INSTANCE.remainingTasks.countDown();
                }
            }
        }
    }
}
