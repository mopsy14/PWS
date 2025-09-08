package PWS.simulation;

public class Worker extends Thread {
    @Override
    public void run() {
        while (true) {
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
