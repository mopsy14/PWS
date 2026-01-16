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
    Thread simulationThread = null;
    ArrayList<Thread> workerThreads = new ArrayList<>();
    public List<SpaceBody> spaceBodies = new ArrayList<>();
    ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<>();
    SimulationControlFrame controlFrame;
    SimulationVisualizeFrame visualizeFrame;
    volatile CountDownLatch remainingTasks = null;
    private volatile SimulationState state = SimulationState.STARTING;
    double stepSize = 60;
    long stepAmount = (long)2.5e6;
    private SimulationStartData startData = null;

    //Configuration:
    public boolean useUI;

    public Simulation(boolean useUI) {

        //Configuration:
        this.useUI = useUI;

        controlFrame = new SimulationControlFrame(this);
        if (useUI)
            visualizeFrame = new SimulationVisualizeFrame(this);
    }

    public void startSolarSimulation() {
        if (simulationThread == null || !simulationThread.isAlive()) {
            Main.state = RunningState.SIMULATING;
            spaceBodies.clear();

            setupSpaceBodiesForSolarSystem(1.78013944e11);

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
    public void startSimulation(SimulationStartData startData) {
        if (simulationThread == null || !simulationThread.isAlive()) {
            Main.state = RunningState.SIMULATING;
            spaceBodies.clear();

            this.startData = startData;
            setupSpaceBodies(startData.rPlanet(), startData.rStars(), 6.546e26);

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

    private void setupSpaceBodies(double rPlanet, double rStars, double luminosity) {
        spaceBodies.add(new Star(rStars,0,0,1e30,3e8,0,0,0,luminosity, this));
        spaceBodies.add(new Star(-rStars,0,0,1e30,3e8,0,0,0,luminosity, this));

        spaceBodies.add(new Planet(rPlanet,0,0,6e24,6.371e6,0,0,0, this));

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

            self.setVy(foundFirstStar && self instanceof Star ? -v : (self instanceof Planet)? -v:v);

            foundFirstStar = foundFirstStar || self instanceof Star;
        }
    }

    private void setupSpaceBodiesForSolarSystem(double rPlanetOrbit) {
        spaceBodies.add(new Star(0,0,0,2e30,6.955e8,0,0,0,3.828e26, this));

        spaceBodies.add(new Planet(rPlanetOrbit,0,0,6e24,6.371e6,0,0,0, this));

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

            self.setVy(foundFirstStar && self instanceof Star ? -v : (self instanceof Planet)? -v:v);

            foundFirstStar = foundFirstStar || self instanceof Star;
        }
    }

    private void runSimulation() {
        try {
            System.out.println("Started simulating");

            startWorkerThreads(3);

            state = SimulationState.RUNNING;

            for (int i = 0; i < stepAmount; i++) {
                if (Main.state != RunningState.SIMULATING) {
                    Main.simulationInstances.remove(this);
                    state = SimulationState.STOPPING;
                    simulationThread = null;
                    Main.runningSimulations.addAndGet(-1);
                    return;
                }
                if (i % 10000 == 0) {
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

                if (i % 100 == 0) {
                    remainingTasks = new CountDownLatch(spaceBodies.size());
                    for (SpaceBody body : spaceBodies) {
                        tasks.add(body::updateLighting);
                    }
                    if (!remainingTasks.await(10, TimeUnit.SECONDS))
                        throw new TimeoutException("Tasks took more than allowed 10 seconds to execute");
                }
            }
            state = SimulationState.STOPPING;
            System.out.println("Finished simulating");
            for (SpaceBody body : spaceBodies) {
                if (body instanceof Planet planet) {
                    if(startData==null){
                        System.out.println("Solar system received " + planet.getReceivedLight() + " light");
                        Main.referenceLightIntensity = planet.getReceivedLight();
                    } else {
                        SimulationData data = new SimulationData(startData.rPlanet(), startData.rStars(), planet.getReceivedLight());
                        Main.allData.add(data);
                        Main.newDataSet.add(data);
                        System.out.println(data);
                    }
                }
            }
        } catch (TimeoutException e) {
            Main.simulationInstances.remove(this);
            Main.runningSimulations.addAndGet(-1);
            if (Main.state == RunningState.SIMULATING) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            System.err.println("An error occurred on the simulating thread");
            e.printStackTrace();
        }
        disposeAWT();
        simulationThread = null;
        Main.simulationInstances.remove(this);
        Main.runningSimulations.addAndGet(-1);
    }

    private void startWorkerThreads(int amount) {
        workerThreads.clear();
        for (int i = 0; i < amount; i++) {
            workerThreads.add(new Thread(new Worker(this),"Worker-"+i));
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
    public SimulationState getSimulationState() {
        return state;
    }
    public void interruptThread() {
        try {
            simulationThread.interrupt();
        } catch (Exception ignored) {
        }
    }
}
