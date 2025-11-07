package PWS;

import PWS.simulation.Simulation;
import PWS.simulation.SimulationData;
import PWS.simulation.SimulationStartData;
import PWS.ui.ConfigFrame;

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

    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args) {
        Logger.initLoggers();

        try {
            configFrame = new ConfigFrame();


//            Simulation simulation = new Simulation(true);
//            synchronized (Main.simulationInstances) {
//                if (Main.state == RunningState.CLOSING)
//                    return;
//                Main.simulationInstances.add(simulation);
//            }
//            Main.runningSimulations.addAndGet(1);
//            simulation.startSolarSimulation();


            while (true) {

                if (runningSimulations.get() == 0) {
                    if (phasesInCycle < 5) {
                        phasesInCycle++;

                    } else {
                        phasesInCycle = 0;
                        cycle++;

                    }
                }

                if (state == RunningState.CLOSING)
                    break;
            }

        } catch (Exception e) {
            System.err.println("An error occurred, closing application");
            e.printStackTrace();
        }

        configFrame.dispose();
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
            for (int i = 0; i < 10; i++) {
                double rStars = random.nextDouble(1e9,5e10);
                double rPlanet = random.nextDouble(2*rStars,5e11);
                result.add(new SimulationStartData(rPlanet, rStars));
            }
        } else {
            synchronized (currentDataSet) {
                double cycleBorder = (5e10-1e9)/cycle;
                for (int i = 0; i < 10; i++) {
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
        return startData.rStars() > 1e9 && startData.rStars() < 5e10 && startData.rPlanet() > 2*startData.rStars() && startData.rPlanet() < 5e11;
    }
}
