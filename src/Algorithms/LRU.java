package Algorithms;

import MyObjects.Memory;

import java.util.ArrayList;

public class LRU {

    public static boolean referToThePage
            (int actualReferenceID, Memory localMemory, ArrayList<Integer> references) {

        if (!localMemory.contains(references.get(actualReferenceID))) {
            if (!localMemory.isFull()) {
                localMemory.addPage(references.get(actualReferenceID));
            }
            else {
                int localMemoryLeastUsedID = getLeastRecentlyUsedPageID(actualReferenceID, localMemory, references);

                if (localMemory.removePage(references.get(localMemoryLeastUsedID))) {
                    localMemory.addPage(references.get(actualReferenceID));
                }
            }
            return true;
        }

        return false;
    }

    public static Integer getLeastRecentlyUsedPageID
            (int actualReferenceID, Memory memory, ArrayList<Integer> references) {

        int leastRecentlyID = actualReferenceID;

        for (Integer page : memory.getPages()) {

            int pageReferenceID = actualReferenceID-1;

            while (pageReferenceID >= 0 && !references.get(pageReferenceID).equals(page)) {
                pageReferenceID--;
            }
            if (pageReferenceID < leastRecentlyID) {
                leastRecentlyID = pageReferenceID;
            }
        }

        return leastRecentlyID;
    }
}