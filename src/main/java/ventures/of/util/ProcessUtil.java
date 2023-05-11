package ventures.of.util;

import lombok.extern.slf4j.Slf4j;
import ventures.of.MainProgram;
import ventures.of.model.NamedProcess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ventures.of.util.StringUtil.printVerbose;


@Slf4j
public class ProcessUtil {
    public static List<NamedProcess> processes = new ArrayList<>();

    public static void sendSignalToPid(String pid) {
        runCommand("kill -s SIGUSR1 " + pid);
    }

    public static String getPid(String programName) {
        try {
            String[] env = {"DISPLAY=:0", "XDG_RUNTIME_DIR=/run/user/1000"};
            Process process = Runtime.getRuntime().exec("pgrep " + programName, env);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            if (line != null) {
                return line.trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getProccessWindow(String pid) {
        try {
            Process p = Runtime.getRuntime().exec("xdotool search --pid " + pid);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = reader.readLine();
            if (line != null) {
                return line.trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Return -1 if process ID is not found
    }

    public static void runCommand(String command) {
        runCommand(command, false);
    }

    public static void runCommand(String command, boolean verbose) {
        runCommand(command, verbose, true);
    }

    public static Process runCommand(String command, boolean verbose, boolean wait) {
        printVerbose("command = " + command, verbose);
        String[] env = {"DISPLAY=:0", "XDG_RUNTIME_DIR=/run/user/1000"};
        try {
            Process process = Runtime.getRuntime().exec(command, env);
            String programName = command.split(" ")[0];// + " " + command.split(" ")[1];
            if(programName.contains("sudo")) {
                programName = command.split(" ")[1];
            }
            processes.add(new NamedProcess(programName, process));
            // Read the output of the command
            createStreamThread(process.getInputStream(), process, "sout", programName, verbose);
            createStreamThread(process.getErrorStream(), process, "serr", programName, verbose);
            // Wait for the command to finish
            if (wait) {
                int exitCode = process.waitFor();
                //remove process from list
                processes = processes.stream().filter(e -> !e.process.equals(process)).collect(Collectors.toList());
                printVerbose("\"" + command + "\" exited with code " + exitCode, verbose);
            }
            return process;
        } catch (IOException | InterruptedException ex) {
            log.error("Error running command: " + ex.getMessage());
        }
        return null;
    }

    public static Void rebootAction() {
        log.info("Rebooting");
        MainProgram.cameraController.killLibCamera();
        runCommand("sudo reboot", true, true);
        return null;
    }

    public static Void shutdownAction() {
        log.info("Shutting down");
        ledContinous();
        MainProgram.cameraController.killLibCamera();
        runCommand("sudo shutdown -h now", true, true);
        return null;
    }

    public static void setBacklight(int backlight) {
        runCommand("sudo pigs pwm 24 " + backlight, false);
    }


    //todo fix led settings
    //these seem to each get 50% of the leds time
    //mmc0 seems to have a inverted logic 0 = on 1 = off in the brightness file
    //default-on seems to have a 1 = on 0= off in the brightness file
    public static void ledSetManualMode() {
        runCommand("sudo sh -c \"echo none > /sys/class/leds/mmc0/trigger\"");
        runCommand("sudo sh -c \"echo none > /sys/class/leds/default-on/trigger\"");
    }

    public static void ledOff() {
        runCommand("sudo sh -c \"echo 1 > /sys/class/leds/mmc0/brightness\"");
        runCommand("sudo sh -c \"echo 0 > /sys/class/leds/default-on/brightness\"");
    }
    public static void ledBlinking() {
        runCommand("sudo sh -c \"echo 1 > /sys/class/leds/mmc0/brightness\"");
        runCommand("sudo sh -c \"echo 1 > /sys/class/leds/default-on/brightness\"");
    }

    public static void ledContinous() {
        runCommand("sudo sh -c \"echo 0 > /sys/class/leds/mmc0/brightness\"");
        runCommand("sudo sh -c \"echo 1 > /sys/class/leds/default-on/brightness\"");
    }

    public static void killProcesses(String... toKill) {
        processes.stream()
                .filter(e -> Arrays.stream(toKill).anyMatch(k -> k.equals(e.program)))
                .forEach(e -> e.process.destroy());
        //remove camera processes from the list
        processes = processes.stream()
                .filter(e -> Arrays.stream(toKill).noneMatch(k -> k.equals(e.program)))
                .filter(e -> e.process.isAlive())
                .collect(Collectors.toList());
    }

    public static void createStreamThread(InputStream inputStream, Process process, String streamName, String programName, boolean verbose) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        new Thread(() -> {
            while (process.isAlive()) {
                try {
                    String line2;
                    printVerbose(programName + " reading " + streamName, verbose);
                    while ((line2 = bufferedReader.readLine()) != null) {
                        printVerbose(streamName + " " + line2, verbose);
                    }
                    printVerbose(programName + " done reading", verbose);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    printVerbose(programName + " output sleeping", verbose);
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        ).start();
    }
}
