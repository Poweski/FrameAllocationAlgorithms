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
    private int freezeCounter = 0;

    public ZoneModel(ArrayList<Process> processes, int memorySize, int DELTA_PARAMETER) {

        this.processes = processes;
        activeProcesses = new ArrayList<>(processes);
        this.DELTA_PARAMETER = DELTA_PARAMETER;

        int totalNumberOfPages = 0;

        for (Process proc : processes)
            totalNumberOfPages += proc.getSetOfPages().size();

        for (Process proc : processes)
            processesAndTheirMemories.put(proc, new Memory
                    (proc.getSetOfPages().size() * memorySize / totalNumberOfPages));

        int totalSizeOfLocalMemory = 0;
        for (Process proc : processes)
            totalSizeOfLocalMemory += processesAndTheirMemories.get(proc).getSize();

        for (Process proc : processes)
            if (memorySize-totalSizeOfLocalMemory > 0)
            {
                processesAndTheirMemories.get(proc).increaseSize();
                totalSizeOfLocalMemory++;
            }

        globalMemory = new Memory(0);
    }

    public int run() {

        boolean flag = false;

        while (doneProcesses.size() < processes.size())
        {
            if (frozenProcesses.size() > 0)
                for (Process frozenProcess : frozenProcesses)
                    if (frozenProcess.getFrozenCapacity()*1.5 <= globalMemory.getSize())
                    {
                        activeProcesses.add(frozenProcess);
                        globalMemory.changeSize(-frozenProcess.getFrozenCapacity());
                        processesAndTheirMemories.get(frozenProcess).changeSize(frozenProcess.getFrozenCapacity());
                        tempProcesses2.add(frozenProcess);
                        flag = false;
                    }

            if (tempProcesses2.size() != 0)
                for (Process proc : tempProcesses2)
                    frozenProcesses.remove(proc);
            tempProcesses2.clear();

            if (tempProcesses.size() != 0)
                for (Process proc : tempProcesses)
                {
                    globalMemory.changeSize(processesAndTheirMemories.get(proc).getSize());
//                    processesAndTheirMemories.get(proc).clear();
                    activeProcesses.remove(proc);
                }

            tempProcesses.clear();
            if (activeProcesses.size() + frozenProcesses.size() + doneProcesses.size() != processes.size()) {
                System.out.println();
            }

            for (Process currentProcess : activeProcesses)
            {
                Integer page = currentProcess.getNextReference();
                Memory currentMemory = processesAndTheirMemories.get(currentProcess);
                int counter = currentProcess.getReferencesCounter();

//                System.out.println(page + " " + counter);

                if (counter > 0 && counter % DELTA_PARAMETER == 0)
                {
                    HashSet<Integer> pagesUsedInHistory = new HashSet<>();

                    for (int id = counter-1; id >= counter - DELTA_PARAMETER; id--)
                        pagesUsedInHistory.add(currentProcess.getReference(id));

                    int requiredMemory = pagesUsedInHistory.size();
                    int actualMemory = currentMemory.getSize();

                    if (requiredMemory < actualMemory)
                    {
                        while (requiredMemory != actualMemory && !currentMemory.isFull() && currentMemory.getSize() > 0)
                        {
                            currentMemory.changeSize(-1);
                            globalMemory.changeSize(1);
                            actualMemory--;
                            flag = true;
                        }

                        while (requiredMemory != actualMemory && currentMemory.getSize() > 0)
                        {
                            Integer pageToDelete = currentProcess.getReference(LRU.getLeastRecentlyUsedPageID
                                    (counter, currentMemory, currentProcess.getAllReferences()));
                            currentMemory.removePage(pageToDelete);
                            currentMemory.changeSize(-1);
                            globalMemory.changeSize(1);
                            actualMemory--;
                            flag = true;
                        }
                    }
                    else if (requiredMemory > actualMemory)
                    {
                        if (requiredMemory - actualMemory > globalMemory.getSize())
                        {
                            currentProcess.setFrozenCapacity(currentMemory.getSize());
                            tempProcesses.add(currentProcess);
                            globalMemory.changeSize(currentMemory.getSize());
                            frozenProcesses.add(currentProcess);
                            freezeCounter++;
                        }
                        else
                        {
                            currentMemory.changeSize(requiredMemory - actualMemory);
                            globalMemory.changeSize(-(requiredMemory - actualMemory));
                        }
                    }
                }

                if (LRU.referToThePage(counter-1, currentMemory, currentProcess.getAllReferences()))
                {
                    pageFaults++;
                    currentProcess.increasePageFaults();
                }

//                for (Process proc : activeProcesses)
//                    System.out.println(processesAndTheirMemories.get(proc).getPages());

                if (currentProcess.getActiveReferences().size() == 0)
                {
                    doneProcesses.add(currentProcess);
                    tempProcesses.add(currentProcess);
                    flag = true;
                }

//                System.out.println();
            }
        }

//        System.out.println(freezeCounter);
        return pageFaults;
    }
}