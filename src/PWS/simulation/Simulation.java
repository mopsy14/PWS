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

            setupSpaceBodies();

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

    private void setupSpaceBodies() {
        spaceBodies.add(new Star(2e10,0,0,1e30,6.955e8,0,70749.50934329,0,1000));
        spaceBodies.add(new Star(-1e10,0,0,5e30,6.955e8,0,-23583.23478963,0,1000));

        spaceBodies.add(new Planet(2.5e11,0,0,5.972e24,6.371e6,0,32656.66327653,0));


        double totalMass = 0.0;
        double baryCentrumX = 0.0;
        double baryCentrumY = 0.0;
        double baryCentrumZ = 0.0;
        for (SpaceBody body : spaceBodies) {
            totalMass += body.getMass();
        }
        for (SpaceBody body : spaceBodies) {
            baryCentrumX += body.getX() * body.getMass() / totalMass;
            baryCentrumY += body.getY() * body.getMass() / totalMass;
            baryCentrumZ += body.getZ() * body.getMass() / totalMass;
        }

        boolean foundFirstStar = false;
        for (SpaceBody self : spaceBodies) {
            double dbx = self.getX() - baryCentrumX;
            double dby = self.getY() - baryCentrumY;
            double dbz = self.getZ() - baryCentrumZ;
            double Fx = 0.0;
            double Fy = 0.0;
            double Fz = 0.0;
            for (SpaceBody other : spaceBodies) {
                if (other == self) continue;
                double dx = self.getX() - other.getX();
                double dy = self.getY() - other.getY();
                double dz = self.getZ() - other.getZ();
                double rSquared = dx*dx + dy*dy + dz*dz;
                double r = Math.sqrt(rSquared);
                double F = 6.674e-11 * self.getMass() * other.getMass() / rSquared;
                Fx += F * dx / r;
                Fy += F * dy / r;
                Fz += F * dz / r;
            }
            double FTotal = Math.sqrt(Fx*Fx + Fy*Fy + Fz*Fz);
            double distanceToBC = Math.sqrt(dbx*dbx + dby*dby + dbz*dbz);

            double v = Math.sqrt(FTotal * distanceToBC / self.getMass());

            self.setVy(foundFirstStar && self instanceof Star ? -v : v);
            System.out.println(self.getVy());

            foundFirstStar = foundFirstStar || self instanceof Star;
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
//            System.out.println("Planet received "+((Planet)spaceBodies.getFirst()).getReceivedLight()+"light");
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
