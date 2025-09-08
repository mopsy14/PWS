package PWS.simulation;

import PWS.simulation.bodies.SpaceBody;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Simulation {
    public static Simulation INSTANCE = null;
    Thread simulationThread = null;
    List<Thread> workerThreads = new ArrayList<>();
    public List<SpaceBody> spaceBodies = new ArrayList<>();
    ConcurrentLinkedQueue tasks = new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue newTasks = new ConcurrentLinkedQueue<>();

    public Simulation() {
        INSTANCE = this;
        workerThreads.add(new Worker());
    }

    public void startSimulation() {
        if (simulationThread == null || !simulationThread.isAlive()) {
            simulationThread = new Thread("Simulation thread") {
                @Override
                public void run() {
                    runSimulation();
                }
            };
        } else {
            System.err.println("Cannot start simulating thread because the thread is still running");
        }
    }


    private void runSimulation() {
        try {
            System.out.println("Started simulating");



            System.out.println("Finished simulating");
        } catch (Exception e) {
            System.err.println("An error occurred on the simulating thread");
            e.printStackTrace();
        }

        simulationThread = null;
    }
}
