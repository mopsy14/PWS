package PWS.simulation;

import PWS.Main;
import PWS.RunningState;
import PWS.simulation.bodies.SpaceBody;
import PWS.ui.SimulationControlFrame;
import PWS.ui.SimulationVisualizeFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Simulation {
    public static Simulation INSTANCE = null;
    Thread simulationThread = null;
    ArrayList<Thread> workerThreads = new ArrayList<>();
    public List<SpaceBody> spaceBodies = new ArrayList<>();
    ConcurrentLinkedQueue tasks = new ConcurrentLinkedQueue<>();
    SimulationControlFrame controlFrame;
    SimulationVisualizeFrame visualizeFrame;
    double stepSize = 1;
    long stepAmount = 1_000_000;

    //Configuration:
    public boolean useUI;

    public Simulation(boolean useUI) {
        INSTANCE = this;

        //Configuration:
        this.useUI = useUI;

        controlFrame = new SimulationControlFrame();
        if (useUI)
            visualizeFrame = new SimulationVisualizeFrame();
    }

    public void startSimulation() {
        if (simulationThread == null || !simulationThread.isAlive()) {
            Main.state = RunningState.SIMULATING;

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

            startWorkerThreads(4);

            for (int i = 0; i < stepAmount; i++) {
                for (SpaceBody body : spaceBodies) {
                    tasks.add(body::updateVelocity);
                }
                for (SpaceBody body : spaceBodies) {
                    tasks.add(body::updatePosition);
                }
            }

            System.out.println("Finished simulating");
        } catch (Exception e) {
            System.err.println("An error occurred on the simulating thread");
            e.printStackTrace();
        }

        simulationThread = null;
    }

    private void startWorkerThreads(int amount) {
        workerThreads.clear();
        for (int i = 0; i < amount; i++) {
            workerThreads.add(new Thread(new Worker()));
        }
    }

    public void disposeAWT() {
        if (controlFrame != null)
            controlFrame.dispose();
        if (visualizeFrame != null)
            visualizeFrame.dispose();
    }

    public double getStepSize() {
        return stepSize;
    }
}
