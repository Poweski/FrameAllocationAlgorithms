package MyObjects;

import java.util.ArrayList;
import java.util.Set;

public class Process {

    private final Set<Integer> setOfPages;
    private final ArrayList<Integer> allReferences;
    private final ArrayList<Integer> activeReferences;
    private int pageFaults = 0;
    private int counter = 0;
    private int frozenCapacity = 0;

    public Process (Set<Integer> setOfPages, ArrayList<Integer> allReferences) {
        this.setOfPages = setOfPages;
        this.allReferences = allReferences;
        activeReferences = new ArrayList<>(allReferences);
    }

    public ArrayList<Integer> getAllReferences() {
        return allReferences;
    }

    public ArrayList<Integer> getActiveReferences() {
        return activeReferences;
    }

    public Set<Integer> getSetOfPages() {
        return setOfPages;
    }

    public Integer getNextReference() {
        this.increaseReferencesCounter();
        if (activeReferences.size() > 0)
            return activeReferences.remove(0);
        return -1;
    }

    public Integer getReference(int id) {
        return allReferences.get(id);
    }

    public void increaseReferencesCounter() {
        counter++;
    }

    public int getReferencesCounter() {
        return counter;
    }

    public void setFrozenCapacity(int frozenCapacity) {
        this.frozenCapacity = frozenCapacity;
    }

    public int getFrozenCapacity() {
        return frozenCapacity;
    }

    public void increasePageFaults() {
        pageFaults++;
    }

    public int getPageFaults() {
        return pageFaults;
    }
}
