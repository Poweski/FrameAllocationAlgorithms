package Algorithms;

import MyObjects.Memory;
import MyObjects.Process;
import com.sun.tools.javac.Main;

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

        for (Process proc : processes)
            processesAndTheirPFFs.put(proc, new ArrayList<>());

        globalMemory = new Memory(0);
    }

    public int run() {

        boolean flag = false;

        while (doneProcesses.size() < processes.size())
        {
            if (flag && frozenProcesses.size() > 0)
                for (Process frozenProcess : frozenProcesses)
                    if (frozenProcess.getFrozenCapacity() <= globalMemory.getSize())
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

            for (Process currentProcess : activeProcesses)
            {
                Integer page = currentProcess.getNextReference();
                Memory currentMemory = processesAndTheirMemories.get(currentProcess);
                ArrayList<Integer> pffList = processesAndTheirPFFs.get(currentProcess);
                int counter = currentProcess.getReferencesCounter();

//                System.out.println(page);

                if (LRU.referToThePage(counter-1, currentMemory, currentProcess.getAllReferences()))
                {
                    pageFaults++;
                    currentProcess.increasePageFaults();
                    pffList.add(1);
                }
                else
                    pffList.add(0);

                if (counter > 0 && counter % T == 0)
                {
                    int pff = 0;

                    for (int i = 0; i < T; i++)
                        pff += pffList.get(i);

                    if (pff < minLim && currentMemory.getSize() > 1)
                    {
                        Integer pageToDelete = currentProcess.getReference(LRU.getLeastRecentlyUsedPageID
                                (counter, currentMemory, currentProcess.getAllReferences()));
                        currentMemory.removePage(pageToDelete);
                        currentMemory.changeSize(-1);
                        globalMemory.changeSize(1);
//                        flag = true;
                    }
                    else if (pff > maxLim)
                    {
                        if (globalMemory.getSize() > 0)
                        {
                            globalMemory.changeSize(-1);
                            currentMemory.changeSize(1);
                        }
                        else if (activeProcesses.size() > 1)
                        {
                            currentProcess.setFrozenCapacity(currentMemory.getSize() + 1);
                            tempProcesses.add(currentProcess);
                            globalMemory.changeSize(currentMemory.getSize());
//                            currentMemory.clear();
                            frozenProcesses.add(currentProcess);
                        }
                    }

                    pffList.clear();
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

        return pageFaults;
    }
}
