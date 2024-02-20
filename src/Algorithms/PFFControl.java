package Algorithms;

import MyObjects.Memory;
import MyObjects.Process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class PFFControl implements Algorithm {

    private final HashMap<Process, Memory> processesAndTheirMemories = new HashMap<>();
    private final HashMap<Process, ArrayList<Integer>> processesAndTheirPFFs = new HashMap<>();
    private final HashSet<Process> doneProcesses = new HashSet<>();
    private final HashSet<Process> frozenProcesses = new HashSet<>();
    private final HashSet<Process> tempProcesses = new HashSet<>();
    private final HashSet<Process> tempProcesses2 = new HashSet<>();
    private final HashSet<Process> activeProcesses;
    private final ArrayList<Process> processes;
    private final Memory globalMemory;
    private final int maxLim;
    private final int minLim;
    private final int T;
    private int pageFaults = 0;

    public PFFControl(ArrayList<Process> processes, int memorySize, int maxLim, int minLim, int T) {

        this.processes = processes;
        activeProcesses = new HashSet<>(processes);
        this.maxLim = maxLim;
        this.minLim = minLim;
        this.T = T;

        int totalNumberOfPages = 0;

        for (Process process : processes) {
            totalNumberOfPages += process.getSetOfPages().size();
        }

        for (Process process : processes) {
            processesAndTheirMemories.put(process, new Memory
                    (process.getSetOfPages().size() * memorySize / totalNumberOfPages));
        }

        int totalSizeOfLocalMemory = 0;
        for (Process process : processes) {
            totalSizeOfLocalMemory += processesAndTheirMemories.get(process).getSize();
        }

        for (Process process : processes) {
            if (memorySize - totalSizeOfLocalMemory > 0) {
                processesAndTheirMemories.get(process).increaseSize();
                totalSizeOfLocalMemory++;
            }
        }
        for (Process process : processes) {
            processesAndTheirPFFs.put(process, new ArrayList<>());
        }

        globalMemory = new Memory(0);
    }

    public int run() {

        boolean flag = false;

        while (doneProcesses.size() < processes.size()) {
            if (flag && !frozenProcesses.isEmpty())
                for (Process frozenProcess : frozenProcesses)
                    if (frozenProcess.getFrozenCapacity() <= globalMemory.getSize()) {
                        activeProcesses.add(frozenProcess);
                        globalMemory.changeSize(-frozenProcess.getFrozenCapacity());
                        processesAndTheirMemories.get(frozenProcess).changeSize(frozenProcess.getFrozenCapacity());
                        tempProcesses2.add(frozenProcess);
                        flag = false;
                    }

            if (!tempProcesses2.isEmpty()) {
                for (Process process : tempProcesses2) {
                    frozenProcesses.remove(process);
                }
            }
            tempProcesses2.clear();

            if (!tempProcesses.isEmpty()) {
                for (Process process : tempProcesses) {
                    globalMemory.changeSize(processesAndTheirMemories.get(process).getSize());
                    activeProcesses.remove(process);
                }
            }
            tempProcesses.clear();

            for (Process currentProcess : activeProcesses) {

                currentProcess.getNextReference();
                Memory currentMemory = processesAndTheirMemories.get(currentProcess);
                ArrayList<Integer> pffList = processesAndTheirPFFs.get(currentProcess);
                int counter = currentProcess.getReferencesCounter();

                if (LRU.referToThePage(counter-1, currentMemory, currentProcess.getAllReferences())) {
                    pageFaults++;
                    currentProcess.increasePageFaults();
                    pffList.add(1);
                }
                else {
                    pffList.add(0);
                }

                if (counter > 0 && counter % T == 0) {

                    int pff = 0;

                    for (int i = 0; i < T; i++) {
                        pff += pffList.get(i);
                    }

                    if (pff < minLim && currentMemory.getSize() > 1) {
                        Integer pageToDelete = currentProcess.getReference(LRU.getLeastRecentlyUsedPageID
                                (counter, currentMemory, currentProcess.getAllReferences()));
                        currentMemory.removePage(pageToDelete);
                        currentMemory.changeSize(-1);
                        globalMemory.changeSize(1);
                    }
                    else if (pff > maxLim) {
                        if (globalMemory.getSize() > 0) {
                            globalMemory.changeSize(-1);
                            currentMemory.changeSize(1);
                        }
                        else if (activeProcesses.size() > 1) {
                            currentProcess.setFrozenCapacity(currentMemory.getSize() + 1);
                            tempProcesses.add(currentProcess);
                            globalMemory.changeSize(currentMemory.getSize());
                            frozenProcesses.add(currentProcess);
                        }
                    }

                    pffList.clear();
                }

                if (currentProcess.getActiveReferences().isEmpty()) {
                    doneProcesses.add(currentProcess);
                    tempProcesses.add(currentProcess);
                    flag = true;
                }
            }
        }

        return pageFaults;
    }
}
