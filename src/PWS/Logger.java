package PWS;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

public class Logger {
    private static final byte[] lineSep = System.lineSeparator().getBytes(StandardCharsets.UTF_8);
    private static FileOutputStream fileOutput;

    public static void initLoggers(){
        File logsFolder = new File(System.getProperty("user.dir") + File.separator + "logs");
        if (!(logsFolder.isDirectory() || logsFolder.mkdirs()))
            throw new RuntimeException("Could not create a directory for the logs!");

        File logFile = new File(logsFolder, "log-" +
                Calendar.getInstance().get(Calendar.YEAR) + "-" +
                (Calendar.getInstance().get(Calendar.MONTH)+1) + "-" +
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "--" +
                getStringFromIntTimeData(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) + "-" +
                getStringFromIntTimeData(Calendar.getInstance().get(Calendar.MINUTE)) + "-" +
                getStringFromIntTimeData(Calendar.getInstance().get(Calendar.SECOND))
                +".txt");

        try {
            if (!logFile.createNewFile())
                throw new RuntimeException("Could not create the log file!");

            fileOutput = new FileOutputStream(logFile);

            System.setOut(new PrintStream(new OutputStream() {
                final OutputStream sysOut = System.out;
                boolean shouldAddPrefix = true;

                @Override
                public void write(byte[] b) throws IOException {
                    sysOut.write(b);
                    fileOutput.write(b);
                }

                @Override
                public void write(byte[] b, int off, int len) throws IOException {
                    if (shouldAddPrefix) {
                        sysOut.write(getPrefix().getBytes(StandardCharsets.UTF_8));
                        fileOutput.write(getPrefix().getBytes(StandardCharsets.UTF_8));
                        shouldAddPrefix = false;
                    }
                    sysOut.write(b, off, len);
                    fileOutput.write(b, off, len);
                    if (endsWithLineSep(b, len))
                        shouldAddPrefix = true;
                }

                @Override
                public void flush() throws IOException {
                    sysOut.flush();
                    fileOutput.flush();
                }

                @Override
                public void close() throws IOException {
                    sysOut.close();
                    fileOutput.close();
                }

                @Override
                public void write(int b) throws IOException {
                    sysOut.write(b);
                    fileOutput.write(b);
                }
            }));
            System.setErr(new PrintStream(new OutputStream() {
                final OutputStream sysOut = System.err;
                boolean shouldAddPrefix = true;

                @Override
                public void write(byte[] b) throws IOException {
                    sysOut.write(b);
                    fileOutput.write(b);
                }

                @Override
                public void write(byte[] b, int off, int len) throws IOException {
                    if (shouldAddPrefix) {
                        sysOut.write(getErrorPrefix().getBytes(StandardCharsets.UTF_8));
                        fileOutput.write(getErrorPrefix().getBytes(StandardCharsets.UTF_8));
                        shouldAddPrefix = false;
                    }
                    sysOut.write(b, off, len);
                    fileOutput.write(b, off, len);
                    if (endsWithLineSep(b, len))
                        shouldAddPrefix = true;
                }

                @Override
                public void flush() throws IOException {
                    sysOut.flush();
                    fileOutput.flush();
                }

                @Override
                public void close() throws IOException {
                    sysOut.close();
                    fileOutput.close();
                }

                @Override
                public void write(int b) throws IOException {
                    sysOut.write(b);
                    fileOutput.write(b);
                }
            }));
        } catch (IOException e) {
            System.err.println("An error occurred while creating the log file");
            throw new RuntimeException(e);
        }
    }
    protected static boolean endsWithLineSep(byte[] bytes, int len){
        if(len<lineSep.length)return false;
        for(int i = len-lineSep.length; i<len; i++){
            if(bytes[i]!=lineSep[i-len+lineSep.length])return false;
        }
        return true;
    }
    protected static String getPrefix(){
        return "["+
                getStringFromIntTimeData(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))+":"+
                getStringFromIntTimeData(Calendar.getInstance().get(Calendar.MINUTE))+":"+
                getStringFromIntTimeData(Calendar.getInstance().get(Calendar.SECOND))+":"+
                getStringFromIntMillisecondTimeData(Calendar.getInstance().get(Calendar.MILLISECOND)) + "] ["+
                Thread.currentThread().getName()+"] [INFO]  ";
    }
    protected static String getErrorPrefix(){
        return "["+
                getStringFromIntTimeData(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))+":"+
                getStringFromIntTimeData(Calendar.getInstance().get(Calendar.MINUTE))+":"+
                getStringFromIntTimeData(Calendar.getInstance().get(Calendar.SECOND))+":"+
                getStringFromIntMillisecondTimeData(Calendar.getInstance().get(Calendar.MILLISECOND)) + "] ["+
                Thread.currentThread().getName()+"] [ERROR] ";
    }
    private static String getStringFromIntTimeData(int timeNumber){
        String res = String.valueOf(timeNumber);
        return res.length() >= 2 ? res : "0"+res;
    }
    private static String getStringFromIntMillisecondTimeData(int timeNumber){
        String res = String.valueOf(timeNumber);
        return res.length() == 2 ? "0"+res : res.length() == 1 ? "00"+res : res;
    }
    public static void closeLoggerFileOutputStream() {
        try {
            fileOutput.close();
        } catch (IOException e) {
            System.err.println("Couldn't close logger fileOutputStream");
            throw new RuntimeException(e);
        }
    }
}
