import Algorithms.*;
import Helpful.*;
import MyObjects.Process;

import java.util.ArrayList;

public class Main {

    static public final int NUMBER_OF_PROCESSES = 10;
    static final int MIN_PAGE_PER_PROCESS_NUMBER = 3;
    static final int MAX_PAGE_PER_PROCESS_NUMBER = 6;
    static final int PROCESS_REFERENCE_STRING_LENGTH = 100;
    static final int NUMBER_OF_FRAMES = 29;
    static final int STRING_CONSTANT = 3;
    static final int MIN_PFF_VALUE = 2;
    static final int MAX_PFF_VALUE = 7;
    static final int T_PFF_PARAMETER = 24;
    static final int DELTA_PARAMETER = 12;
    static int SEED = 42;
    static int REPETITIONS = 1;


    public static void main (String[] args) {

        double equalResult = 0;
        double proportionalResult = 0;
        double pffControlResult = 0;
        double zoneModelResult = 0;

        double[] equalArray = new double[NUMBER_OF_PROCESSES];
        double[] proportionalArray = new double[NUMBER_OF_PROCESSES];
        double[] pffControlArray = new double[NUMBER_OF_PROCESSES];
        double[] zoneModelArray = new double[NUMBER_OF_PROCESSES];

        for (int i = 0; i < REPETITIONS; i++) {

            ArrayList<Process> processes = Generator.generateProcesses
                    (SEED, NUMBER_OF_PROCESSES, MIN_PAGE_PER_PROCESS_NUMBER, MAX_PAGE_PER_PROCESS_NUMBER,
                            PROCESS_REFERENCE_STRING_LENGTH, STRING_CONSTANT);

            ArrayList<Process> equalProcesses = Generator.copyProcessList(processes);
            ArrayList<Process> proportionalProcesses = Generator.copyProcessList(processes);
            ArrayList<Process> PFFControlProcesses = Generator.copyProcessList(processes);
            ArrayList<Process> zoneModelProcesses = Generator.copyProcessList(processes);

            Equal equal = new Equal(equalProcesses, NUMBER_OF_FRAMES);
            Proportional proportional = new Proportional(proportionalProcesses, NUMBER_OF_FRAMES);
            PFFControl PFFControl = new PFFControl(PFFControlProcesses, NUMBER_OF_FRAMES, MAX_PFF_VALUE, MIN_PFF_VALUE, T_PFF_PARAMETER);
            ZoneModel zoneModel = new ZoneModel(zoneModelProcesses, NUMBER_OF_FRAMES, DELTA_PARAMETER);

            equalResult += equal.run();
            proportionalResult += proportional.run();
            pffControlResult += PFFControl.run();
            zoneModelResult += zoneModel.run();

            int j = 0;
            for (Process process : equalProcesses) {
                equalArray[j] += process.getPageFaults();
                j++;
            }
            j = 0;
            for (Process process : proportionalProcesses) {
                proportionalArray[j] += process.getPageFaults();
                j++;
            }
            j = 0;
            for (Process process : PFFControlProcesses) {
                pffControlArray[j] += process.getPageFaults();
                j++;
            }
            j = 0;
            for (Process process : zoneModelProcesses) {
                zoneModelArray[j] += process.getPageFaults();
                j++;
            }
        }

        System.out.println("\nEqual: " + equalResult/REPETITIONS);
        for (int i = 0; i < NUMBER_OF_PROCESSES; i++) {
            System.out.print((equalArray[i] / REPETITIONS) + " ");
        }
        System.out.println("\n\nProportional: " + proportionalResult/REPETITIONS);
        for (int i = 0; i < NUMBER_OF_PROCESSES; i++) {
            System.out.print((proportionalArray[i] / REPETITIONS) + " ");
        }
        System.out.println("\n\nPFFControl: " + pffControlResult/REPETITIONS);
        for (int i = 0; i < NUMBER_OF_PROCESSES; i++) {
            System.out.print((pffControlArray[i] / REPETITIONS) + " ");
        }
        System.out.println("\n\nZoneModel: " + zoneModelResult/REPETITIONS);
        for (int i = 0; i < NUMBER_OF_PROCESSES; i++) {
            System.out.print((zoneModelArray[i] / REPETITIONS) + " ");
        }
        System.out.println();
    }
}