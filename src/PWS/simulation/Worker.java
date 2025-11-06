package PWS.simulation;

public class Worker implements Runnable {
    private Simulation simulation;
    public Worker(Simulation simulation) {
        this.simulation = simulation;
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public void run() {
        while (simulation.getSimulationState() == SimulationState.STARTING || simulation.getSimulationState() == SimulationState.RUNNING) {
            Runnable runnable = simulation.tasks.poll();
            if (runnable != null) {
                try {
                    runnable.run();
                } finally {
                    simulation.remainingTasks.countDown();
                }
            }
        }
    }
}
