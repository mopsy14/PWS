package PWS;

import PWS.simulation.Simulation;
import PWS.simulation.SimulationData;
import PWS.simulation.SimulationStartData;
import PWS.ui.ConfigFrame;
import PWS.ui.SimulationDataVisualizeFrame;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static volatile RunningState state = RunningState.CONFIG_SCREEN;
    public static ConfigFrame configFrame;
    public static final List<Simulation> simulationInstances = Collections.synchronizedList(new ArrayList<>());
    public static Set<SimulationData> allData = Collections.synchronizedSet(new HashSet<>());
    public static List<SimulationData> currentDataSet = Collections.synchronizedList(new ArrayList<>());
    public static List<SimulationData> newDataSet = Collections.synchronizedList(new ArrayList<>());
    public static int cycle = 0;
    public static AtomicInteger runningSimulations = new AtomicInteger();
    public static int phasesInCycle;
    private static final Random random = new Random();
    public static volatile double referenceLightIntensity = 0.0;

    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args) {
        Logger.initLoggers();
        SimulationDataVisualizeFrame visualizeFrame = null;
        try {
            configFrame = new ConfigFrame();


            Simulation simulation = new Simulation(true);
            synchronized (Main.simulationInstances) {
                if (Main.state == RunningState.CLOSING)
                    return;
                Main.simulationInstances.add(simulation);
            }
            Main.runningSimulations.addAndGet(1);
            simulation.startSolarSimulation();

            visualizeFrame = new SimulationDataVisualizeFrame();
            int counter = 0;

            while (true) {
                counter++;
                if (counter % 10 == 0)
                    visualizeFrame.updateVisualization();
                if (runningSimulations.get() == 0 && state == RunningState.SIMULATING) {
                    if (phasesInCycle < 50) {
                        phasesInCycle++;
                        System.out.println("Started phase " + phasesInCycle);
                        startNewSimulationSet();
                    } else {
                        phasesInCycle = 0;
                        System.out.println("================================");
                        System.out.println("Finished cycle " + cycle);
                        System.out.println("================================");
                        cycle++;
                        synchronized (currentDataSet) {
                            synchronized (newDataSet) {
                                currentDataSet.addAll(newDataSet);
                                double reference = referenceLightIntensity;
                                currentDataSet.sort(Comparator.comparingDouble((data) -> Math.abs(reference - data.receivedLight())));
                                currentDataSet = Collections.synchronizedList(currentDataSet.subList(0, currentDataSet.size() / 2));
                                newDataSet = Collections.synchronizedList(new ArrayList<>());
                            }
                        }
                        if (cycle == 50)
                            break;
                    }
                }

                Thread.sleep(100);

                if (state == RunningState.CLOSING)
                    break;
            }

        } catch (Exception e) {
            System.err.println("An error occurred, closing application");
            e.printStackTrace();
        }

        synchronized (currentDataSet) {
            for (SimulationData data : currentDataSet) {
                System.out.println(data);
            }
        }

        configFrame.dispose();
        visualizeFrame.dispose();
        synchronized (simulationInstances) {
            for (Simulation simulation : simulationInstances) {
                simulation.disposeAWT();
            }
        }

        Logger.closeLoggerFileOutputStream();
    }

    private static void startNewSimulationSet() {
        List<SimulationStartData> startData = generateStartData();
        for (int i = 0; i < startData.size(); i++) {
            Simulation simulation = new Simulation(true);
            synchronized (Main.simulationInstances) {
                if (Main.state == RunningState.CLOSING)
                    return;
                Main.simulationInstances.add(simulation);
            }
            Main.runningSimulations.addAndGet(1);
            simulation.startSimulation(startData.get(i));
        }
    }
    private static List<SimulationStartData> generateStartData() {
        List<SimulationStartData> result = new ArrayList<>();
        if (cycle == 0) {
            for (int i = 0; i < 3; i++) {
                double rStars = random.nextDouble(1e9,5e9);
                double rPlanet = random.nextDouble(3*rStars,2e10);
                result.add(new SimulationStartData(rPlanet, rStars));
            }
        } else {
            synchronized (currentDataSet) {
                double cycleBorder = (1e10-1e9)/(Math.pow(Math.E,0.25*cycle+0.25));
                for (int i = 0; i < 3; i++) {
                    SimulationData originData = currentDataSet.get(random.nextInt(currentDataSet.size()));
                    while (true) {
                        SimulationStartData startData = new SimulationStartData(originData.rPlanet() + random.nextDouble(-cycleBorder, cycleBorder), originData.rStars() + random.nextDouble(-cycleBorder, cycleBorder));
                        if (inLimits(startData)) {
                            result.add(startData);
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }
    private static boolean inLimits(SimulationStartData startData) {
        return startData.rStars() > 1e9 && startData.rStars() < 5e9 && startData.rPlanet() > 3*startData.rStars() && startData.rPlanet() < 2e10;
    }
}
