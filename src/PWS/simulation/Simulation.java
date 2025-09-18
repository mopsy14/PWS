package PWS.simulation;

import PWS.Main;
import PWS.RunningState;
import PWS.simulation.bodies.Planet;
import PWS.simulation.bodies.SpaceBody;
import PWS.simulation.bodies.Star;
import PWS.ui.SimulationControlFrame;
import PWS.ui.SimulationVisualizeFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Simulation {
    public static Simulation INSTANCE = null;
    Thread simulationThread = null;
    ArrayList<Thread> workerThreads = new ArrayList<>();
    public List<SpaceBody> spaceBodies = new ArrayList<>();
    ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<>();
    SimulationControlFrame controlFrame;
    SimulationVisualizeFrame visualizeFrame;
    volatile CountDownLatch remainingTasks = null;
    double stepSize = 1;
    long stepAmount = 15_768_000;

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
            spaceBodies.clear();
            spaceBodies.add(new Planet(1.495e11,0,0,5.9722e24,6.371e6,0,2.978e4,0));
            spaceBodies.add(new Star(0,0,0,1.988416e30,6.955e8,0,0,0,1000));

            simulationThread = new Thread("Simulation thread") {
                @Override
                public void run() {
                    runSimulation();
                }
            };
            simulationThread.start();
        } else {
            System.err.println("Cannot start simulating thread because the thread is still running");
        }
    }


    private void runSimulation() {
        try {
            System.out.println("Started simulating");

            startWorkerThreads(4);

            for (int i = 0; i < stepAmount; i++) {
                if (i%10000==0) {
                    visualizeFrame.updateVisualization();

                    if (i % 1000000 == 0) {
                        System.out.println("Iteration " + i / 1000000 + "e+6");
                    }
                }
                remainingTasks = new CountDownLatch(spaceBodies.size());
                for (SpaceBody body : spaceBodies) {
                    tasks.add(body::updateVelocity);
                }
                if (!remainingTasks.await(10, TimeUnit.SECONDS))
                    throw new TimeoutException("Tasks took more than allowed 10 seconds to execute");

                remainingTasks = new CountDownLatch(spaceBodies.size());
                for (SpaceBody body : spaceBodies) {
                    tasks.add(body::updatePosition);
                }
                if (!remainingTasks.await(10, TimeUnit.SECONDS))
                    throw new TimeoutException("Tasks took more than allowed 10 seconds to execute");

                if (i%100==0) {
                    remainingTasks = new CountDownLatch(spaceBodies.size());
                    for (SpaceBody body : spaceBodies) {
                        tasks.add(body::updateLighting);
                    }
                    if (!remainingTasks.await(10, TimeUnit.SECONDS))
                        throw new TimeoutException("Tasks took more than allowed 10 seconds to execute");
                }
            }
            System.out.println("Finished simulating");
            System.out.println("Planet received "+((Planet)spaceBodies.getFirst()).getReceivedLight()+"light");
            Main.state = RunningState.CLOSING;
        } catch (Exception e) {
            System.err.println("An error occurred on the simulating thread");
            e.printStackTrace();
        }

        simulationThread = null;
    }

    private void startWorkerThreads(int amount) {
        workerThreads.clear();
        for (int i = 0; i < amount; i++) {
            workerThreads.add(new Thread(new Worker(),"Worker-"+i));
        }
        for (Thread thread : workerThreads) {
            thread.start();
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
