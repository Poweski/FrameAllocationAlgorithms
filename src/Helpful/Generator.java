package Helpful;

import MyObjects.Process;

import java.util.*;

public class Generator {

    public static ArrayList<Integer> generateReferenceString
            (int PROCESS_REFERENCE_STRING_LENGTH, Set<Integer> pageSet, int STRING_CONSTANT, int SEED) {

        ArrayList<Integer> availablePagesList = new ArrayList<>(pageSet);
        ArrayList<Integer> references = new ArrayList<>();
        Random generator = new Random(SEED);
        int temporary = -1;

        for (int i = 0; i < PROCESS_REFERENCE_STRING_LENGTH/STRING_CONSTANT; i++) {

            temporary = generator.nextInt(pageSet.size());

            for (int j = 0; j < STRING_CONSTANT; j++) {
                references.add(availablePagesList.get(Math.min(pageSet.size() - 1,
                        temporary + (int) (generator.nextExponential()))));
            }
        }
        for (int i = (PROCESS_REFERENCE_STRING_LENGTH/STRING_CONSTANT)*STRING_CONSTANT; i < PROCESS_REFERENCE_STRING_LENGTH; i++) {
            references.add(availablePagesList.get(Math.min(pageSet.size()-1,
                        temporary + (int)(generator.nextExponential()))));
        }

        return references;
    }

    public static ArrayList<Process> generateProcesses
            (int SEED, int NUMBER_OF_PROCESSES, int MIN_PAGE_PER_PROCESS_NUMBER, int MAX_PAGE_PER_PROCESS_NUMBER,
             int PROCESS_REFERENCE_STRING_LENGTH, int STRING_CONSTANT) {

        ArrayList<Process> processes = new ArrayList<>();
        Random rng = new Random(SEED);
        int pageID = 0;

        for (int processID = 0; processID < NUMBER_OF_PROCESSES; processID++) {

            int numberOfPages = rng.nextInt(MIN_PAGE_PER_PROCESS_NUMBER, MAX_PAGE_PER_PROCESS_NUMBER+1);
            HashSet<Integer> pageSet = new HashSet<>();

            for (int newPageID = pageID; newPageID < (pageID + numberOfPages); newPageID++) {
                pageSet.add(newPageID);
            }

            pageID += numberOfPages;

            ArrayList<Integer> references = Generator.generateReferenceString
                    (PROCESS_REFERENCE_STRING_LENGTH, pageSet, STRING_CONSTANT, SEED);

            processes.add(new Process(pageSet, references));
        }

        return processes;
    }

    public static ArrayList<Process> copyProcessList(ArrayList<Process> originalList) {

        ArrayList<Process> copy = new ArrayList<>();

        for (Process process : originalList) {
            copy.add(new Process(new HashSet<>(process.getSetOfPages()), new ArrayList<>(process.getAllReferences())));
        }

        return copy;
    }
}