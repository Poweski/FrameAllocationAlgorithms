package Algorithms;

import Helpful.Generator;
import MyObjects.Memory;
import MyObjects.Process;

import java.util.*;

public class Proportional implements Algorithm {

    private final HashMap<Process, Memory> processesAndTheirMemories = new HashMap<>();
    private final HashSet<Process> doneProcesses = new HashSet<>();
    private final HashSet<Process> activeProcesses;
    private final ArrayList<Process> processes;
    private int pageFaults = 0;

    public Proportional(ArrayList<Process> processes, int memorySize) {

        this.processes = processes;
        activeProcesses = new HashSet<>(processes);

        int totalNumberOfPages = 0;

        for (Process proc : processes)
            totalNumberOfPages += proc.getSetOfPages().size();

        for (Process proc : processes)
            processesAndTheirMemories.put(proc, new Memory
                    (proc.getSetOfPages().size() * memorySize / totalNumberOfPages));

//        int totalSizeOfLocalMemory = 0;
//        for (Process proc : processes)
//            totalSizeOfLocalMemory += processesAndTheirMemories.get(proc).getSize();
//
//        processes.sort(Comparator.comparingInt(proc -> proc.getSetOfPages().size()));
//
//        for (Process proc : processes)
//            if (memorySize-totalSizeOfLocalMemory > 0) {
//                processesAndTheirMemories.get(proc).increaseSize();
//                totalSizeOfLocalMemory++;
//            }
    }

    public int run() {

        while (doneProcesses.size() < processes.size())
        {
            for (Process currentProcess : activeProcesses)
            {
                Integer page = currentProcess.getNextReference();
                Memory currentMemory = processesAndTheirMemories.get(currentProcess);
                int counter = currentProcess.getReferencesCounter();

                if (LRU.referToThePage(counter-1, currentMemory, currentProcess.getAllReferences()))
                {
                    pageFaults++;
                    currentProcess.increasePageFaults();
                }
                if (currentProcess.getActiveReferences().size() == 0)
                    doneProcesses.add(currentProcess);
            }
        }

        return pageFaults;
    }
}