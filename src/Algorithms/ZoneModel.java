package Algorithms;

import MyObjects.Memory;
import MyObjects.Process;

import java.util.*;

public class ZoneModel implements Algorithm {

    private final HashMap<Process, Memory> processesAndTheirMemories = new HashMap<>();
    private final ArrayList<Process> doneProcesses = new ArrayList<>();
    private final ArrayList<Process> frozenProcesses = new ArrayList<>();
    private final ArrayList<Process> tempProcesses = new ArrayList<>();
    private final ArrayList<Process> tempProcesses2 = new ArrayList<>();
    private final ArrayList<Process> activeProcesses;
    private final ArrayList<Process> processes;
    private final Memory globalMemory;
    private final int DELTA_PARAMETER;
    private int pageFaults = 0;

    public ZoneModel(ArrayList<Process> processes, int memorySize, int DELTA_PARAMETER) {

        this.processes = processes;
        activeProcesses = new ArrayList<>(processes);
        this.DELTA_PARAMETER = DELTA_PARAMETER;

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

        globalMemory = new Memory(0);
    }

    public int run() {

        while (doneProcesses.size() < processes.size()) {
            if (!frozenProcesses.isEmpty()) {
                for (Process frozenProcess : frozenProcesses) {
                    if (frozenProcess.getFrozenCapacity() * 1.5 <= globalMemory.getSize()) {
                        activeProcesses.add(frozenProcess);
                        globalMemory.changeSize(-frozenProcess.getFrozenCapacity());
                        processesAndTheirMemories.get(frozenProcess).changeSize(frozenProcess.getFrozenCapacity());
                        tempProcesses2.add(frozenProcess);
                    }
                }
            }

            if (!tempProcesses2.isEmpty()) {
                for (Process process : tempProcesses2) {
                    frozenProcesses.remove(process);
                }
            }
            tempProcesses2.clear();

            if (!tempProcesses.isEmpty()) {
                for (Process proc : tempProcesses) {
                    globalMemory.changeSize(processesAndTheirMemories.get(proc).getSize());
                    activeProcesses.remove(proc);
                }
            }

            tempProcesses.clear();

            if (activeProcesses.size() + frozenProcesses.size() + doneProcesses.size() != processes.size()) {
                System.out.println();
            }

            for (Process currentProcess : activeProcesses)
            {
                currentProcess.getNextReference();
                Memory currentMemory = processesAndTheirMemories.get(currentProcess);
                int counter = currentProcess.getReferencesCounter();

                if (counter > 0 && counter % DELTA_PARAMETER == 0) {

                    HashSet<Integer> pagesUsedInHistory = new HashSet<>();

                    for (int id = counter-1; id >= counter - DELTA_PARAMETER; id--) {
                        pagesUsedInHistory.add(currentProcess.getReference(id));
                    }

                    int requiredMemory = pagesUsedInHistory.size();
                    int actualMemory = currentMemory.getSize();

                    if (requiredMemory < actualMemory) {
                        while (requiredMemory != actualMemory && !currentMemory.isFull() && currentMemory.getSize() > 0) {
                            currentMemory.changeSize(-1);
                            globalMemory.changeSize(1);
                            actualMemory--;
                        }

                        while (requiredMemory != actualMemory && currentMemory.getSize() > 0) {
                            Integer pageToDelete = currentProcess.getReference(LRU.getLeastRecentlyUsedPageID
                                    (counter, currentMemory, currentProcess.getAllReferences()));
                            currentMemory.removePage(pageToDelete);
                            currentMemory.changeSize(-1);
                            globalMemory.changeSize(1);
                            actualMemory--;
                        }
                    }
                    else if (requiredMemory > actualMemory) {
                        if (requiredMemory - actualMemory > globalMemory.getSize()) {
                            currentProcess.setFrozenCapacity(currentMemory.getSize());
                            tempProcesses.add(currentProcess);
                            globalMemory.changeSize(currentMemory.getSize());
                            frozenProcesses.add(currentProcess);
                        }
                        else {
                            currentMemory.changeSize(requiredMemory - actualMemory);
                            globalMemory.changeSize(-(requiredMemory - actualMemory));
                        }
                    }
                }

                if (LRU.referToThePage(counter-1, currentMemory, currentProcess.getAllReferences())) {
                    pageFaults++;
                    currentProcess.increasePageFaults();
                }

                if (currentProcess.getActiveReferences().isEmpty()) {
                    doneProcesses.add(currentProcess);
                    tempProcesses.add(currentProcess);
                }
            }
        }

        return pageFaults;
    }
}