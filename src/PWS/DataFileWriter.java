package PWS;

import PWS.simulation.SimulationData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;

import static PWS.Logger.getStringFromIntTimeData;

public class DataFileWriter {
    private static File dataFile = null;

    public static void writeResultsFile() {
        try (FileWriter writer = new FileWriter(dataFile)) {

            writer.write("rPlanet,rStars,receivedLight");
            writeData(writer, Main.currentDataSet);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createFile() {
        // Make the results folder
        File resultsFolder = new File("logs/results");
        if (!resultsFolder.mkdirs() && !resultsFolder.isDirectory()) {
            System.err.println("Could not create results folder");
            System.exit(1);
        }

        dataFile = new File(resultsFolder, "results-" +
                Calendar.getInstance().get(Calendar.YEAR) + "-" +
                (Calendar.getInstance().get(Calendar.MONTH)+1) + "-" +
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "--" +
                getStringFromIntTimeData(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) + "-" +
                getStringFromIntTimeData(Calendar.getInstance().get(Calendar.MINUTE)) + "-" +
                getStringFromIntTimeData(Calendar.getInstance().get(Calendar.SECOND))
                + ".csv");

        try {
            dataFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeData(FileWriter writer, Collection<SimulationData> dataSet) throws IOException {
        for (SimulationData data : dataSet) {
            String str = "\n\"" + data.rPlanet() + "\",\"" + data.rStars() + "\",\"" + data.receivedLight() + '"';
            writer.write(str.replace('.',','));
        }
    }
}
