package Algorithms;

import MyObjects.Memory;
import MyObjects.Process;

import java.util.*;

public class Equal implements Algorithm {

    private final HashMap<Process, Memory> processesAndTheirMemories = new HashMap<>();
    private final HashSet<Process> doneProcesses = new HashSet<>();
    private final HashSet<Process> activeProcesses;
    private final ArrayList<Process> processes;
    private int pageFaults = 0;

    public Equal(ArrayList<Process> processes, int memorySize) {

        this.processes = processes;
        activeProcesses = new HashSet<>(processes);

        int memoryPerProcess = memorySize/processes.size();

        for (Process process : processes) {
            processesAndTheirMemories.put(process, new Memory(memoryPerProcess));
        }
    }

    public int run() {

        while (doneProcesses.size() < processes.size()) {
            for (Process currentProcess : activeProcesses) {

                currentProcess.getNextReference();
                Memory currentMemory = processesAndTheirMemories.get(currentProcess);
                int counter = currentProcess.getReferencesCounter();

                if (LRU.referToThePage(counter-1, currentMemory, currentProcess.getAllReferences())) {
                    pageFaults++;
                    currentProcess.increasePageFaults();
                }
                if (currentProcess.getActiveReferences().isEmpty()) {
                    doneProcesses.add(currentProcess);
                }
            }
        }

        return pageFaults;
    }
}