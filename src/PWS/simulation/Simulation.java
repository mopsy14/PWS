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
    List<Thread> workerThreads = new ArrayList<>();
    public List<SpaceBody> spaceBodies = new ArrayList<>();
    ConcurrentLinkedQueue tasks = new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue newTasks = new ConcurrentLinkedQueue<>();
    SimulationControlFrame controlFrame;
    SimulationVisualizeFrame visualizeFrame;

    //Configuration:
    public boolean useUI;

    public Simulation(boolean useUI) {
        INSTANCE = this;

        //Configuration:
        this.useUI = useUI;

        controlFrame = new SimulationControlFrame();
        if (useUI)
            visualizeFrame = new SimulationVisualizeFrame();
        workerThreads.add(new Worker());
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



            System.out.println("Finished simulating");
        } catch (Exception e) {
            System.err.println("An error occurred on the simulating thread");
            e.printStackTrace();
        }

        simulationThread = null;
    }

    public void disposeAWT() {
        if (controlFrame != null)
            controlFrame.dispose();
        if (visualizeFrame != null)
            visualizeFrame.dispose();
    }
}
